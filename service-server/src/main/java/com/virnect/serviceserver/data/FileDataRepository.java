package com.virnect.serviceserver.data;

import com.virnect.data.ApiResponse;
import com.virnect.data.dao.File;
import com.virnect.data.dto.file.request.FileUploadRequest;
import com.virnect.data.dto.file.response.FileUploadResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.service.FileService;
import com.virnect.serviceserver.infra.file.LocalFileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDataRepository {
    private static final String TAG = FileDataRepository.class.getSimpleName();

    private final FileService fileService;
    private final LocalFileUploadService localFileUploadService;

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

                String fileUploadPath = localFileUploadService.upload();

                return new DataProcess<>(ErrorCode.ERR_FILE_UPLOAD_FAILED);

                /*File file = fileService.registerFile(fileUploadRequest, fileUploadPath);
                if(file != null) {
                    FileUploadResponse fileUploadResponse = new FileUploadResponse();
                    fileUploadResponse = modelMapper.map(file, FileUploadResponse.class);
                    return new DataProcess<>(fileUploadResponse);
                } else {
                    return new DataProcess<>(ErrorCode.ERR_FILE_UPLOAD_FAILED);
                }*/
            }
        }.asApiResponse();
    }
}
