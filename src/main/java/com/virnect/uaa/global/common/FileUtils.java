package com.virnect.uaa.global.common;

import java.util.Arrays;
import java.util.List;

public class FileUtils {
	private static final List<String> PROFILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("jpg", "png", "JPG", "PNG");

	public static String getFileExtension(String fileOriginName) {
		int dotIndex = fileOriginName.lastIndexOf('.');
		return dotIndex == -1 ? "" : fileOriginName.substring(dotIndex + 1);
	}

	public static boolean isValidaExtension(String extension) {
		String checkExtension = getFileExtension(extension);
		return PROFILE_IMAGE_ALLOW_EXTENSION.contains(checkExtension);
	}
}
