package fi.haagahelia.spotifymc.spotifytracker.repository;

import fi.haagahelia.spotifymc.spotifytracker.domain.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
    Song findByTitleArtistAndAlbum(String title, String artist, String album);

}
