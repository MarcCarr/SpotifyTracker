package fi.haagahelia.spotifymc.spotifytracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.view.RedirectView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.haagahelia.spotifymc.spotifytracker.domain.Album;
import fi.haagahelia.spotifymc.spotifytracker.domain.Artist;
import fi.haagahelia.spotifymc.spotifytracker.domain.Song;
import fi.haagahelia.spotifymc.spotifytracker.dto.SpotifyItem;
import fi.haagahelia.spotifymc.spotifytracker.dto.SpotifyResponse;
import fi.haagahelia.spotifymc.spotifytracker.repository.AlbumRepository;
import fi.haagahelia.spotifymc.spotifytracker.repository.ArtistRepository;
import fi.haagahelia.spotifymc.spotifytracker.repository.SongRepository;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Controller
public class SpotifyAuthController {
    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    private static final String AUTH_URL = "https://accounts.spotify.com/authorize";

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;
    
    @Autowired
    private AlbumRepository albumRepository;

    @GetMapping("/login")
    public RedirectView login() {
        String scope = "user-read-recently-played"; // Get spotify code for API access and recently played songs
        String authUrl = AUTH_URL + "?client_id=" + clientId
                + "&response_type=code"
                + "&redirect_uri=" + redirectUri
                + "&scope=" + scope;

        return new RedirectView(authUrl);

    }

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

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://accounts.spotify.com/api/token",
                request,
                String.class);

        System.out.println("Spotify token:\n" + response.getBody());

        return ResponseEntity.ok("Authorization complete. See console for token");
    }

    @GetMapping("/recent")
    public ResponseEntity<String> getRecentlyPlayed() {
        RestTemplate restTemplate = new RestTemplate();

        String accessToken = "Access token"; //Removed hard coded token before push. Dynamic token later.

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.spotify.com/v1/me/player/recently-played?limit=10",
                HttpMethod.GET,
                request,
                String.class);

        try {

            ObjectMapper mapper = new ObjectMapper();
            SpotifyResponse spotifyResponse = mapper.readValue(response.getBody(), SpotifyResponse.class);

            for (SpotifyItem item : spotifyResponse.getItems()) {
                String title = item.getTrack().getName();
                String artistName = item.getTrack().getArtists().get(0).getName();
                String albumTitle = item.getTrack().getAlbum().getName();
                String playedAt = item.getPlayed_at();

                System.out.println(
                        "Title " + title + " - " + artistName + " | Album: " + albumTitle + " | Played at: " + playedAt);

               Artist artist = artistRepository.findByName(artistName);
               if (artist == null) {
                artist = new Artist();
                artist.setName(artistName);
                artistRepository.save(artist);
               }

               Album album = albumRepository.findByTitleAndArtist(albumTitle, artist);
               if (album == null) {
                album = new Album();
                album.setTitle(albumTitle);
                album.SetArtist(artist);
                albumRepository.save(album);
               }

               Song existingSong = songRepository.findByTitleArtistAndAlbum(title, artistName, albumTitle);
               if (existingSong != null) {
                existingSong.incrementPlayCount();
                songRepository.save(existingSong);
               } else {
                Song newSong = new Song();
                newSong.setTitle(title);
                newSong.setArtist(artist);
                newSong.setAlbum(album);
                newSong.setPlayCount(1);
                songRepository.save(newSong);
               }

            
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error parsing Spotify data");
        }

        return ResponseEntity.ok("Tracks fetched and printed in console.");
    }

}
