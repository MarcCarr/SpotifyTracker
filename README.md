# Spotify Listening Tracker
Final project for Haaga-Helia backend course Spring 2025.

This is a Java Spring Boot application that connects to the Spotify API to track and store listening history. Track metadata is stored in a PostegreSQL database, and includes custom analytics like play counts, top tracks, and listening timelines.

## Features

- Spotify OAuth 2.0 authentication (authorization flow)
- Stores recently played track using Spotify's '/recently-played' endpoint
- Tracks play count per song with deduplication via 'played_at' timestamps
- Full song, artist, and album entity structure
- JSON API with endpoints to:
  - View all songs('/songs')
  - View top 10 most played songs ('/songs/top')
  - Filter songs by artist ('/songs/by-artist/{name}')
  - View a full listening timeline ('/timeline')
  - Filter timeline by date range
- Scheduled automatic fetching of new plays
- PostgreSQL database integration 

## Technologies used

- Java 17
- Spring Boot
- Spring Boot JPA
- Spotify Web API
- PostgreSQL (local or cloud)
- Maven
- ObbjectMapper (Jackson) for JSON parsing

## Running the Project
1. Clone the repository

2. Visit https://developer.spotify.com/documentation/web-api to create your own app (Required for API access)
   - Find client and secret ID
   - Set a redirect URI. E.g http://127.0.0.1:8080/callback
   - Note. localhost - URI's are no longer accepted by Spotify!
3. Set up PostegrSQL database (Install first if needed)
   - In terminal: CREATE DATABASE yourdatabasename;
   - CREATE USER youruser WITH PASSWORD 'yourpassword';
   - GRANT ALL PRIVILEGES ON DATABASE yourdatabasename TO youruser;
4. Add the following to 'application properties':

spotify.client-id=your_spotify_client_id
spotify.client-secret=your_spotify_client_secret
spotify.redirect-uri=http://127.0.0.1:8080/callback
spotify.refresh-token= # Leavy empty initially! Will be auto-saved after login.


spring.datasource.url=jdbc:postgresql://localhost:5432/yourdatabasename
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password


spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect


