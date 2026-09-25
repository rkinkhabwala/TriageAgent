package org.example.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

/**
 * HTTP-based implementation of {@link LlmClient} using Spring WebClient.
 * Calls an OpenAI-compatible chat completions endpoint.
 */
@Component
public class HttpLlmClient implements LlmClient {

    private final WebClient webClient;
    private final String model;

    /**
     * Builds the WebClient with the configured base URL, API key, and model.
     *
     * @param builder Spring WebClient builder
     * @param baseUrl LLM provider base URL
     * @param apiKey  LLM provider API key
     * @param model   model name to use for completions
     */
    public HttpLlmClient(
            WebClient.Builder builder,
            @Value("${llm.baseUrl}") String baseUrl,
            @Value("${llm.apiKey}") String apiKey,
            @Value("${llm.model}") String model
    ) {
        this.model = model;

        this.webClient = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey.trim())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * Sends a prompt to the LLM and returns the generated text.
     *
     * @param prompt the prompt text
     * @return the LLM completion text
     * @throws IllegalArgumentException   if the prompt is empty
     * @throws WebClientResponseException on HTTP errors
     */
    @Override
    public String complete(String prompt) {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Prompt is empty");
        }

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt)),
                "temperature", 0.2
        );

        try {
            Map<?, ?> resp = webClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return extractText(resp);

        } catch (WebClientResponseException e) {
            System.out.println("OpenAI status: " + e.getStatusCode());
            System.out.println("OpenAI error body: " + e.getResponseBodyAsString());
            throw e;
        }
    }

    /**
     * Extracts the assistant message content from the chat completions response.
     *
     * @param resp parsed JSON response map
     * @return the generated message content
     */
    @SuppressWarnings("unchecked")
    private String extractText(Map<?, ?> resp) {
        var choices = (List<Map<String, Object>>) resp.get("choices");
        var message = (Map<String, Object>) choices.get(0).get("message");
        return message.get("content").toString();
    }
}

