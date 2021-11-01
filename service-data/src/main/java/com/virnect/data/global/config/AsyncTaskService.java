package com.virnect.data.global.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.concurrent.ListenableFuture;

import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.obj.BufferStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dao.file.FileRepository;
import com.virnect.data.domain.file.FileConvertStatus;
import com.virnect.data.dto.UploadResult;
import com.virnect.data.dto.request.file.FileUploadRequest;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.global.util.obj2gltf.ConvertObjToGltf;
import com.virnect.data.infra.file.IFileManagementService;
import com.virnect.data.infra.utils.FileUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncTaskService {

    private final FileUtil fileUtil;
    private final ConvertObjToGltf convertObjToGltf;
    private final IFileManagementService fileManagementService;
    private final FileRepository fileRepository;

    @Async
    public void covertObj2Gltf(FileUploadRequest fileUploadRequest, String objectName) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        File originalFile = fileUtil.convertUploadMultiFileToFile(fileUploadRequest.getFile());

        GltfAsset gltfAsset = convertObjToGltf.createGltfAsset(BufferStrategy.BUFFER_PER_FILE, originalFile);
        File gltfFile = convertObjToGltf.extractGltf(gltfAsset, objectName);
        File binFile = convertObjToGltf.extractBin(gltfAsset);

        saveObjectFile(fileUploadRequest, gltfFile, binFile);

        originalFile.delete();
        gltfFile.delete();
        binFile.delete();
    }

    public void saveObjectFile(
        FileUploadRequest fileUploadRequest,
        File gltfFile,
        File binFile
    ) throws IOException, NoSuchAlgorithmException, InvalidKeyException {

        com.virnect.data.domain.file.File gltf = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(
            fileUploadRequest.getWorkspaceId(),
            fileUploadRequest.getSessionId(),
            gltfFile.getName()).orElse(null);

        com.virnect.data.domain.file.File bin = fileRepository.findByWorkspaceIdAndSessionIdAndObjectName(
            fileUploadRequest.getWorkspaceId(),
            fileUploadRequest.getSessionId(),
            "buffer0.bin" + "_" + FilenameUtils.removeExtension(gltfFile.getName())).orElse(null);

        InputStream gltfFileInputStream = new FileInputStream(gltfFile);
        InputStream binFileInputStream = new FileInputStream(binFile);

        if (upload3dFileToStorage(gltfFileInputStream, fileUploadRequest, gltfFile.getName(), gltfFile.getName()) == ErrorCode.ERR_SUCCESS) {
            log.info("[Thread : Obj to glft Processing] Object file(.glft) upload success");
            gltf.setSize(gltfFile.length());
            updateFileConvertStatus(gltf, FileConvertStatus.CONVERTED);

        } else {
            log.info("[Thread : Obj to glft Processing] Object file(.glft) upload fail");
            updateFileConvertStatus(gltf, FileConvertStatus.CONVERTING_FAIL);
        }

        if (upload3dFileToStorage(binFileInputStream, fileUploadRequest, binFile.getName(), gltfFile.getName()) == ErrorCode.ERR_SUCCESS) {
            log.info("[Thread : Obj to glft Processing] Object file(.glft) upload success");
            bin.setSize(binFile.length());
            updateFileConvertStatus(bin, FileConvertStatus.CONVERTED);
        } else {
            log.info("[Thread : Obj to glft Processing] Object file(.glft) upload fail");
            updateFileConvertStatus(bin, FileConvertStatus.CONVERTING_FAIL);
        }

        gltfFileInputStream.close();
        binFileInputStream.close();
    }

    private ErrorCode upload3dFileToStorage(
        InputStream inputStream,
        FileUploadRequest objectFileUploadRequest,
        String objectName,
        String objectPath
    ) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        UploadResult uploadResult = fileManagementService.objectFileUpload(
            inputStream,
            fileUtil.generateDirPath(objectFileUploadRequest.getWorkspaceId(),
                objectFileUploadRequest.getSessionId()),
            objectFileUploadRequest.getFile().getOriginalFilename(),
            objectName,
            objectPath
        );
        return uploadResult.getErrorCode();
    }

    private void updateFileConvertStatus(
        com.virnect.data.domain.file.File file,
        FileConvertStatus fileConvertStatus
    ) {
        file.setFileConvertStatus(fileConvertStatus);
        if (ObjectUtils.isEmpty(fileRepository.save(file))) {
            log.info("[Thread : Obj to glft Processing] file converting status update fail : " + fileConvertStatus.toString());
        }
    }

}
