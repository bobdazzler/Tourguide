package tourGuide.service;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import tourGuide.gpsUtil.Attraction;
import tourGuide.gpsUtil.VisitedLocation;
@Service
public class GpsUtilService {
	 private Logger logger = LoggerFactory.getLogger(GpsUtilService.class);

	    /**
	     * this method connects with the gpsUntil api and gets the list of attractions
	     * @return list of all attractions
	     */
	 
	    public List<Attraction> getAttractions() {
	        WebClient.Builder webClientBuilder = WebClient.builder();
	        String jsonResponseFromGetAttraction = webClientBuilder.build()
	                .get()
	                .uri("http://localhost:8083/attractions")
	                .retrieve()
	                .bodyToMono(String.class)
	                .block();

	        Gson gson = new Gson();
	        List<Attraction> attractionList = gson.fromJson(jsonResponseFromGetAttraction,
	                new TypeToken<List<Attraction>>() {
	                }.getType());
	        return attractionList;
	    }

	    /**
	     * this method finds a user current location by connecting to the gpsUtil controller
	     * @param userId unique Id of the user
	     * @return user's current location
	     */
	    public VisitedLocation getUserLocation(@RequestParam UUID userId) {
	        WebClient.Builder webClientBuilder = WebClient.builder();
	        String JsonResponseFrom = webClientBuilder.build()
	                .get()
	                .uri("http://localhost:8083/userLocation?userId=" + userId)
	                .retrieve()
	                .bodyToMono(String.class)
	                .block();
	        Gson gson = new Gson();
	        return gson.fromJson(JsonResponseFrom, VisitedLocation.class);
	    }
}
