package fi.haagahelia.spotifymc.spotifytracker.controller;

import fi.haagahelia.spotifymc.spotifytracker.domain.Song;
import fi.haagahelia.spotifymc.spotifytracker.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs") //Request mapping ready for additional pages for stats on tracks
public class SongController {

    @Autowired
    private SongRepository songRepository;

    @GetMapping("/songs") // Lists all songs in JSON from DB
    public ResponseEntity<List<Song>> getAllSongs() {
        return ResponseEntity.ok(songRepository.findAll());

    }

}
