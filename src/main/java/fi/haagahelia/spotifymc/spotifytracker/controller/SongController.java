package fi.haagahelia.spotifymc.spotifytracker.controller;

import fi.haagahelia.spotifymc.spotifytracker.domain.Song;
import fi.haagahelia.spotifymc.spotifytracker.dto.SongListResponseDTO;
import fi.haagahelia.spotifymc.spotifytracker.dto.SongResponseDTO;
import fi.haagahelia.spotifymc.spotifytracker.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints for all songs, top-played songs, and filter by artist
 */
@RestController
@RequestMapping("/songs") 
public class SongController {

    @Autowired
    private SongRepository songRepository;

    /**
     * Returns all tracked songs + total song and artist counts
     * Endpoint: GET /songs
     */
    @GetMapping 
    public ResponseEntity<SongListResponseDTO> getAllSongs() {
        List<Song> songEntities = songRepository.findAll();
        List<SongResponseDTO> songDTOs = songEntities.stream()
                .map(SongResponseDTO::new)
                .toList();

        int totalSongs = songEntities.size();
        long totalArtists = songEntities.stream()
                .map(song -> song.getArtist().getName())
                .distinct()
                .count();

        SongListResponseDTO response = new SongListResponseDTO(totalSongs,(int) totalArtists, songDTOs);
        return ResponseEntity.ok(response);

    }

    /**
     * Returns the top 10 most played songs.
     * Endpoint: GET /songs/top
     */
    @GetMapping("/top")
    public ResponseEntity<List<SongResponseDTO>> getTopSongs() {
        List<Song> topSongs = songRepository.findTop10ByOrderByPlayCountDesc();
        List<SongResponseDTO> response = topSongs.stream()
                .map(SongResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);

    }

    /**
     * Returns all songs by a given artist (case-insensitive).
     * Endpoint: GET /songs/by-artist/{artist name}
     */
    @GetMapping("/by-artist/{name}")
    public ResponseEntity<List<SongResponseDTO>> getByArtist(@PathVariable String name) {
        List<Song> songs = songRepository.findByArtist_NameIgnoreCase(name);
        List<SongResponseDTO> response = songs.stream()
                .map(SongResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);
    }

}
