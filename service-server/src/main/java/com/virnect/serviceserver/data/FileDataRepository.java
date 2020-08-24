package com.virnect.serviceserver.data;

import com.virnect.data.ApiResponse;
import com.virnect.data.dao.File;
import com.virnect.data.dto.file.request.FileDownloadRequest;
import com.virnect.data.dto.file.request.FileUploadRequest;
import com.virnect.data.dto.file.response.FileUploadResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.service.FileService;
import com.virnect.serviceserver.infra.file.LocalFileUploadService;
import com.virnect.serviceserver.infra.file.download.LocalFileDownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDataRepository {
    private static final String TAG = FileDataRepository.class.getSimpleName();

    private final FileService fileService;
    private final LocalFileUploadService localFileUploadService;
    private final LocalFileDownloadService localFileDownloadService;

    private final ModelMapper modelMapper;

    public ApiResponse<FileUploadResponse> uploadFile(FileUploadRequest fileUploadRequest) {
        return new RepoDecoder<File, FileUploadResponse>(RepoDecoderType.CREATE) {
            @Override
            File loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<FileUploadResponse> invokeDataProcess() {
                log.info("file upload: {}", fileUploadRequest.getUuid());
                StringBuilder stringBuilder;
                stringBuilder = new StringBuilder();
                stringBuilder.append(fileUploadRequest.getWorkspaceId()).append("/").append(fileUploadRequest.getSessionId()).append("/");
                String fileUploadPath = null;
                try {
                    fileUploadPath = localFileUploadService.upload(fileUploadRequest.getFile(), stringBuilder.toString());
                } catch (IOException exception) {
                    exception.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                if (fileUploadPath != null) {
                    File file = fileService.registerFile(fileUploadRequest, fileUploadPath);
                    if (file != null) {
                        FileUploadResponse fileUploadResponse = new FileUploadResponse();
                        fileUploadResponse = modelMapper.map(file, FileUploadResponse.class);
                        return new DataProcess<>(fileUploadResponse);
                    } else {
                        return new DataProcess<>(ErrorCode.ERR_FILE_UPLOAD_FAILED);
                    }
                } else {
                    return new DataProcess<>(ErrorCode.ERR_FILE_UPLOAD_FAILED);
                }
            }
        }.asApiResponse();
    }

    public DataProcess<ResponseEntity<byte[]>> downloadFile(FileDownloadRequest fileDownloadRequest) {
        return new RepoDecoder<File, ResponseEntity<byte[]>>(RepoDecoderType.READ) {
            @Override
            File loadFromDatabase() {
                return fileService.getFile(
                        fileDownloadRequest.getWorkspaceId(),
                        fileDownloadRequest.getSessionId(),
                        fileDownloadRequest.getFileName());
            }

            @Override
            DataProcess<ResponseEntity<byte[]>> invokeDataProcess() {
                File file = loadFromDatabase();
                log.info("file download: {}", file.getPath());
                try {
                    byte[] byteArray = localFileDownloadService.fileDownload(file.getPath());
                    String[] resources = file.getPath().split("/");
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                    httpHeaders.setContentLength(byteArray.length);
                    httpHeaders.setContentDispositionFormData("attachment", resources[2]);
                    return new DataProcess<>(new ResponseEntity<>(byteArray, httpHeaders, HttpStatus.OK));
                } catch (IOException exception) {
                    exception.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                return new DataProcess<>();

            }
        }.asResponseData();
    }
}
