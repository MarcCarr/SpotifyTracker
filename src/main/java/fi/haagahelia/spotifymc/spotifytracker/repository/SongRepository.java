package fi.haagahelia.spotifymc.spotifytracker.repository;

import fi.haagahelia.spotifymc.spotifytracker.domain.Album;
import fi.haagahelia.spotifymc.spotifytracker.domain.Artist;
import fi.haagahelia.spotifymc.spotifytracker.domain.Song;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
    Song findByTitleAndArtistAndAlbum(String title, Artist artist, Album album);
    List<Song> findTop10ByOrderByPlayCountDesc();
    List<Song> findByArtist_NameIgnoreCase(String name);

}
