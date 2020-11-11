package com.virnect.workspace.event;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationEventHandler {
	private final EurekaDiscoveryClient eurekaDiscoveryClient;

	@EventListener(ApplicationReadyEvent.class)
	public void applicationReady() {
		String msg = "\n\n----------------------------------------------------\n" + "\n"
			+ "   Platform Workspace Server is ready!\n"
			+ "   ---------------------------\n" + "\n"
			+ "   * Server Host: [" + System.getenv("eureka.instance.ip-address") + "]\n" + "\n"
			+ "   * VIRNECT_ENV: [" + System.getenv("VIRNECT_ENV") + "]\n" + "\n"
			+ "   * Config Server Url: [" + System.getenv("CONFIG_SERVER") + "]\n" + "\n"
			+ "----------------------------------------------------\n";
		log.info(msg);

		for (String service : eurekaDiscoveryClient.getServices()) {
			eurekaDiscoveryClient.getInstances(service).forEach(instance -> {
				log.info(String.format("[ SERVICE_ID: %-25s HOST: %-13s PORT: %-6s SECURE(HTTPS): %-5s ]"
					, service, instance.getHost(), instance.getPort(), instance.isSecure()));
			});
		}
	}
}
