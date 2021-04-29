package com.virnect.content.event;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.content.dao.contentdonwloadlog.ContentDownloadLogRepository;
import com.virnect.content.domain.Content;
import com.virnect.content.domain.ContentDownloadLog;

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
    private final ContentDownloadLogRepository contentDownloadLogRepository;

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
        ContentDownloadLog contentDownloadLog = ContentDownloadLog.
                builder()
                .workspaceUUID(content.getWorkspaceUUID())
                .contentName(content.getName())
                .contentSize(content.getSize())
                .contentUploader(content.getUserUUID())
                .downloader(event.getDownloader())
                .build();
        contentDownloadLogRepository.save(contentDownloadLog);
    }
}
