package fi.haagahelia.spotifymc.spotifytracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.haagahelia.spotifymc.spotifytracker.service.SpotifyAuthService;
import fi.haagahelia.spotifymc.spotifytracker.service.SpotifyTrackService;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * For authentication and authorization with Spotify using OAuth 2.0
 * Login, callback handling, token refresh, and manual fetching of recent tracks
 */

@RestController
public class SpotifyAuthController {

    // Values injected from application.properties
    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    private static final String AUTH_URL = "https://accounts.spotify.com/authorize";

    @Autowired
    private SpotifyAuthService spotifyAuthService;

    @Autowired
    private SpotifyTrackService spotifyTrackService;

    /**
     * Spotify login flow initiated with redirecting user to Spotify's auth page.
     * After login, Spotify redirects to /callback with authorization code (URL).
     */
    @GetMapping("/login")
    public RedirectView login() {
        String scope = "user-read-recently-played";
        String authUrl = AUTH_URL + "?client_id=" + clientId
                + "&response_type=code"
                + "&redirect_uri=" + redirectUri
                + "&scope=" + scope;

        return new RedirectView(authUrl);

    }

    // Exchanges the authorization code for access and refresh token
    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://accounts.spotify.com/api/token",
                    request,
                    String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response.getBody());

            JsonNode accessTokenNode = json.get("access_token");
            JsonNode refreshTokenNode = json.get("refresh_token");

            if (accessTokenNode == null || refreshTokenNode == null) {
                System.out.println("Missing token fields in Spotify response.");
                System.out.println("Full spotify response: " + response.getBody());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Failed to retrive tokens from Spotify. Check client settings");
            }

            String accessToken = accessTokenNode.asText();
            String refreshToken = refreshTokenNode.asText();

            System.out.println("Access token: " + accessToken);
            System.out.println("Refresh token: " + refreshToken);

            spotifyAuthService.saveRefreshToken(refreshToken);

            return ResponseEntity.ok("Authorization complete. Refresh token saved. Check console");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process token response.");
        }
    }

    /**
     * Refreshes access token with saved refresh token.
     * Access to Spotify API is maintained withour re-login or hard-coding access /
     * refresh token.
     */

    @GetMapping("/refresh-token")
    public ResponseEntity<String> refreshToken() {
        String newToken = spotifyAuthService.refreshAccessToken();

        if (newToken == null) {
            return ResponseEntity.badRequest().body("No refresh token available. Please login first.");
        }

        return ResponseEntity.ok("New access token refreshed successfully!");
    }

    /**
     * Manually triggers fetching of recently played tracks.
     * Saving new play events and increments play counts when appropriate.
     */
    @GetMapping("/recent")
    public ResponseEntity<String> fetchRecent() {
        spotifyTrackService.fetchAndStoreRecentlyPlayedTracks();

        return ResponseEntity.ok("Fetched and stored recent tracks.");
    }

}
