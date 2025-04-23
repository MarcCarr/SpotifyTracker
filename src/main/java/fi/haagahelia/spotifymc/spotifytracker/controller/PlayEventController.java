package fi.haagahelia.spotifymc.spotifytracker.controller;

import fi.haagahelia.spotifymc.spotifytracker.dto.PlayEventDTO;
import fi.haagahelia.spotifymc.spotifytracker.domain.PlayEvent;
import fi.haagahelia.spotifymc.spotifytracker.repository.PlayEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

/**
 * REST controller for listening timeline.
 * Optional filterin by date using 'from' and 'to' query parameters.
 */
@RestController
public class PlayEventController {
    @Autowired
    private PlayEventRepository playEventRepository;

    /**
     * Returns list of played tracks (most recent first)
     * Query parameters:
     * - from: start date in format yyyy-MM-dd
     * -to : end sate in format yyyy-MM-dd
     */
    @GetMapping("/timeline")
    public ResponseEntity<List<PlayEventDTO>> getTimeline(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        List<PlayEvent> events;

        if (from != null && to != null) {
            Instant fromDate = LocalDate.parse(from)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();
            Instant toDate = LocalDate.parse(to)
                    .plusDays(1)
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant(); // 1 day added to include full 'to' day.
            events = playEventRepository.findByPlayedAtBetween(fromDate, toDate);

        } else {
            events = playEventRepository.findAll(Sort.by(Sort.Direction.DESC, "playedAt"));
        }

        List<PlayEventDTO> timeline = events.stream()
                .map(PlayEventDTO::new)
                .toList();

        return ResponseEntity.ok(timeline);

    }

}
