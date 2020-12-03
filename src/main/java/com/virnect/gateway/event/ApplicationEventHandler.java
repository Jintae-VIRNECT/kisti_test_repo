package com.virnect.gateway.event;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import reactor.util.Logger;
import reactor.util.Loggers;

@Slf4j
@Service
public class ApplicationEventHandler {
	private static final Logger logger = Loggers.getLogger("com.virnect.gateway.event.ApplicationEventHandler");

	@EventListener(ApplicationReadyEvent.class)
	public void applicationReady() {
		String msg = "\n\n----------------------------------------------------\n" + "\n"
			+ "   Platform Gateway Server is ready!\n"
			+ "   ---------------------------\n" + "\n"
			+ "   * Server Host: [" + System.getenv("eureka.instance.ip-address") + "]\n" + "\n"
			+ "   * VIRNECT_ENV: [" + System.getenv("VIRNECT_ENV") + "]\n" + "\n"
			+ "   * Config Server Url: [" + System.getenv("CONFIG_SERVER") + "]\n" + "\n"
			+ "----------------------------------------------------\n";
		logger.info(msg);
	}
}
