package fi.haagahelia.spotifymc.spotifytracker.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
public class PlayEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant playedAt;

    @ManyToOne
    @JoinColumn(name = "song_id")

    private Song song;

    public PlayEvent() {
    }

    public PlayEvent(Song song, Instant playedAt) {
        this.song = song;
        this.playedAt = playedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Instant playedAt) {
        this.playedAt = playedAt;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

}
