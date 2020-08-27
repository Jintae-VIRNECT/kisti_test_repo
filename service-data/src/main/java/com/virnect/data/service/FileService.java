package com.virnect.data.service;

import com.virnect.data.dao.File;
import com.virnect.data.dto.file.request.FileUploadRequest;
import com.virnect.data.repository.FileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly=true)
public class FileService {
    private static final String TAG = FileService.class.getSimpleName();

    private final FileRepository fileRepository;

    @Transactional
    public File registerFile(FileUploadRequest fileUploadRequest, String filePath) {
        File file = File.builder()
                .workspaceId(fileUploadRequest.getWorkspaceId())
                .sessionId(fileUploadRequest.getSessionId())
                .uuid(fileUploadRequest.getUserId())
                .name(fileUploadRequest.getFile().getOriginalFilename())
                .size(fileUploadRequest.getFile().getSize())
                .path(filePath)
                .deleted(false)
                .build();
        return fileRepository.save(file);
    }

    public File getFile(String workspaceId, String sessionId, String name) {
        return this.fileRepository.findByWorkspaceIdAndSessionIdAndName(workspaceId, sessionId, name).orElse(null);
    }

    public Page<File> getFileList(String workspaceId, String sessionId, Pageable pageable, boolean isDeleted) {
        if(isDeleted)
            return this.fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(workspaceId, sessionId, pageable);
        else
            return this.fileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId, pageable);
    }

    @Transactional
    public void deleteFile(File file, boolean drop) {
        if(drop) {
            fileRepository.delete(file);
        } else {
            file.setDeleted(true);
            fileRepository.save(file);
        }
    }
}
