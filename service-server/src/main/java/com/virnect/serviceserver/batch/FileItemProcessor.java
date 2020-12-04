package com.virnect.serviceserver.batch;

import com.virnect.file.dao.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDateTime;


/*public class FileItemProcessor implements ItemProcessor<File, File> {
    private static final Logger log = LoggerFactory.getLogger(FileItemProcessor.class);

    @Override
    public File process(File item) throws Exception {
        LocalDateTime expirationDate = item.getExpirationDate();


        //log.info("Converting (" + item + ") into (" + transformedPerson + ")");
        return null;
    }
}*/
