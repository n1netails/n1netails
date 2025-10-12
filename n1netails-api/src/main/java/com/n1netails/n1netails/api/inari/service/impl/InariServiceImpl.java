package com.n1netails.n1netails.api.inari.service.impl;

import com.n1netails.n1netails.api.inari.service.AiAgentService;
import com.n1netails.n1netails.api.inari.service.GitHubService;
import com.n1netails.n1netails.api.inari.service.InariService;
import com.n1netails.n1netails.api.model.dto.Note;
import com.n1netails.n1netails.api.model.response.TailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("inariService")
public class InariServiceImpl implements InariService {

    @Autowired
    private GitHubService githubService;

    @Autowired
    private AiAgentService aiService;

    public void handleTailAlert(String owner, String repository, String branch, TailResponse tailResponse) throws Exception {

        // TODO get notes for tail response

        // 0. Fetch repo tree from GitHub
        log.info("0. Fetch repo tree from GitHub");
        List<String> filePaths = githubService.getRepoFileTree(owner, repository, branch);
        log.info("repo tree: {}", filePaths);

        // 1. Ask AI which files to change
        log.info("1. Ask AI which files to change");
        List<String> files = aiService.identifyFiles(tailResponse.getDetails(), filePaths);
        log.info("list identified files: {}", files);

        // 2. Fetch files from GitHub
        log.info("2. Fetch files from GitHub");
        Map<String, String> fileContents = new HashMap<>();
        for (String file : files) {
            String content = githubService.getFileContents(owner, repository, file, branch);

            log.info("FOR LOOP FILE: {}", file);
            log.info("FOR LOOP FILE CONTENT: {}", content);

            fileContents.put(file, content);
        }

        // 3. Ask AI to generate fixes
        log.info("3. Ask AI to generate fixes");
        Map<String, String> fixedFiles = aiService.generateFix(tailResponse.getDetails(), fileContents);
        log.info("FIXED FILES: {}", fixedFiles);

        // 4. Commit and PR
        log.info("4. Commit and PR");
//        String branchName = "fix-tail-alert-" + alert.getId();
        String inariBranchName = "n1netails-inari/fix-tail-alert-" + tailResponse.getId() + "-" + System.currentTimeMillis();
        log.info("BRANCH NAME: {}", inariBranchName);
        githubService.createBranch(owner, repository, inariBranchName);

        for (Map.Entry<String, String> entry : fixedFiles.entrySet()) {
            log.info("commit file: {}", entry.getKey());
            githubService.commitFile(owner, repository, inariBranchName, entry.getKey(), entry.getValue(),
                    "Fix bug from tail alert " + tailResponse.getId());
        }

        log.info("creating pull request");
        githubService.createPullRequest(owner, repository, inariBranchName, branch,
                "Fix tail alert " + tailResponse.getId(),
                "Automated fix for stack trace:\n```\n" + tailResponse.getDetails() + "\n```");
    }
}
