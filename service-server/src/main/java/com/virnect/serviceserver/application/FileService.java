package com.virnect.serviceserver.application;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.virnect.file.dao.File;
import com.virnect.file.dao.RecordFile;
import com.virnect.file.repository.FileRepository;
import com.virnect.file.repository.RecordFileRepository;
import com.virnect.serviceserver.dto.request.file.FileUploadRequest;
import com.virnect.serviceserver.dto.request.file.RecordFileUploadRequest;

@Slf4j
@Service
//@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional(readOnly=true)
public class FileService {
    private static final String TAG = FileService.class.getSimpleName();

    /*private final FileRepository fileRepository;
    private final RecordFileRepository recordFileRepository;*/

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private RecordFileRepository recordFileRepository;

    @Transactional
    public File registerFile(FileUploadRequest fileUploadRequest, String objectName) {
        File file = File.builder()
                .workspaceId(fileUploadRequest.getWorkspaceId())
                .sessionId(fileUploadRequest.getSessionId())
                .uuid(fileUploadRequest.getUserId())
                .name(fileUploadRequest.getFile().getOriginalFilename())
                .objectName(objectName)
                .contentType(fileUploadRequest.getFile().getContentType())
                .size(fileUploadRequest.getFile().getSize())
                .build();

        return fileRepository.save(file);
    }

    @Transactional
    public RecordFile registerRecordFile(RecordFileUploadRequest recordFileUploadRequest, String objectName) {
        RecordFile recordFile = RecordFile.builder()
                .workspaceId(recordFileUploadRequest.getWorkspaceId())
                .sessionId(recordFileUploadRequest.getSessionId())
                .uuid(recordFileUploadRequest.getUserId())
                .name(recordFileUploadRequest.getFile().getOriginalFilename())
                .objectName(objectName)
                .contentType(recordFileUploadRequest.getFile().getContentType())
                .size(recordFileUploadRequest.getFile().getSize())
                .durationSec(recordFileUploadRequest.getDurationSec())
                .build();

        return recordFileRepository.save(recordFile);
    }

    public File getFileByName(String workspaceId, String sessionId, String name) {
        return this.fileRepository.findByWorkspaceIdAndSessionIdAndName(workspaceId, sessionId, name).orElse(null);
    }

    public File getFileByObjectName(String workspaceId, String sessionId, String objectName) {
        return this.fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
    }

    public RecordFile getRecordFileByObjectName(String workspaceId, String sessionId, String objectName) {
        return this.recordFileRepository.findByWorkspaceIdAndSessionIdAndObjectName(workspaceId, sessionId, objectName).orElse(null);
    }

    public Page<File> getFileList(String workspaceId, String sessionId, Pageable pageable, boolean isDeleted) {
        if(isDeleted)
            return this.fileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(workspaceId, sessionId, pageable);
        else
            return this.fileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId, pageable);
    }

    public List<File> getFileList(String workspaceId, String sessionId) {
        return this.fileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId);
    }

    public Page<RecordFile> getRecordFileList(String workspaceId, String sessionId, Pageable pageable, boolean isDeleted) {
        if(isDeleted)
            return this.recordFileRepository.findByWorkspaceIdAndSessionIdAndDeletedIsTrue(workspaceId, sessionId, pageable);
        else
            return this.recordFileRepository.findByWorkspaceIdAndSessionId(workspaceId, sessionId, pageable);
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

    @Transactional
    public void deleteFiles(String workspaceId, String sessionId) {
        fileRepository.deleteAllByWorkspaceIdAndSessionId(workspaceId, sessionId);
    }
}
