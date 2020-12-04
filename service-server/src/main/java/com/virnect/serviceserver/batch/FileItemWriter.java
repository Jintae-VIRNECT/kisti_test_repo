package com.virnect.serviceserver.batch;

import com.virnect.file.dao.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

public class FileItemWriter implements ItemWriter<File> {
    private static final Logger log = LoggerFactory.getLogger(FileItemProcessor.class);

    @Override
    public void write(List<? extends File> items) throws Exception {
        log.info("FileItemWriter");
    }
}
