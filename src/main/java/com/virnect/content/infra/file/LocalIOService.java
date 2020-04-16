package com.virnect.content.infra.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
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
    public boolean copyFileWithUrl(final String sourceUrl, final String destinationUrl) {
        File targetFile = new File(sourceUrl);
        Path targetPath = targetFile.toPath();
        Path destinationPath = Paths.get(destinationUrl);
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

    @Override
    public boolean copyFileWithFile(final File sourceFile, final String destinationUrl) {
        File destinationFile = new File(destinationUrl);
        try {
            FileUtils.copyFile(sourceFile, destinationFile);
            log.info("FILE COPY - name : [{}], path : [{}]", destinationFile.getName(), destinationFile.getPath());
            Path destinationPath = Paths.get(destinationFile.getPath());
            // check file exists and isReadable
            return Files.exists(destinationPath) && Files.isReadable(destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("FILE COPY ERROR : {}", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean rename(final String sourceUrl, final String destinationUrl) {
        try {
            FileUtils.moveFile(
                    FileUtils.getFile(sourceUrl)
                    , FileUtils.getFile(destinationUrl)
            );
            Path destinationPath = Paths.get(destinationUrl);
            log.info("FILE RENAME - sourceUrl : [{}], destinationUrl : [{}], varificationName : [{}]"
                    , sourceUrl, destinationUrl, FileUtils.getFile(destinationUrl).getName());
            // check file exists and isReadable
            return Files.exists(destinationPath) && Files.isReadable(destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("FILE COPY ERROR : {}", e.getMessage());
        }
        return false;
    }
}
