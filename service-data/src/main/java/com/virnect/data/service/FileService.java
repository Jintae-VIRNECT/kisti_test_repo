package com.virnect.data.service;

import com.virnect.data.dao.File;
import com.virnect.data.dto.file.request.FileUploadRequest;
import com.virnect.data.repository.FileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .uuid(fileUploadRequest.getUuid())
                .name(fileUploadRequest.getFile().getName())
                .size(fileUploadRequest.getFile().getSize())
                .path(filePath)
                .deleted(false)
                .build();

        return fileRepository.save(file);
    }
}
