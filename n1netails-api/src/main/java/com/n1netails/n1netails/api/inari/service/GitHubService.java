package com.n1netails.n1netails.api.inari.service;

import java.util.List;

public interface GitHubService {
    void saveInstallationId(String installationId, Long organizationId) throws Exception;

    void checkAppAuth() throws Exception;
    List<String> listRepositories(Long organizationId) throws Exception;
    List<String> listBranches(Long organizationId, String owner, String repo) throws Exception;

    List<String> getRepoFileTree(Long organizationId, String owner, String repo, String branch) throws Exception;
    String getFileContents(Long organizationId, String owner, String repo, String filePath, String branch) throws Exception;
    void createBranch(Long organizationId, String owner, String repo, String branchName) throws Exception;
    void commitFile(Long organizationId, String owner, String repo, String branchName, String filePath, String contentBase64, String message) throws Exception;
    void createPullRequest(Long organizationId, String owner, String repo, String branchName, String baseBranch, String title, String body) throws Exception;
}
