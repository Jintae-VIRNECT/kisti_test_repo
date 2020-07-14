package com.virnect.remoteserviceserver.utils;

public interface MediaNodeStatusManager {

	public boolean isLaunching(String mediaNodeId);

	public boolean isCanceled(String mediaNodeId);

	public boolean isRunning(String mediaNodeId);

	public boolean isTerminating(String mediaNodeId);

	public boolean isWaitingIdleToTerminate(String mediaNodeId);

}
