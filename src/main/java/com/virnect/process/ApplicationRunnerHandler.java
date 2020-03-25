package com.virnect.process;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-22
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: Application Runner Event Handler Class
 */

@Slf4j
@Service
public class ApplicationRunnerHandler implements ApplicationRunner {
    @Value("${upload.dir}")
    private String uploadDirectoryPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        File file = new File(uploadDirectoryPath);
        FileUtils.forceMkdir(file);
        log.info("FILE UPLOAD DIRECTORY CREATE: [{}]", file.getAbsolutePath());
    }
}
