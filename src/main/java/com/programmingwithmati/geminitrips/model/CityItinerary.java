package com.programmingwithmati.geminitrips.model;

import java.util.List;

public record CityItinerary(
        String title,
        String description,
        List<Day> days,
        List<Hotel> hotels
) {
  public record Day(
          int day,
          List<Activity> activities
  ) {}

  public record Activity(
          String name,
          String description,
          String estimatedDuration
  ) {}

  public record Hotel(
          String name,
          double starRating,
          String description,
          double price
  ) {}
}
