package com.virnect.content.infra.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.14
 */
@Slf4j
@Service
public class LocalIOService implements FileIOService {
    @Override
    public boolean copyFile(final String targetUrl, final String destinationaUrl) {
        File targetFile = new File(targetUrl);
        Path targetPath = targetFile.toPath();
        Path destinationPath = Paths.get(destinationaUrl);
        try {
            destinationPath = Files.copy(targetPath, destinationPath, StandardCopyOption.COPY_ATTRIBUTES);
            // check file exists and isReadable
            return Files.exists(destinationPath) && Files.isReadable(destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("FILE COPY ERROR: {}", e.getMessage());
        }
        return false;
    }
}
