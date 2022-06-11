package tourGuide;

import java.util.List;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jsoniter.output.JsonStream;
import tourGuide.gpsUtil.VisitedLocation;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tripPricer.Provider;

@RestController
public class TourGuideController {
	@Autowired
	TourGuideService tourGuideService;
	
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }
    
    @RequestMapping("/getLocation") 
    public String getLocation(@RequestParam String userName) {
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
		return JsonStream.serialize(visitedLocation.location);
    }
    /**
     * this api returns 5 attractions closest to a user with some of it details
     * @param userName
     * @return Name of Tourist attraction, Tourist attractions lat/long,The user's location lat/long,
     * The distance in miles between the user's location and each of the attractions.
     * The reward points for visiting each Attraction.
     */
    @RequestMapping("/getNearbyAttractions") 
    public String getNearbyAttractions(@RequestParam String userName) {
    	User user = getUser(userName);
    	VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
    	return JsonStream.serialize(tourGuideService.getEachAttractionDetailsForAUser(visitedLocation,user));
    }
    
    @RequestMapping("/getRewards") 
    public String getRewards(@RequestParam String userName) {
    	return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }
    
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations()  {
    	// Gets a list of every user's most recent location as JSON
    	//- Note: does not use gpsUtil to query for their current location, 
    	//        but rather gathers the user's current location from their stored location history.
    	//
    	// Return object should be the just a JSON mapping of userId to Locations similar to:
    	//     {
    	//        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371} 
    	//        ...
    	//     }
    	
    	
    	return JsonStream.serialize(tourGuideService.locationHistoryOfUsers());
    }
    
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
    	List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
    	return JsonStream.serialize(providers);
    }
    
    @RequestMapping("/getUserPreference")
    public String getUserPreference(@RequestParam String userName) {
        User user = getUser(userName);
        return JsonStream.serialize(user.getUserPreferences());
    }

    @RequestMapping("/setUserPreference")
    public void setUserPreference(@RequestParam String userName, @RequestParam int attractionProximity, @RequestParam Money lowerPricePoint,
                                  @RequestParam Money highPricePoint, @RequestParam int tripDuration,
                                  @RequestParam int ticketQuantity, @RequestParam int numberOfAdults,
                                  @RequestParam int numberOfChildren) {
        User user = getUser(userName);
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setAttractionProximity(attractionProximity);
        userPreferences.setLowerPricePoint(lowerPricePoint);
        userPreferences.setHighPricePoint(highPricePoint);
        userPreferences.setTripDuration(tripDuration);
        userPreferences.setTicketQuantity(ticketQuantity);
        userPreferences.setNumberOfAdults(numberOfAdults);
        userPreferences.setNumberOfChildren(numberOfChildren);
        user.setUserPreferences(userPreferences);
    }

    
    private User getUser(String userName) {
    	return tourGuideService.getUser(userName);
    }
   

}