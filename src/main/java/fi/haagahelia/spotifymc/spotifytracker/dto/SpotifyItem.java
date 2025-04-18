package fi.haagahelia.spotifymc.spotifytracker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyItem {
    private SpotifyTrack track;
    private String played_at;

    public SpotifyTrack getTrack() {
        return track;
    }

    public void setTrack(SpotifyTrack track) {
        this.track = track;
    }

    public String getPlayed_at() {
        return played_at;
    }

    public void setPlayed_at(String played_at) {
        this.played_at = played_at;
    }
}
