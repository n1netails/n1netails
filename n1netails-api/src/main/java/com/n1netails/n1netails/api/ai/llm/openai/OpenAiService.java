package com.n1netails.n1netails.api.ai.llm.openai;

import com.n1netails.n1netails.api.ai.llm.LlmService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService implements LlmService {

  // Define these properties in application-ai.yml
  // openai.api.key=YOUR_OPENAI_API_KEY
  // openai.api.url=YOUR_OPENAI_API_URL
  @Value("${openai.api.key}")
  private String apiKey;

  @Value("${openai.api.url}")
  private String apiUrl;

  @Override
  public String completePrompt(String prompt) {
    HttpClient client = HttpClient.newHttpClient();
    // TODO: Update the request body to match the OpenAI API specification
    // This is a placeholder and needs to be adjusted based on the specific OpenAI model and API version
    String requestBody = "{\"prompt\":\"" + prompt + "\",\"max_tokens\":150}";

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(apiUrl)) // OpenAI URL might not need the key in the query param
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + apiKey) // OpenAI typically uses Bearer token
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() == 200) {
        // TODO: Implement more sophisticated JSON parsing if needed
        return response.body();
      } else {
        // Handle API errors
        return "Error: Received status code " + response.statusCode() + " - " + response.body();
      }
    } catch (Exception e) {
      // Handle network or other errors
      return "Error: Failed to call OpenAI API - " + e.getMessage();
    }
  }
}
