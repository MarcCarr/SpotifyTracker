# Spotify Listening Tracker
This is a Spring Boot Java application that tracks Sptify listening history through Spotify WEB API. Recently played tracks are stored in a PostgreSQL database accurately with playback event logging.

## Features
- OAuth2 authorization with Spotify
- Access token refreshing to enable long-term running
- Scheduled fetching of recently played tracks
- Deduplication using "played_at" timestamps
- Relational database storage with PostgreSQL
- REST enpoints to view song data
- DTO-based responses for structured JSON

## Endpoints
- /recent - Method - GET - Manually fetches recently played tracks (limit 50)
- /songs 
