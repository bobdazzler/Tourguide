package tourGuide.dto;
public class AttractionDetails {
String attraction;
Double attractionLongitude;
Double attractionLatitude;
Double userLongitude;
Double userLatitude;
Double distanceBetweenUserAndAttraction;
int rewardPoint;
public String getAttraction() {
	return attraction;
}
public void setAttraction(String attraction) {
	this.attraction = attraction;
}
public Double getAttractionLongitude() {
	return attractionLongitude;
}
public void setAttractionLongitude(Double attractionLongitude) {
	this.attractionLongitude = attractionLongitude;
}
public Double getAttractionLatitude() {
	return attractionLatitude;
}
public void setAttractionLatitude(Double attractionLatitude) {
	this.attractionLatitude = attractionLatitude;
}
public Double getUserLongitude() {
	return userLongitude;
}
public void setUserLongitude(Double userLongitude) {
	this.userLongitude = userLongitude;
}
public Double getUserLatitude() {
	return userLatitude;
}
public void setUserLatitude(Double userLatitude) {
	this.userLatitude = userLatitude;
}

public Double getDistanceBetweenUserAndAttraction() {
	return distanceBetweenUserAndAttraction;
}
public void setDistanceBetweenUserAndAttraction(Double distanceBetweenUserAndAttraction) {
	this.distanceBetweenUserAndAttraction = distanceBetweenUserAndAttraction;
}
public int getRewardPoint() {
	return rewardPoint;
}
public void setRewardPoint(int rewardPoint) {
	this.rewardPoint = rewardPoint;
}
}
