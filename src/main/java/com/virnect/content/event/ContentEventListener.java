package com.virnect.content.event;

import com.virnect.content.domain.Content;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description: Content Event Listener Class
 * @since 2020.03.05
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContentEventListener {

    @EventListener
    public void contentUpdateRollback(ContentUpdateFileRollbackEvent event) {
        log.info("Content Update Rollback Event For Content File");
        File file = event.getContentFile();
        try {
            FileUtils.copyFile(file, new File(file.getPath()));
        } catch (Exception e) {
            log.error("File Recover Event Fail => {}", e);
        }
        log.info("FileName: {} , Path: {} is Restored.", file.getName(), file.getPath());
    }


    @EventListener
    public void ContentDownloadHit(ContentDownloadHitEvent event) {
        Content content = event.getContent();
        log.info("CURRENT => Content: [{}] DownloadHits: [{}]", content.getName(), content.getDownloadHits());
        content.setDownloadHits(content.getDownloadHits() + 1);
        log.info("UPDATE => Content: [{}] DownloadHits: [{}]", content.getName(), content.getDownloadHits());
    }
}
