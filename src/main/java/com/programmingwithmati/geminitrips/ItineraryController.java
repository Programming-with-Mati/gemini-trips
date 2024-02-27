package com.programmingwithmati.geminitrips;

import com.programmingwithmati.geminitrips.model.ItineraryParams;
import com.programmingwithmati.geminitrips.service.ItineraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/itinerary")
public class ItineraryController {

  private final ItineraryService itineraryService;

  public ItineraryController(ItineraryService itineraryService) {
    this.itineraryService = itineraryService;
  }

  @GetMapping
  public String viewItineraryRequest() {
    return "create-itinerary";
  }

  @PostMapping("/generate")
  public String generateItinerary(@RequestParam("city")String city,
                                  @RequestParam("amountOfDays")Integer amountOfDays,
                                  Model model) {
    var itinerary = itineraryService.generateCityItinerary(new ItineraryParams(city, amountOfDays));
    model.addAttribute("itinerary", itinerary);
    return "itinerary";
  }

}
