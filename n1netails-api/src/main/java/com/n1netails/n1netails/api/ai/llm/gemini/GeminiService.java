package com.n1netails.n1netails.api.ai.llm.gemini;

import com.n1netails.n1netails.api.ai.llm.LlmService;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService implements LlmService {

  // Define these properties in application-ai.yml
  // gemini.api.key=YOUR_GEMINI_API_KEY
  // gemini.api.url=YOUR_GEMINI_API_URL
  @Value("${gemini.api.key}")
  private String apiKey;

  @Value("${gemini.api.url}")
  private String apiUrl;

  @Override
  public String completePrompt(String prompt) {
    HttpClient client = HttpClient.newHttpClient();
    // TODO: Update the request body to match the Gemini API specification
    String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}";

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(apiUrl + "?key=" + apiKey))
        .header("Content-Type", "application/json")
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
      return "Error: Failed to call Gemini API - " + e.getMessage();
    }
  }
}
