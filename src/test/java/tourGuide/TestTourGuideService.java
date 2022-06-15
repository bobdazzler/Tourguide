package tourGuide;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.junit.Ignore;
import org.junit.Test;

import tourGuide.dto.AttractionDetails;
import tourGuide.dto.LocationHistory;
import tourGuide.gpsUtil.Attraction;
import tourGuide.gpsUtil.Location;
import tourGuide.gpsUtil.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardGetterService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tripPricer.Provider;

public class TestTourGuideService {
	GpsUtilService gpsUtilService = new GpsUtilService();
	RewardGetterService rewardGetterService = new RewardGetterService();
	RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);

	@Test
	public void getUserLocation() throws InterruptedException, ExecutionException {

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertThat(user.getVisitedLocations().size() > 0);
	}

	@Test
	public void addUser() {
		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		User retrivedUser = tourGuideService.getUser(user.getUserName());
		User retrivedUser2 = tourGuideService.getUser(user2.getUserName());

		tourGuideService.tracker.stopTracking();

		assertEquals(user, retrivedUser);
		assertEquals(user2, retrivedUser2);
	}

	@Test
	public void getAllUsers() {
		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

		tourGuideService.addUser(user);
		tourGuideService.addUser(user2);

		List<User> allUsers = tourGuideService.getAllUsers();

		tourGuideService.tracker.stopTracking();

		assertTrue(allUsers.contains(user));
		assertTrue(allUsers.contains(user2));
	}

	@Test
	public void trackUser() throws InterruptedException, ExecutionException {

		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.trackUserLocation(user);
		tourGuideService.tracker.stopTracking();
		assertThat( user.getVisitedLocations().size()>0);
	}

	@Test
	public void getNearbyAttractions() throws InterruptedException, ExecutionException {
		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);
		InternalTestHelper.setInternalUserNumber(100);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.trackUserLocation(user);
		VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(33.817595D, -117.922008D), new Date());
		List<Attraction> attractions = tourGuideService.getNearByAttractions(visitedLocation);

		tourGuideService.tracker.stopTracking();

		assertNotNull(attractions);
	}

	@Test
	public void sortNearByAttractionsByDistance() throws InterruptedException, ExecutionException {
		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);
		InternalTestHelper.setInternalUserNumber(100);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.trackUserLocation(user);
		 VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(33.817595D, -117.922008D), new Date());
		HashMap<Attraction, Double> attractions = tourGuideService.sortNearByAttractionsByDistance(visitedLocation);

		tourGuideService.tracker.stopTracking();
		assertNotNull(attractions);
	}

	@Test
	public void get5ClosestAttractionsByMiles() throws InterruptedException, ExecutionException {
		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);
		InternalTestHelper.setInternalUserNumber(100);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.trackUserLocation(user);
		 VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(33.817595D, -117.922008D), new Date());
		List<Attraction> attractions = tourGuideService.get5ClosestAttractionsByMiles(visitedLocation);

		tourGuideService.tracker.stopTracking();
		assertEquals(5, attractions.size());
	}

	@Test
	public void getEachAttractionDetailsForAUserTest() throws InterruptedException, ExecutionException {
		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);
		InternalTestHelper.setInternalUserNumber(100);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		tourGuideService.trackUserLocation(user);
		 VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(33.817595D, -117.922008D), new Date());
		List<AttractionDetails> attractions = tourGuideService.getEachAttractionDetailsForAUser(visitedLocation, user);

		tourGuideService.tracker.stopTracking();
		assertEquals(5, attractions.size());
	}

	@Test
	public void getTripDeals() {
		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);
		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

		List<Provider> providers = tourGuideService.getTripDeals(user);

		tourGuideService.tracker.stopTracking();
		assertEquals(5, providers.size());
		assertThat(providers.contains(user.getUserPreferences()));
		assertThat(providers.contains(user.getUserPreferences().getNumberOfChildren()));
	}

	@Test
	public void getAllUsersCurrentLocation() throws InterruptedException, ExecutionException {
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);
		InternalTestHelper.setInternalUserNumber(0);
		List<LocationHistory> locationList = tourGuideService.locationHistoryOfUsers();
		tourGuideService.tracker.stopTracking();
		assertThat(locationList.contains(user.getUserId()));

	}

}
