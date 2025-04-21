package fi.haagahelia.spotifymc.spotifytracker.repository;

import fi.haagahelia.spotifymc.spotifytracker.domain.PlayEvent;
import fi.haagahelia.spotifymc.spotifytracker.domain.Song;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayEventRepository extends JpaRepository<PlayEvent, Long> {
    boolean existsBySongAndPlayedAt(Song song, Instant playedAt);

}
