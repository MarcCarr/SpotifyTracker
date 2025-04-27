package fi.haagahelia.spotifymc.spotifytracker.service;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

/**
 * Managing token lifecycle using refresh token.
 * Refreshing and caching tokens until they expire
 */
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

    /**
     * Uses refresh token to request a new access token From Spotify.
     * Saves new access token and its expiration timestamp.
     */

    @PostConstruct
    public void init() {
        try {
            if (refreshToken == null || refreshToken.isBlank()) {
                Path path = Path.of("refresh-token.txt");
                if (Files.exists(path)) {
                    refreshToken = Files.readString(path).trim();
                    System.out.println("Loaded refresh token from file");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String refreshAccessToken() {
        if (refreshToken == null || refreshToken.isBlank()) {
            System.out.println("No refresh token available. Please log in first.");
            return null;
        }

        try {
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

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());

            accessToken = json.get("access_token").asText();
            int expiresIn = json.get("expires_in").asInt();
            expiresAt = Instant.now().plusSeconds(expiresIn);

            System.out.println("Refreshed access token: " + accessToken);
            return accessToken;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    //Returns valid access token. Refreshes if it's expired or not yet fetched.
    public String getAccessToken() {
        if (accessToken == null || Instant.now().isAfter(expiresAt)) {
            System.out.println("Access token rexpires, refreshing...(3h time diff!)");
            return refreshAccessToken();
        }
        return accessToken;
    }

    public void saveRefreshToken (String newRefreshToken) {
        if (newRefreshToken != null && !newRefreshToken.isBlank()) {
            try {
                Files.writeString(Path.of("refresh-token.txt"), newRefreshToken.trim());
                System.out.println("Refresh token saved to file.");
                refreshToken = newRefreshToken.trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
