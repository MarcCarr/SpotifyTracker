package fi.haagahelia.spotifymc.spotifytracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.haagahelia.spotifymc.spotifytracker.dto.SpotifyResponse;
import fi.haagahelia.spotifymc.spotifytracker.dto.SpotifyItem;
import fi.haagahelia.spotifymc.spotifytracker.domain.*;
import fi.haagahelia.spotifymc.spotifytracker.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
                String playedAt = item.getPlayed_at();

                System.out.println(
                        "Title " + title + " - " + artistName + " | Album: " + albumTitle + " | Played at: "
                                + playedAt);

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

                Song existingSong = songRepository.findByTitleAndArtistAndAlbum(title, artist, album);
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

            System.out.println("Recently played tracks fetched and stored.");

        } catch (Exception e) {
            System.err.println("Error during scheduled fetch:");
            e.printStackTrace();

        }
    }
}
