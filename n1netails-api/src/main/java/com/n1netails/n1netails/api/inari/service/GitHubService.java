package com.n1netails.n1netails.api.inari.service;

import java.util.List;

public interface GitHubService {

    List<String> getRepoFileTree(String owner, String repo, String branch) throws Exception;
    String getFileContents(String owner, String repo, String filePath, String branch) throws Exception;
    void createBranch(String owner, String repo, String branchName) throws Exception;
    void commitFile(String owner, String repo, String branchName, String filePath, String contentBase64, String message) throws Exception;
    void createPullRequest(String owner, String repo, String branchName, String baseBranch, String title, String body) throws Exception;
}
