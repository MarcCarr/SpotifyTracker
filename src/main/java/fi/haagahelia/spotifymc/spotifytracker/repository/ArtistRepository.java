package fi.haagahelia.spotifymc.spotifytracker.repository;

import fi.haagahelia.spotifymc.spotifytracker.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Artist findByName(String artistName);

}
