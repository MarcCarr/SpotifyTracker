package fi.haagahelia.spotifymc.spotifytracker.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyTrack {
    private String name;
    private SpotifyAlbum album;
    private List<SpotifyArtist> artists;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpotifyAlbum getAlbum() {
        return album;
    }

    public void setAlbum(SpotifyAlbum album) {
        this.album = album;
    }

    public List<SpotifyArtist> getArtists() {
        return artists;
    }

    public void setArtists(List<SpotifyArtist> artists) {
        this.artists = artists;
    }

}
