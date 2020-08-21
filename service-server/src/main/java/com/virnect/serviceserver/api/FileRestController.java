package com.virnect.serviceserver.api;

import com.virnect.data.ApiResponse;
import com.virnect.data.api.IFileRestAPI;
import com.virnect.data.dto.file.request.FileUploadRequest;
import com.virnect.data.dto.file.response.FileUploadResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.error.exception.RestServiceException;
import com.virnect.serviceserver.data.DataRepository;
import com.virnect.serviceserver.data.FileDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileRestController implements IFileRestAPI {
    private static final String TAG = FileRestController.class.getSimpleName();
    private static String PARAMETER_LOG_MESSAGE = "[PARAMETER ERROR]:: {}";
    private static final String REST_PATH = "/remote/file";

    private final FileDataRepository fileDataRepository;

    @Override
    public ResponseEntity<ApiResponse<FileUploadResponse>> fileUploadRequestHandler(@Valid FileUploadRequest fileUploadRequest, BindingResult result) {
        log.info("REST API: POST {}/upload", REST_PATH);
        if(result.hasErrors()) {
            result.getAllErrors().forEach(message -> log.error(PARAMETER_LOG_MESSAGE, message));
            throw new RestServiceException(ErrorCode.ERR_INVALID_REQUEST_PARAMETER);
        }
        ApiResponse<FileUploadResponse> apiResponse = this.fileDataRepository.uploadFile(fileUploadRequest);
        log.info("[FILE UPLOAD RESPONSE] :: {}", apiResponse.getData().toString());

        return ResponseEntity.ok(apiResponse);
    }
}
