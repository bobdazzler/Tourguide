package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.dto.AttractionDetails;
import tourGuide.dto.LocationHistory;
import tourGuide.gpsUtil.Attraction;
import tourGuide.gpsUtil.Location;
import tourGuide.gpsUtil.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
	private Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final GpsUtilService gpsUtilService;
	private final RewardsService rewardsService;
	private final TripPricer tripPricer = new TripPricer();
	public final Tracker tracker;
	boolean testMode = true;
	public TourGuideService(GpsUtilService gpsUtilService, RewardsService rewardsService) {
		this.gpsUtilService = gpsUtilService;
		this.rewardsService = rewardsService;

		if (testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);
		addShutDownHook();
	}
	/**
	 * 
	 * @param user
	 * @return user rewards
	 */

	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
/**
 * 
 * @param user
 * @return current user location
 */
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ? user.getLastVisitedLocation()
				: trackUserLocation(user);
		return visitedLocation;
	}
	/**
	 * 
	 * @param userName
	 * @return user details.
	 */
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	/**
	 * 
	 * @return list of all users 
	 */
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}
	
	/**
	 * 
	 * @param user
	 * add users to the internal user map
	 */
	public void addUser(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	public List<Provider> getTripDeals(User user) {
		int cumulatativeRewardPoints = user.getUserRewards().stream().mapToInt(i -> i.getRewardPoints()).sum();
		List<Provider> providers = tripPricer.getPrice(tripPricerApiKey, user.getUserId(),
				user.getUserPreferences().getNumberOfAdults(), user.getUserPreferences().getNumberOfChildren(),
				user.getUserPreferences().getTripDuration(), cumulatativeRewardPoints);
		user.setTripDeals(providers);
		return providers;
	}

	public VisitedLocation trackUserLocation(User user)  {
		 VisitedLocation visitedLocation = gpsUtilService.getUserLocation(user.getUserId());
	        user.addToVisitedLocations(visitedLocation);
	        rewardsService.calculateRewards(user);
	        return visitedLocation;
	}

	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		for (Attraction attraction : gpsUtilService.getAttractions()) {
			nearbyAttractions.add(attraction);
		}
		logger.info("attraction " + nearbyAttractions);
		return nearbyAttractions;
	}

	public HashMap<Attraction, Double> sortNearByAttractionsByDistance(VisitedLocation visitedLocation) {
		HashMap<Attraction, Double> nearbyAttractionsByMiles = new HashMap<Attraction, Double>();
		for (Attraction attraction : getNearByAttractions(visitedLocation)) {
			double distanceInMiles = rewardsService.getDistance(attraction, visitedLocation.location);
			nearbyAttractionsByMiles.put(attraction, distanceInMiles);
		}
		HashMap<Attraction, Double> soretednearbyAttractionsByMiles = nearbyAttractionsByMiles.entrySet().stream()
				.sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(oldValue, newValue) -> oldValue, LinkedHashMap::new));
		logger.info("getting attractions and distance " + nearbyAttractionsByMiles);
		logger.info("sorted attractions by distance " + soretednearbyAttractionsByMiles);
		return soretednearbyAttractionsByMiles;
	}

	public List<Attraction> get5ClosestAttractionsByMiles(VisitedLocation visitedLocation) {
		HashMap<Attraction, Double> closestAttractionsByMiles = sortNearByAttractionsByDistance(visitedLocation);
		List<Attraction> listOfAttractionsSorted = new ArrayList<Attraction>(closestAttractionsByMiles.keySet());
		List<Attraction> closest5Attractions = listOfAttractionsSorted.subList(0, 5);
		for (Attraction attraction : closest5Attractions) {
			logger.info("attractions sorted names " + attraction.attractionName);
		}
		return closest5Attractions;
	}

	public List<AttractionDetails> getEachAttractionDetailsForAUser(VisitedLocation visitedLocation, User user) {

		List<AttractionDetails> attractionDetailsList = new ArrayList<>();
		for (Attraction attraction : get5ClosestAttractionsByMiles(visitedLocation)) {
			AttractionDetails attractionDetails = new AttractionDetails();
			logger.info("5 attractions " + attraction.attractionName);
			attractionDetails.setAttraction(attraction.attractionName);
			attractionDetails.setAttractionLatitude(attraction.latitude);
			attractionDetails.setAttractionLongitude(attraction.longitude);
			attractionDetails.setDistanceBetweenUserAndAttraction(
					rewardsService.getDistance(attraction, visitedLocation.location));
			attractionDetails.setUserLatitude(visitedLocation.location.latitude);
			attractionDetails.setUserLongitude(visitedLocation.location.longitude);
			attractionDetails.setRewardPoint(rewardsService.getRewardPoints(attraction, user));
			attractionDetailsList.add(attractionDetails);
		}
		for (AttractionDetails attractionDetails1 : attractionDetailsList) {
			logger.info("attraction name " + attractionDetails1.getAttraction() + " attraction reward point "
					+ attractionDetails1.getRewardPoint() + " attraction distance "
					+ attractionDetails1.getDistanceBetweenUserAndAttraction());
		}
		return attractionDetailsList;
	}

	public List<LocationHistory> locationHistoryOfAuser() {

		List<LocationHistory> locationHistorys = new ArrayList<>();
		List<User> users = getAllUsers();
		for (User user : users) {
			LocationHistory locationHistory = new LocationHistory();
			locationHistory.setUserId(user.getUserId());
			Map<String, Double> location = new HashMap<String, Double>();
			VisitedLocation visitedLocation = getUserLocation(user);
			location.put("Longitude", visitedLocation.location.latitude);
			location.put("Latitude", visitedLocation.location.longitude);
			locationHistory.setUserIdAndDistanceByLongitudeAndLatitude(location);
		}
		return locationHistorys;
	}
	
	 public HashMap<String, Location> getAllUsersCurrentLocation() {
	        List<User> listOfUsers = getAllUsers();
	        HashMap<String, Location> locationList = new HashMap<>();
	        for (User user : listOfUsers) {
	            String userId = user .getUserId().toString();
	            Location location = user.getLastVisitedLocation().location;
	            locationList.put(userId, location);
	        }
	        return locationList;
	    }
	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
			}
		});
	}

	/**********************************************************************************
	 * 
	 * Methods Below: For Internal Testing
	 * 
	 **********************************************************************************/
	private static final String tripPricerApiKey = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes
	// internal users are provided and stored in memory
	private final Map<String, User> internalUserMap = new HashMap<>();

	private void initializeInternalUsers() {
		IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			internalUserMap.put(userName, user);
		});
		logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i -> {
			user.addToVisitedLocations(new VisitedLocation(user.getUserId(),
					new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
		});
	}

	private double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}


}
