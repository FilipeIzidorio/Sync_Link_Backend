package com.synclink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// abrir o swagger http://localhost:8081/sync-link/swagger-ui/index.html

@SpringBootApplication
public class SyncLinkBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SyncLinkBackendApplication.class, args);
	}

}
