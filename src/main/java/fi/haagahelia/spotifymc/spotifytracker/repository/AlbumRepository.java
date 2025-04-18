package fi.haagahelia.spotifymc.spotifytracker.repository;

import fi.haagahelia.spotifymc.spotifytracker.domain.Album;
import fi.haagahelia.spotifymc.spotifytracker.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    Album findByTitleAndArtist(String title, Artist artist);

}
