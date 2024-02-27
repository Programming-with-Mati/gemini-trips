package com.programmingwithmati.geminitrips.model;

public record ItineraryParams(
        String city,
        Integer amountOfDays
) {
  public ItineraryParams adjustDays() {
    return new ItineraryParams(city, amountOfDays > 2 ? 2 : amountOfDays);
  }
}
