package tourGuide.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
@Service 
public class RewardGetterService {
	 public int getAttractionRewardPoints(UUID attractionId, UUID userId){
	        WebClient.Builder webClientBuilder = WebClient.builder();
	        String JsonResponseFrom =webClientBuilder.build()
	                .get()
	                .uri("http://localhost:8084/attractionRewardPoint?attractionId="+attractionId+"&userId="+userId)
	                .retrieve()
	                .bodyToMono(String.class)
	                .block();
	        return Integer.parseInt(JsonResponseFrom);
	    }

}
