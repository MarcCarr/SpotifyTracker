package fi.haagahelia.spotifymc.spotifytracker.dto;

import fi.haagahelia.spotifymc.spotifytracker.domain.Song;

/**
 * DTO for exposing song data in API responses.
 * Includes song title, artist name, album title, and play count.
 */
public class SongResponseDTO {
    private String title;
    private int playCount;
    private String artist;
    private String album;

    public SongResponseDTO(Song song) {
        this.title = song.getTitle();
        this.playCount = song.getPlayCount();
        this.artist = song.getArtist() != null ? song.getArtist().getName() : null;
        this.album = song.getAlbum() != null ? song.getAlbum().getTitle() : null;
    }

    public String getTitle() {
        return title;
    }

    public int getPlayCount() {
        return playCount;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

}
