package fi.haagahelia.spotifymc.spotifytracker.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class SpotifyAuthController {
    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    private static final String AUTH_URL = "https://accounts.spotify.com/authorize";

    @GetMapping("/login")
    public RedirectView login() {
        String scope = "user-read-recently-played"; // Find recently played songs
        String authUrl = AUTH_URL + "?client_id" + clientId
                + "&response_type=code"
                + "&redirect_uri=" + redirectUri
                + "&scope=" + scope;

        return new RedirectView(authUrl);

    }
}
