package com.virnect.serviceserver.infra.utils;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;

public class ConfigValidation {

	public URI checkWebsocketUri(String uri) throws Exception {
		try {
			if (!uri.startsWith("ws://") || uri.startsWith("wss://")) {
				throw new Exception("WebSocket protocol not found");
			}
			String parsedUri = uri.replaceAll("ws://", "http://").replaceAll("wss://", "https://");
			return new URL(parsedUri).toURI();
		} catch (Exception e) {
			throw new RuntimeException(
				"URI '" + uri + "' has not a valid WebSocket endpoint format: " + e.getMessage());
		}
	}

	public List<String> asUriConference(List<String> urisConference) {
		for (String uri : urisConference) {
			try {
				this.checkWebsocketUri(uri).toString();
			} catch (Exception e) {
				//addError(property, uri + " is not a valid WebSocket URL");
			}
		}
		return urisConference;
	}

	public static String asFileSystemPath(String stringPath) {
		try {
			Paths.get(stringPath);
			File f = new File(stringPath);
			f.getCanonicalPath();
			f.toURI().toString();
			stringPath = stringPath.endsWith("/") ? stringPath : (stringPath + "/");
			return stringPath;
		} catch (Exception e) {
			//addError(stringPath, "Is not a valid file system path. " + e.getMessage());
			return null;
		}
	}

	public static String asWritableFileSystemPath(String stringPath) {
		try {
			Paths.get(stringPath);
			File f = new File(stringPath);
			// set all permission granted
            /*f.setReadable(true, false);
            f.setWritable(true, false);
            f.setExecutable(true, false);*/

			f.getCanonicalPath();
			f.toURI().toString();
			if (!f.exists()) {
				if (!f.mkdirs()) {
					throw new Exception(
						"The path does not exist and RemoteService Server does not have enough permissions to create it");
				}
			}
			if (!f.canWrite()) {
				throw new Exception(
					"RemoteService Server does not have permissions to write on path " + f.getCanonicalPath());
			}
			stringPath = stringPath.endsWith("/") ? stringPath : (stringPath + "/");
			return stringPath;
		} catch (Exception e) {
			//addError(stringPath, "Is not a valid writable file system path. " + e.getMessage());
			return null;
		}
	}
}
