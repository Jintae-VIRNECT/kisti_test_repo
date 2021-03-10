package com.virnect.mediaserver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MediaNodeStatusManagerDummy implements MediaNodeStatusManager {

	private static final Logger log = LoggerFactory.getLogger(MediaNodeStatusManagerDummy.class);
	@Override
	public boolean isLaunching(String mediaNodeId) {
		log.info("Media node status: " + "isLaunching" + "mediaNodeId = " + mediaNodeId);
		return false;
	}

	@Override
	public boolean isCanceled(String mediaNodeId) {
		log.info("Media node status: " + "isCanceled" + "mediaNodeId = " + mediaNodeId);
		return false;
	}

	@Override
	public boolean isRunning(String mediaNodeId) {
		log.info("Media node status: " + "isRunning" + "mediaNodeId = " + mediaNodeId);
		return true;
	}

	@Override
	public boolean isTerminating(String mediaNodeId) {
		log.info("Media node status: " + "isTerminating" + "mediaNodeId = " + mediaNodeId);
		return false;
	}

	@Override
	public boolean isWaitingIdleToTerminate(String mediaNodeId) {
		log.info("Media node status: " + "isWaitingIdleToTerminate" + "mediaNodeId = " + mediaNodeId);
		return false;
	}

}
