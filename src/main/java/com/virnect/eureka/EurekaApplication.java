package com.virnect.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableEurekaServer
public class EurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }


	@EventListener(ApplicationReadyEvent.class)
	public void applicationReady() {
		String msg = "\n\n----------------------------------------------------\n" + "\n"
			+ "   Platform Eureka Server is ready!\n"
			+ "   ---------------------------\n" + "\n"
			+ "   * Server Host: [" + System.getenv("eureka.instance.ip-address") + "]\n" + "\n"
			+ "   * VIRNECT_ENV: [" + System.getenv("VIRNECT_ENV") + "]\n" + "\n"
			+ "   * Config Server Url: [" + System.getenv("CONFIG_SERVER") + "]\n" + "\n"
			+ "----------------------------------------------------\n";
		log.info(msg);
	}
}
