package com.n1netails.n1netails.api.inari.service.impl;

import com.n1netails.n1netails.api.inari.service.AiAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.sql.ast.tree.expression.Over;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("aiAgentService")
public class OpenAiAgentServiceImpl implements AiAgentService {

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    private final RestTemplate restTemplate = new RestTemplate();

    // 🔍 Step 1: Identify files that are relevant to the stack trace
    @Override
    public List<String> identifyFiles(String stackTrace, List<String> filePaths) throws Exception {
        log.info("identifyFiles");

        log.info("flatten file paths");
        String repoFileList = String.join("\n", filePaths);

        String prompt = "You are an assistant that analyzes error stack traces and finds the correct source files. " +
                "Given the following stack trace and repository file list, return a JSON array of file paths " +
                "from the repository that most likely need fixing. Only return file paths from the provided repo list. " +
                "Do not include explanations or additional text.\n\n" +
                "Stack trace:\n" + stackTrace + "\n\n" +
                "Repository files:\n" + repoFileList;

        String response = callOpenAI(prompt);
        log.info("OPENAI RESPONSE:: {}", response);

        // Expect something like: ["src/main/java/com/shahidfoy/s3_demo/service/impl/S3ServiceSubtleErrors.java"]
        return parseJsonArray(response);
    }

    // 🛠️ Step 2: Generate fixed versions of the files
    @Override
    public Map<String, String> generateFix(String stackTrace, Map<String, String> fileContents) throws Exception {
        log.info("generateFix");
        log.info("stackTrace: {}", stackTrace);
//        log.info("fileContents: {}", fileContents);
        Map<String, String> fixedFiles = new HashMap<>();

        for (Map.Entry<String, String> entry : fileContents.entrySet()) {
            String filePath = entry.getKey();
            String originalContent = new String(
                    Base64.getMimeDecoder().decode(entry.getValue()), StandardCharsets.UTF_8);

            String prompt = "A stack trace indicates a bug:\n" + stackTrace +
                    "\n\nThe file `" + filePath + "` contains the following code:\n" +
                    originalContent +
                    "\n\nFix the bug and return only the updated file contents. " +
                    "Do not include explanations, comments, or markdown formatting. " +
                    "Return the raw code, keeping the original programming language intact.";

            String updatedFile = callOpenAI(prompt);
            log.info("=================updated file====================");
            log.info(updatedFile);
            log.info("=================updated file end====================");
            fixedFiles.put(filePath, Base64.getEncoder().encodeToString(updatedFile.getBytes()));
        }

        return fixedFiles;
    }

    // === Utility methods ===

    private String callOpenAI(String prompt) throws Exception {
        log.info("callOpenAI prompt:: {}", prompt);
        String url = "https://api.openai.com/v1/chat/completions";

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4.1"); // ✅ pick stable model
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        body.put("temperature", 0);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer " + OPENAI_API_KEY);
        headers.set("Content-Type", "application/json");

        org.springframework.http.HttpEntity<Map<String, Object>> request =
                new org.springframework.http.HttpEntity<>(body, headers);

        Map<String, Object> response =
                restTemplate.postForObject(url, request, Map.class);

        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return message.get("content").toString().trim();
    }

    private List<String> parseJsonArray(String response) {
        log.info("parseJsonArray");
        response = response.trim();
        if (response.startsWith("[")) {
            response = response.substring(1, response.length() - 1);
        }
        String[] parts = response.split(",");
        List<String> files = new ArrayList<>();
        for (String part : parts) {
            files.add(part.replace("\"", "").trim());
        }
        return files;
    }
}
