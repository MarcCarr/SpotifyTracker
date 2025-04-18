package fi.haagahelia.spotifymc.spotifytracker.domain;

import jakarta.persistence.*;

@Entity
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String genre;
    private int playCount = 1;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void incrementPlayCount() {
        this.playCount++;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

}
