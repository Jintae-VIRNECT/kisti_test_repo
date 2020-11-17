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

package com.virnect.mediaserver.cdr;

import com.virnect.mediaserver.kurento.endpoint.KmsEvent;
import com.virnect.mediaserver.summary.SessionSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDRLoggerFile implements CDRLogger {

	private Logger log = LoggerFactory.getLogger(CDRLoggerFile.class);

	/**
	 * This logs directly to the file thanks to logback configuration
	 */
	@Override
	public void log(CDREvent event) {
		log.info("{}", event);
	}

	@Override
	public void log(KmsEvent event) {
	}

	@Override
	public void log(SessionSummary sessionSummary) {
	}

}
