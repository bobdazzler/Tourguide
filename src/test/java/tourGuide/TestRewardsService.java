package tourGuide;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.junit.Ignore;
import org.junit.Test;
import tourGuide.gpsUtil.Attraction;
import tourGuide.gpsUtil.Location;
import tourGuide.gpsUtil.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.GpsUtilService;
import tourGuide.service.RewardGetterService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

public class TestRewardsService {
	GpsUtilService gpsUtilService = new GpsUtilService();
	RewardGetterService rewardGetterService = new RewardGetterService();

	@Test
	public void userGetRewards() throws InterruptedException, ExecutionException {
		GpsUtilService gpsUtilService = new GpsUtilService();
		RewardGetterService rewardGetterService = new RewardGetterService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);

		InternalTestHelper.setInternalUserNumber(0);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);
		
		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		 VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(33.817595D, -117.922008D), new Date());
		List<Attraction> attraction =tourGuideService.getNearByAttractions(visitedLocation);
		
		user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction.get(0), new Date()));
		tourGuideService.trackUserLocation(user);
		List<UserReward> userRewards = user.getUserRewards();
		tourGuideService.tracker.stopTracking();
		assertTrue(userRewards.size() == 0);
	}
	
	@Test
	public void isWithinAttractionProximity() {
		GpsUtilService gpsUtil = new GpsUtilService();
		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);
		Attraction attraction = gpsUtil.getAttractions().get(0);
		assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
	}
	
	@Test
	public void nearAllAttractions() throws InterruptedException, ExecutionException {
		RewardsService rewardsService = new RewardsService(gpsUtilService, rewardGetterService);
		rewardsService.setProximityBuffer(Integer.MAX_VALUE);

		InternalTestHelper.setInternalUserNumber(1);
		TourGuideService tourGuideService = new TourGuideService(gpsUtilService, rewardsService);
		
		rewardsService.calculateRewards(tourGuideService.getAllUsers().get(0));
		List<UserReward> userRewards = tourGuideService.getUserRewards(tourGuideService.getAllUsers().get(0));
		tourGuideService.tracker.stopTracking();

		assertEquals(0, userRewards.size());
	}
	
}
