package com.virnect.content.infra.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;

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
    private String HOST_REGEX = "^(http://|https://)([0-9.A-Za-z]+):[0-9]+/contents/";

    @Override
    public boolean copyFileWithUrl(final String sourceFullUrl, final String destinationUrl) throws IOException {
        Path destinationPath = Paths.get(convertHostUrl(destinationUrl));

        FileUtils.copyFile(new File(convertHostUrl(sourceFullUrl)), new File(destinationUrl));

        // check file exists and isReadable
        return Files.exists(destinationPath) && Files.isReadable(destinationPath);
    }

    @Override
    public boolean copyFileWithFile(final File sourceFile, final String destinationUrl) throws IOException {
        File destinationFile = new File(convertHostUrl(destinationUrl));

        FileUtils.copyFile(sourceFile, destinationFile);

        Path destinationPath = Paths.get(destinationFile.getPath());

        // check file exists and isReadable
        return Files.exists(destinationPath) && Files.isReadable(destinationPath);
    }

    @Override
    public boolean rename(final String sourceUrl, final String destinationUrl) throws IOException {
        String sourceHostUrl = convertHostUrl(sourceUrl);
        String destinationHostUrl = convertHostUrl(destinationUrl);

        FileUtils.moveFile(
                FileUtils.getFile(sourceHostUrl)
                , FileUtils.getFile(destinationHostUrl)
        );

        Path destinationPath = Paths.get(destinationHostUrl);

        // check file exists and isReadable
        return Files.exists(destinationPath) && Files.isReadable(destinationPath);
    }

    private String convertHostUrl(final String sourceUrl) {
        String result = sourceUrl.replaceAll(HOST_REGEX, "").replace(Matcher.quoteReplacement(File.separator), "/");
        log.debug("CONVERT FILE PATH : {}", result);
        return result;
    }
}
