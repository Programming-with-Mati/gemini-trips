package com.programmingwithmati.geminitrips.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.programmingwithmati.geminitrips.model.CityItinerary;
import com.programmingwithmati.geminitrips.model.ItineraryParams;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ItineraryService {

  public static final String PROMPT = "Genera un itinerario de \"%d\" días en la ciudad de \"%s\".\nEl formato del itinerario debe ser JSON.\nEl JSON tiene el siguiente JSON Schema:\n\n{\n  \"$schema\": \"http://json-schema.org/draft-07/schema#\",\n  \"title\": \"City Itinerary\",\n  \"type\": \"object\",\n  \"properties\": {\n    \"title\": {\n      \"type\": \"string\",\n      \"description\": \"Creative title for the itinerary\"\n    },\n    \"description\": {\n      \"type\": \"string\",\n      \"description\": \"Description of the itinerary\"\n    },\n    \"days\": {\n      \"type\": \"array\",\n      \"items\": {\n        \"type\": \"object\",\n        \"properties\": {\n          \"day\": {\n            \"type\": \"integer\",\n            \"minimum\": 1,\n            \"description\": \"Day number\"\n          },\n          \"activities\": {\n            \"type\": \"array\",\n            \"items\": {\n              \"type\": \"object\",\n              \"description\": \"Activity for the day\",\n              \"properties\": {\n                \"name\": {\n                  \"type\": \"string\",\n                  \"description\": \"Name of the activity\"\n                },\n                \"description\": {\n                  \"type\": \"string\",\n                  \"description\": \"200 character description of the activity\"\n                },\n                \"estimatedDuration\": {\n                  \"type\": \"string\",\n                  \"description\": \"How much time the activity takes, in minutes.\"\n                }\n                \n              }\n            }\n          }\n        },\n        \"required\": [\"day\", \"activities\"]\n      }\n    },\n    \"hotels\": {\n      \"type\": \"array\",\n      \"items\": {\n        \"type\": \"object\",\n        \"properties\": {\n          \"name\": {\n            \"type\": \"string\",\n            \"description\": \"Name of the hotel\"\n          },\n          \"starRating\": {\n            \"type\": \"number\",\n            \"minimum\": 1,\n            \"maximum\": 5,\n            \"description\": \"Star rating of the hotel\"\n          },\n          \"description\": {\n            \"type\": \"string\",\n            \"description\": \"Description of the hotel\"\n          },\n          \"price\": {\n            \"type\": \"number\",\n            \"description\": \"price of the hotel\"\n          }\n        },\n        \"required\": [\"name\", \"starRating\"]\n      }\n    }\n  },\n  \"required\": [\"title\", \"description\", \"days\", \"hotels\"]\n}\n\n\nAunque el json está en inglés, devuelve la información en español. El campo \"hotels\" no puede estar vacío. Debe tener 2 o 3 hoteles.\n\nSolo devuelve el JSON. Ningún texto a parte de eso, ni siquiera markdown.";
  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private final GeminiService geminiService;

  private final Map<ItineraryParams, CityItinerary> cache = new ConcurrentHashMap<>();

  public ItineraryService(GeminiService geminiService) {
    this.geminiService = geminiService;
  }

  public CityItinerary generateCityItinerary(ItineraryParams itineraryParams) {
    var params = itineraryParams.adjustDays();

    if (cache.containsKey(params)) return cache.get(params);

    var stringItinerary = geminiService.getResponse(PROMPT.formatted(params.amountOfDays(), params.city()))
            .replace("```json", "")
            .replace("```", "");

    try {
      var cityItinerary = OBJECT_MAPPER.readValue(stringItinerary, CityItinerary.class);
      cache.put(params, cityItinerary);
      return cityItinerary;
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error parsing JSON Itinerary", e);
    }
  }
}
