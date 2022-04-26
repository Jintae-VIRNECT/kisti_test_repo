package com.virnect.content.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;

import lombok.extern.slf4j.Slf4j;
import redis.embedded.RedisServer;

@Slf4j
@TestConfiguration
public class TestRedisConfiguration {
	@Value("${spring.redis.port}")
	private int redisPort;

	private RedisServer redisServer;

	private static boolean isWindows() {
		String osName = System.getProperty("os.name").toLowerCase();
		return (osName.contains("win"));
	}

	@PostConstruct
	public void redisServer() throws IOException {
		int port = isRedisRunning() ? findAvailablePort() : redisPort;
		log.info("redisServer port : {}", port);
		redisServer = RedisServer.builder()
			.port(port)
			.setting("maxmemory 128M")
			.build();

		redisServer.start();
	}

	@PreDestroy
	public void stopRedis() {
		if (redisServer != null) {
			redisServer.stop();
		}
	}

	private boolean isRedisRunning() throws IOException {
		return isTCPDisabledState(executeShellCommand(redisPort));
	}

	public int findAvailablePort() throws IOException {
		for (int port = 6378; port >= 6300; port--) {
			Process process = executeShellCommand(port);
			if (!isTCPDisabledState(process)) {
				return port;
			}
		}

		throw new IllegalArgumentException("Not Found Available port: 6300 ~ 6377");
	}

	private Process executeShellCommand(int port) throws IOException {
		String[] shell = getShellScriptToCheckPort(port);
		return Runtime.getRuntime().exec(shell);
	}

	private String[] getShellScriptToCheckPort(int port) {
		if (isWindows()) {
			String command = String.format("netstat -nao | find \"LISTEN\" | find \"%d\"", port);
			return new String[] {"cmd.exe", "/y", "/c", command};
		} else {
			String command = String.format("netstat -nat | grep LISTEN|grep %d", port);
			return new String[] {"/bin/sh", "-c", command};
		}
	}

	private boolean isTCPDisabledState(Process process) {
		String line;
		StringBuilder pidInfo = new StringBuilder();

		try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			while ((line = input.readLine()) != null) {
				pidInfo.append(line);
			}
		} catch (Exception e) {
			log.error("pid info read fail", e);
		}

		String pid = pidInfo.toString();

		return !StringUtils.isEmpty(pid) && pid.contains("LISTEN");
	}
}
