package com.research.assistant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.research.assistant.dto.GeminiResponse;
import com.research.assistant.dto.ResearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ResearchService {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public ResearchService(ObjectMapper objectMapper , WebClient.Builder webClientBuilder) {
        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.build();
    }

    public String processContent(ResearchRequest request) {

        String prompt = buildPrompt(request);

        GeminiRequest geminiRequest = new GeminiRequest(
                List.of(new Content(List.of(new Part(prompt))))
        );

//        String geminiResponse = webClient.post()
//                .uri(geminiApiUrl + geminiApiKey)
//                .header("Content-Type", "application/json")
//                .bodyValue(geminiRequest)
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();

        String geminiResponse = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue(geminiRequest)
                .retrieve()
                .onStatus(
                        status -> status.value() == 429,
                        response -> Mono.error(new RuntimeException("Gemini API quota exceeded. Please wait 30 seconds."))
                )
                .bodyToMono(String.class)
                .block();


        return extractTextFromResponse(geminiResponse);
    }

    private String extractTextFromResponse(String response) {

        try
        {
            GeminiResponse geminiResponse = objectMapper
                    .readValue(response, GeminiResponse.class);

            if(geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()) {
                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);

                if(firstCandidate.getContent() != null && firstCandidate.getContent().getParts() != null && !firstCandidate.getContent().getParts().get(0).getText().isEmpty())
                {
                    return firstCandidate.getContent().getParts().get(0).getText();
                }
            }
        return "No content found in response";
        }

        catch (Exception e)
        {
            return "Error Parsing: " +e.getMessage();
        }

    }

    private String buildPrompt(ResearchRequest request) {
        StringBuilder prompt = new StringBuilder();
        switch (request.getOperation()) {
            case "summarize" ->
                    prompt.append("Provide a clear and concise summary of the following text in few lines.\n\n");
            case "suggest" ->
                    prompt.append("Based on the following content: suggest related topics with headings and bullet points.\n\n");
            default -> throw new IllegalArgumentException("Unknown operation: " + request.getOperation());
        }
        prompt.append(request.getContent());
        return prompt.toString();
    }

    record Part(String text) {}
    record Content(List<Part> parts) {}
    record GeminiRequest(List<Content> contents) {}
}
