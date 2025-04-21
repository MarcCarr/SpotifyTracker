package fi.haagahelia.spotifymc.spotifytracker.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SpotifyAuthService {
    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.refresh-token}")
    private String refreshToken;

    private String accessToken;
    private Instant expiresAt;

    public String refreshAcessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://accounts.spotify.com/api/token",
                request,
                String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());

            accessToken = json.get("access_token").asText();
            int expiresIn = json.get("expires_in").asInt(); // 3600s / 1h
            expiresAt = Instant.now().plusSeconds(expiresIn);

            System.out.println("New access token: " + accessToken);
            System.out.println("Expires at: " + expiresAt);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return accessToken;

    }

    public String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(expiresAt)) {
            System.out.println("Access token rexpires, refreshing...(3h time diff!)");
            refreshAcessToken();
        }
        return accessToken;
    }

}
