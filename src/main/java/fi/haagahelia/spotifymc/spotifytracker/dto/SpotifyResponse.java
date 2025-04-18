package fi.haagahelia.spotifymc.spotifytracker.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyResponse {
    private List<SpotifyItem> items;

    public List<SpotifyItem> getItems() {
        return items;
    }

    public void setItems(List<SpotifyItem> items) {
        this.items = items;
    }


}
