package fi.haagahelia.spotifymc.spotifytracker.dto;

import java.util.List;

/**
 * DTO wrapper for /songs endpoint to show total songs and total unique artists
 */
public class SongListResponseDTO {
    private int totalSongs;
    private int totalArtists;
    private List<SongResponseDTO> songs;

    public SongListResponseDTO(int totalSongs, int totalArtists, List<SongResponseDTO> songs) {
        this.totalSongs = totalSongs;
        this.totalArtists = totalArtists;
        this.songs = songs;
    }

    public int getTotalSongs() {
        return totalSongs;
    }

    public int getTotalArtists() {
        return totalArtists;
    }

    public List<SongResponseDTO> getSongs() {
        return songs;
    }

}
