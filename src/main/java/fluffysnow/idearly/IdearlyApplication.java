package fluffysnow.idearly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class IdearlyApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdearlyApplication.class, args);
	}

}
