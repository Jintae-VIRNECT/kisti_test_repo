package com.virnect.serviceserver.batch;

import com.mysema.commons.lang.Assert;
import com.virnect.file.dao.File;
import com.virnect.file.repository.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

//
public class FileItemReader extends RepositoryItemReader<File> {
    private static final Logger log = LoggerFactory.getLogger(FileItemProcessor.class);

    @Autowired
    private FileRepository fileRepository;



    @Override
    protected List<File> doPageRead() throws Exception {
        return super.doPageRead();
    }

    /*@Override
    public File read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        log.info("FileItemReader");
        fileRepository.findAll()
        return null;
    }*/
}
