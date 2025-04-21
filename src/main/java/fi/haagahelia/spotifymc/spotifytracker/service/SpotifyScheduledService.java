package fi.haagahelia.spotifymc.spotifytracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SpotifyScheduledService {

    @Autowired
    private SpotifyTrackService spotifyTrackService;

    @Scheduled(fixedRate = 3600000) // In milliseconds. Every hour fetched when app is running.
    public void fetchOnSchedule() {
        System.out.println("Running scheduled Spotify fetch...");

        spotifyTrackService.fetchAndStoreRecentlyPlayedTracks();
    }

}
