package fi.haagahelia.spotifymc.spotifytracker.dto;

import fi.haagahelia.spotifymc.spotifytracker.domain.PlayEvent;

import java.time.Instant;

/**
 * DTO for individual playback history.
 */
public class PlayEventDTO {
    private String title;
    private String artist;
    private String album;
    private Instant playedAt;

    public PlayEventDTO(PlayEvent event) {
        this.title = event.getSong().getTitle();
        this.artist = event.getSong().getArtist().getName();
        this.album = event.getSong().getAlbum().getTitle();
        this.playedAt = event.getPlayedAt();
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public Instant getPlayedAt() {
        return playedAt;
    }

}
