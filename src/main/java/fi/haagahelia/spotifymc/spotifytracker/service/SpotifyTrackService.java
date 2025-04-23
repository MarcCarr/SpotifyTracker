package fi.haagahelia.spotifymc.spotifytracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.haagahelia.spotifymc.spotifytracker.dto.SpotifyResponse;
import fi.haagahelia.spotifymc.spotifytracker.dto.SpotifyItem;
import fi.haagahelia.spotifymc.spotifytracker.domain.*;
import fi.haagahelia.spotifymc.spotifytracker.repository.*;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
/**
 * Service that fetches recently played tracks from Spotify API, processes them, and stores new play events to DB
 * OAuth token usage, Track, Artist, album entity management and deduplication with PlayEvent
 */
@Service
public class SpotifyTrackService {
    @Autowired
    private SpotifyAuthService spotifyAuthService;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private PlayEventRepository playEventRepository;

    /**
     * Fetches recently played tracks (max 50)
     * Checks for unique playback events and stores them
     */
    public void fetchAndStoreRecentlyPlayedTracks() {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String accessToken = spotifyAuthService.getAccessToken();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.spotify.com/v1/me/player/recently-played?limit=50",
                    HttpMethod.GET,
                    request,
                    String.class);

            ObjectMapper mapper = new ObjectMapper();
            SpotifyResponse spotifyResponse = mapper.readValue(response.getBody(), SpotifyResponse.class);

            for (SpotifyItem item : spotifyResponse.getItems()) {
                String title = item.getTrack().getName();
                String artistName = item.getTrack().getArtists().get(0).getName();
                String albumTitle = item.getTrack().getAlbum().getName();
                Instant playedAt = Instant.parse(item.getPlayed_at());

                //Look for or create Artist
                Artist artist = artistRepository.findByName(artistName);
                if (artist == null) {
                    artist = new Artist();
                    artist.setName(artistName);
                    artistRepository.save(artist);
                }

                //Look for or create Album
                Album album = albumRepository.findByTitleAndArtist(albumTitle, artist);
                if (album == null) {
                    album = new Album();
                    album.setTitle(albumTitle);
                    album.SetArtist(artist);
                    albumRepository.save(album);
                }

                // Look for or create Song
                Song existingSong = songRepository.findByTitleAndArtistAndAlbum(title, artist, album);
                if (existingSong == null) {
                    existingSong = new Song();
                    existingSong.setTitle(title);
                    existingSong.setArtist(artist);
                    existingSong.setAlbum(album);
                    existingSong.setPlayCount(0);
                    songRepository.save(existingSong);
                }

                /**
                 * Only store unique play events by timestamp to avoid duplicate play counts
                 * Play count inflates with /recent fetch without this!
                */
                if (!playEventRepository.existsBySongAndPlayedAt(existingSong, playedAt)) {
                    existingSong.incrementPlayCount();
                    songRepository.save(existingSong);

                    PlayEvent event = new PlayEvent(existingSong, playedAt);
                    playEventRepository.save(event);
                }
            }

            System.out.println("Recently played tracks fetched and stored.");

        } catch (Exception e) {
            System.err.println("Error during scheduled fetch:");
            e.printStackTrace();

        }
    }
}
