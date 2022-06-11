package tourGuide.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LocationHistory {
	public UUID userId;
	Map<String,Double> distanceByLongitudeAndLatitude = new HashMap<String,Double>();
	public UUID getUserId() {
		return userId;
	}
	public void setUserId(UUID userId) {
		this.userId = userId;
	}
	public Map<String, Double> getDistanceByLongitudeAndLatitude() {
		return distanceByLongitudeAndLatitude;
	}
	public void setDistanceByLongitudeAndLatitude(Map<String, Double> distanceByLongitudeAndLatitude) {
		this.distanceByLongitudeAndLatitude = distanceByLongitudeAndLatitude;
	}
	
}
