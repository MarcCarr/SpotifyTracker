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

@RestController
public class PlayEventController {
    @Autowired
    private PlayEventRepository playEventRepository;

    @GetMapping("/timeline")
    public ResponseEntity<List<PlayEventDTO>> getTimeline(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        List<PlayEvent> events;

        if (from != null && to != null) {
            Instant fromDate = LocalDate.parse(from).atStartOfDay(ZoneOffset.UTC).toInstant();
            Instant toDate = LocalDate.parse(to).plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant(); //1 day added in case searching through same date.
            events = playEventRepository.findByPlayedAtBetween(fromDate, toDate);
            System.out.println("Filterin from " + fromDate + " to " + toDate);
            System.out.println("Found events: " + events.size());
        } else {
            events = playEventRepository.findAll(Sort.by(Sort.Direction.DESC, "playedAt"));
        }

        List<PlayEventDTO> timeline = events.stream()
                .map(PlayEventDTO::new)
                .toList();

        return ResponseEntity.ok(timeline);

    }

}
