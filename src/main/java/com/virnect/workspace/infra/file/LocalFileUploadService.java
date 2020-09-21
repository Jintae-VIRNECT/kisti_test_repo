package com.virnect.workspace.infra.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import com.virnect.workspace.exception.WorkspaceException;
import com.virnect.workspace.global.error.ErrorCode;

/**
 * Project: PF-Admin
 * DATE: 2020-03-16
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Profile("local")
@Slf4j
@Service
public class LocalFileUploadService implements FileUploadService {

    @Value("${file.upload-path}")
    private String fileUploadPath;

    @Value("${file.url}")
    private String fileUrl;

    @Value("${file.allow-extension}")
    private String allowExtension;


    @Override
    public String upload(MultipartFile multipartFile) throws IOException {
        if (!allowExtension.contains(FilenameUtils.getExtension(multipartFile.getOriginalFilename()).toLowerCase())) {
            throw new WorkspaceException(ErrorCode.ERR_UNEXPECTED_SERVER_ERROR);
        }

        File convertFile = new File(fileUploadPath + multipartFile.getOriginalFilename());
        if (convertFile.createNewFile()) {
            FileOutputStream fos = new FileOutputStream(convertFile);
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        String filePath = fileUrl + convertFile.getPath();

        filePath = filePath.replace("\\", "/");
        log.info("SAVE FILE_URL: {}", filePath);

        return filePath;
    }

    @Override
    public void delete(String url) {
        File file = new File("./" + fileUploadPath + url);
        if (file.delete()) {
            log.info("{} 파일이 삭제되었습니다.", file.getName());
        } else {
            log.info("{} 파일을 삭제하지 못했습니다.", file.getName());
        }
    }

    @Override
    public String getFileExtension(String originFileName) {
        return null;
    }

    @Override
    public boolean isAllowFileExtension(String fileExtension) {
        return false;
    }
}
