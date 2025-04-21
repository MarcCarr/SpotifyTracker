package fi.haagahelia.spotifymc.spotifytracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpotifytrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpotifytrackerApplication.class, args);
	}

}
