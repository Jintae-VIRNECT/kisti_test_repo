/*
 * (C) Copyright 2017-2020 OpenVidu (https://openvidu.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.virnect.remoteserviceserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author Pablo Fuente (pablofuenteperez@gmail.com)
 */
public class CommandExecutor {

	private static final Logger log = LoggerFactory.getLogger(CommandExecutor.class);

	private static final long MILLIS_TIMEOUT = 10000;

	public static String execCommand(String... command) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		processBuilder.redirectErrorStream(true);
		return commonExecCommand(processBuilder);
	}

	public static String execCommandRedirectError(File errorOutputFile, String... command)
			throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(command).redirectError(errorOutputFile);
		return commonExecCommand(processBuilder);
	}

	public static void execCommandRedirectStandardOutputAndError(File standardOutputFile, File errorOutputFile,
			String... command) throws IOException, InterruptedException {
		ProcessBuilder processBuilder = new ProcessBuilder(command).redirectOutput(standardOutputFile)
				.redirectError(errorOutputFile);
		Process process = processBuilder.start();
		if (!process.waitFor(MILLIS_TIMEOUT, TimeUnit.MILLISECONDS)) {
			log.error("Command {} did not receive a response in {} ms", Arrays.toString(command), MILLIS_TIMEOUT);
			String errorMsg = "Current process status of host:\n" + gatherLinuxHostInformation();
			log.error(errorMsg);
			throw new IOException(errorMsg);
		}
	}

	private static String commonExecCommand(ProcessBuilder processBuilder) throws IOException, InterruptedException {
		Process process = processBuilder.start();
		StringBuilder processOutput = new StringBuilder();
		String output;
		InputStreamReader inputStreamReader = null;
		BufferedReader processOutputReader = null;
		try {
			inputStreamReader = new InputStreamReader(process.getInputStream());
			processOutputReader = new BufferedReader(inputStreamReader);
			String readLine;
			while ((readLine = processOutputReader.readLine()) != null) {
				processOutput.append(readLine + System.lineSeparator());
			}

			if (!process.waitFor(MILLIS_TIMEOUT, TimeUnit.MILLISECONDS)) {
				log.error("Command {} did not receive a response in {} ms",
						Arrays.toString(processBuilder.command().toArray()), MILLIS_TIMEOUT);
				String errorMsg = "Current process status of host:\n" + gatherLinuxHostInformation();
				log.error(errorMsg);
				throw new IOException(errorMsg);
			}
			output = processOutput.toString().trim();
		} finally {
			if (inputStreamReader != null) {
				inputStreamReader.close();
			}
			if (processOutputReader != null) {
				processOutputReader.close();
			}
		}
		return output;
	}

	public static String gatherLinuxHostInformation() throws IOException, InterruptedException {
		final String psCommand = "ps -eo pid,ppid,user,%mem,%cpu,cmd --sort=-%cpu";
		return execCommand("/bin/sh", "-c", psCommand);
	}

}
