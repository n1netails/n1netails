package com.n1netails.n1netails.api.inari.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.n1netails.n1netails.api.exception.type.OrganizationNotFoundException;
import com.n1netails.n1netails.api.inari.service.GitHubService;
import com.n1netails.n1netails.api.model.entity.GitHubInstallationEntity;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.repository.GitHubInstallationRepository;
import com.n1netails.n1netails.api.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("gitHubService")
public class GitHubServiceImpl implements GitHubService {

    private final GitHubInstallationRepository gitHubInstallationRepository;
    private final OrganizationRepository organizationRepository;

    private final String appId = "1787765"; // GitHub App ID
    // TODO FIGURE OUT HOW TO LOAD IN PEM KEY SECURELY
    private final String pemFilePath = "D:/N1NE_TAILS/GITHUB_APP/n1netails-local.pem";

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void saveInstallationId(String installationId, Long organizationId) throws OrganizationNotFoundException {
        OrganizationEntity organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));

        GitHubInstallationEntity installation = gitHubInstallationRepository.findByOrganization(organization)
                .orElse(new GitHubInstallationEntity());

        installation.setOrganization(organization);
        installation.setInstallationId(installationId);
        gitHubInstallationRepository.save(installation);
    }

    // Step 1: Generate JWT for the GitHub App
    private String generateJwt() throws Exception {
        byte[] keyBytes = Files.readAllBytes(Paths.get(pemFilePath));

//        System.out.println("PEM file contents: " + new String(keyBytes));

        String privateKeyContent = new String(keyBytes)
                .replaceAll("-----BEGIN (.*)-----", "")
                .replaceAll("-----END (.*)-----", "")
                .replaceAll("\\s+", "");

//        System.out.println("privateKeyContent: " + privateKeyContent);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
        PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        Algorithm algorithm = Algorithm.RSA256(null, (java.security.interfaces.RSAPrivateKey) privateKey);

        return JWT.create()
                .withIssuer(appId)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(540))) // max 10 mins
                .sign(algorithm);
    }

    // Step 2: Get Installation Access Token
    private String getInstallationToken(Long organizationId) throws Exception {
        String jwt = generateJwt();

        DecodedJWT decoded = JWT.decode(jwt);
        log.info("iss: {}, iat: {}, exp: {}", decoded.getIssuer(), decoded.getIssuedAt(), decoded.getExpiresAt());

        OrganizationEntity organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new OrganizationNotFoundException("Organization not found"));
        GitHubInstallationEntity installation = gitHubInstallationRepository.findByOrganization(organization)
                .orElseThrow(() -> new RuntimeException("GitHub App not installed for this organization"));
        String installationId = installation.getInstallationId();


        String url = "https://api.github.com/app/installations/" + installationId + "/access_tokens";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.set("Accept", "application/vnd.github+json");
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        return response.getBody().get("token").toString();
    }

    private HttpHeaders authHeaders(Long organizationId) throws Exception {
        String token = getInstallationToken(organizationId);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.set("Accept", "application/vnd.github+json");
        return headers;
    }


    public void checkAppAuth() throws Exception {
        String jwt = generateJwt();

        String url = "https://api.github.com/app/installations";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwt);
        headers.set("Accept", "application/vnd.github+json");

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        log.info("Status: {}", response.getStatusCode());
        log.info("Body: {}", response.getBody());
    }


    // == repository controls ===
