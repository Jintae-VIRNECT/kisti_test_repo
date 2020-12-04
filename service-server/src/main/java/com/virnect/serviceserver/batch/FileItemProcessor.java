package com.virnect.serviceserver.batch;

import com.virnect.file.dao.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;


public class FileItemProcessor implements ItemProcessor<File, File> {
    private static final Logger log = LoggerFactory.getLogger(FileItemProcessor.class);

    @Override
    public File process(File item) throws Exception {
        log.info("process");
        if(item.getExpirationDate().isBefore(LocalDateTime.now())) {
            log.info("Converting (" + item + ") to deleted");
            item.setDeleted(true);
        }
        return item;
    }
}
