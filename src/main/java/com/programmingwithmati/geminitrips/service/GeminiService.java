package com.programmingwithmati.geminitrips.service;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.*;
import com.google.cloud.vertexai.generativeai.preview.GenerativeModel;
import com.google.cloud.vertexai.generativeai.preview.ResponseStream;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GeminiService {


  public String getResponse(String prompt) {
    try (VertexAI vertexAi = new VertexAI("globantminds", "us-central1");) {
      GenerationConfig generationConfig =
              GenerationConfig.newBuilder()
                      .setMaxOutputTokens(2048)
                      .setTemperature(0.9F)
                      .setTopP(1F)
                      .build();
      GenerativeModel model = new GenerativeModel("gemini-pro", generationConfig, vertexAi);
      List<SafetySetting> safetySettings = Arrays.asList(
              SafetySetting.newBuilder()
                      .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                      .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                      .build(),
              SafetySetting.newBuilder()
                      .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                      .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                      .build(),
              SafetySetting.newBuilder()
                      .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                      .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                      .build(),
              SafetySetting.newBuilder()
                      .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                      .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                      .build()
      );
      List<Content> contents = new ArrayList<>();
      contents.add(Content.newBuilder().setRole("user").addParts(Part.newBuilder().setText(prompt)).build());

      ResponseStream<GenerateContentResponse> responseStream = model.generateContentStream(contents, safetySettings);
      // Do something with the response
      return responseStream.stream()
              .flatMap(response -> response.getCandidatesList().stream())
              .flatMap(candidate -> candidate.getContent().getPartsList().stream())
              .map(Part::getText)
              .collect(Collectors.joining());
    } catch (IOException e) {
      throw new RuntimeException("Error generating itinerary", e);
    }

  }
}