//    @Override
//    public List<String> listRepositories() throws Exception {
//        log.info("listRepositories");
//        HttpHeaders headers = authHeaders();
//
//        String url = "https://api.github.com/installation/repositories";
//
//        ResponseEntity<Map> response = restTemplate.exchange(
//                url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
//
//        log.info("RESPONSE: {}", response);
//
//        List<Map<String, Object>> repos = (List<Map<String, Object>>) response.getBody().get("repositories");
//        log.info("REPOS: {}", repos);
//
//        List<String> repoNames = new ArrayList<>();
//        for (Map<String, Object> repo : repos) {
//            repoNames.add(repo.get("full_name").toString()); // owner/repo format
//        }
//
//        return repoNames;
//    }

    @Override
    public List<String> listRepositories(Long organizationId) throws Exception {
        log.info("listRepositories");
        HttpHeaders headers = authHeaders(organizationId);

        String url = "https://api.github.com/installation/repositories?per_page=100";
        List<String> repoNames = new ArrayList<>();

        while (url != null) {
            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            List<Map<String, Object>> repos =
                    (List<Map<String, Object>>) response.getBody().get("repositories");

            for (Map<String, Object> repo : repos) {
                repoNames.add(repo.get("full_name").toString());
            }

            // Look for "Link" header to see if there's a "next"
            List<String> linkHeaders = response.getHeaders().get("Link");
            url = null;
            if (linkHeaders != null) {
                for (String linkHeader : linkHeaders) {
                    for (String part : linkHeader.split(",")) {
                        if (part.contains("rel=\"next\"")) {
                            url = part.substring(part.indexOf("<") + 1, part.indexOf(">"));
                            break;
                        }
                    }
                }
            }
        }

        return repoNames;
    }

    @Override
    public List<String> listBranches(Long organizationId, String owner, String repo) throws Exception {
        log.info("listBranches");
        HttpHeaders headers = authHeaders(organizationId);

        String url = String.format("https://api.github.com/repos/%s/%s/branches", owner, repo);

        ResponseEntity<List> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), List.class);

        List<Map<String, Object>> branches = (List<Map<String, Object>>) response.getBody();

        List<String> branchNames = new ArrayList<>();
        for (Map<String, Object> branch : branches) {
            branchNames.add(branch.get("name").toString());
        }

        return branchNames;
    }




    // === CORE METHODS ===

    @Override
    public List<String> getRepoFileTree(Long organizationId, String owner, String repo, String branch) throws Exception {
        log.info("getRepoFileTree");
        HttpHeaders headers = authHeaders(organizationId);

        String url = String.format("https://api.github.com/repos/%s/%s/git/trees/%s?recursive=1",
                owner, repo, branch);

        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        List<Map<String, Object>> tree = (List<Map<String, Object>>) response.getBody().get("tree");

        List<String> filePaths = new ArrayList<>();
        for (Map<String, Object> item : tree) {
            if ("blob".equals(item.get("type"))) { // only files, not directories
                filePaths.add(item.get("path").toString());
            }
        }

        return filePaths;
    }

    @Override
    public String getFileContents(Long organizationId, String owner, String repo, String filePath, String branch) throws Exception {
        log.info("getFileContents");
        HttpHeaders headers = authHeaders(organizationId);

        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s?ref=%s",
                owner, repo, filePath, branch);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        return response.getBody().get("content").toString(); // base64 encoded
    }

    @Override
    public void createBranch(Long organizationId, String owner, String repo, String branchName) throws Exception {
        log.info("createBranch");
        HttpHeaders headers = authHeaders(organizationId);

        // Get latest main SHA
        ResponseEntity<Map> mainRefResponse = restTemplate.exchange(
                String.format("https://api.github.com/repos/%s/%s/git/ref/heads/main", owner, repo),
                HttpMethod.GET, new HttpEntity<>(headers), Map.class);

        String mainSha = ((Map)((Map)mainRefResponse.getBody().get("object"))).get("sha").toString();

        // Create branch
        Map<String, Object> branchBody = new HashMap<>();
        branchBody.put("ref", "refs/heads/" + branchName);
        branchBody.put("sha", mainSha);

        restTemplate.exchange(
                String.format("https://api.github.com/repos/%s/%s/git/refs", owner, repo),
                HttpMethod.POST, new HttpEntity<>(branchBody, headers), String.class);
    }

    @Override
    public void commitFile(Long organizationId, String owner, String repo, String branchName,
                           String filePath, String contentBase64, String message) throws Exception {
        log.info("commitFile");

        HttpHeaders headers = authHeaders(organizationId);
        Map<String, Object> fileBody = new HashMap<>();
        fileBody.put("message", message);
        fileBody.put("content", contentBase64);
        fileBody.put("branch", branchName);

        // Check if file exists to get the SHA
        String url = String.format("https://api.github.com/repos/%s/%s/contents/%s?ref=%s",
                owner, repo, filePath, branchName);
        try {
            Map<?, ?> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class)
                    .getBody();
            if (response != null && response.containsKey("sha")) {
                String sha = response.get("sha").toString();
                fileBody.put("sha", sha); // Required for updating existing file
            }
        } catch (HttpClientErrorException.NotFound ignored) {
            // File does not exist, will create a new one
        }

        restTemplate.exchange(
                String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, filePath),
                HttpMethod.PUT, new HttpEntity<>(fileBody, headers), String.class);
    }

    @Override
    public void createPullRequest(Long organizationId, String owner, String repo, String branchName,
                                  String baseBranch, String title, String body) throws Exception {
        log.info("createPullRequest");
        HttpHeaders headers = authHeaders(organizationId);

        Map<String, Object> prBody = new HashMap<>();
        prBody.put("title", title);
        prBody.put("head", branchName);
        prBody.put("base", baseBranch);
        prBody.put("body", body);

        restTemplate.exchange(
                String.format("https://api.github.com/repos/%s/%s/pulls", owner, repo),
                HttpMethod.POST, new HttpEntity<>(prBody, headers), String.class);
    }
}
