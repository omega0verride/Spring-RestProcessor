package org.indritbreti.restprocessordemo;

import org.indritbreti.restprocessor.EnableRestProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRestProcessor
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
