package com.n1netails.n1netails.api.inari.service;

import java.util.List;
import java.util.Map;

public interface AiAgentService {

    List<String> identifyFiles(String stackTrace, List<String> filePaths) throws Exception;
    Map<String, String> generateFix(String stackTrace, Map<String, String> fileContents) throws Exception;
}
