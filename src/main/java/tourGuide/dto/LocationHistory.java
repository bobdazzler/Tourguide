package tourGuide.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LocationHistory {
	public UUID userId;
	Map<String,Double> userIdAndDistanceByLongitudeAndLatitude = new HashMap<String,Double>();
	public UUID getUserId() {
		return userId;
	}
	public void setUserId(UUID userId) {
		this.userId = userId;
	}
	public Map<String, Double> getUserIdAndDistanceByLongitudeAndLatitude() {
		return userIdAndDistanceByLongitudeAndLatitude;
	}
	public void setUserIdAndDistanceByLongitudeAndLatitude(Map<String, Double> userIdAndDistanceByLongitudeAndLatitude) {
		this.userIdAndDistanceByLongitudeAndLatitude = userIdAndDistanceByLongitudeAndLatitude;
	}
}
