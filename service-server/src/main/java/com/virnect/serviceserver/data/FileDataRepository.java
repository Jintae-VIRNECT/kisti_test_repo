package com.virnect.serviceserver.data;

import com.virnect.data.ApiResponse;
import com.virnect.data.dao.File;
import com.virnect.data.dao.Room;
import com.virnect.data.dto.PageMetadataResponse;
import com.virnect.data.dto.file.request.FileUploadRequest;
import com.virnect.data.dto.file.response.*;
import com.virnect.data.dto.request.RoomProfileUpdateRequest;
import com.virnect.data.dto.response.RoomProfileUpdateResponse;
import com.virnect.data.error.ErrorCode;
import com.virnect.data.service.FileService;
import com.virnect.data.service.SessionService;
import com.virnect.serviceserver.infra.file.Default;
import com.virnect.serviceserver.infra.file.IFileManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileDataRepository {
    private static final String TAG = FileDataRepository.class.getSimpleName();

    private final SessionService sessionService;
    private final FileService fileService;
    private final IFileManagementService fileManagementService;

    private final ModelMapper modelMapper;

    public ApiResponse<FileUploadResponse> uploadFile(FileUploadRequest fileUploadRequest) {
        return new RepoDecoder<File, FileUploadResponse>(RepoDecoderType.CREATE) {
            @Override
            File loadFromDatabase() {
                return null;
            }

            @Override
            DataProcess<FileUploadResponse> invokeDataProcess() {
                StringBuilder stringBuilder;
                stringBuilder = new StringBuilder();
                stringBuilder.append(fileUploadRequest.getWorkspaceId()).append("/").append(fileUploadRequest.getSessionId()).append("/");

                String objectName;
                try {
                    objectName = fileManagementService.upload(fileUploadRequest.getFile(), stringBuilder.toString());
                } catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
                    log.info("{}", exception.getMessage());
                    return new DataProcess<>(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION.getCode(), exception.getMessage());
                }

                File file = fileService.registerFile(fileUploadRequest, objectName);
                if (file != null) {
                    //fileUploadResponse = modelMapper.map(file, FileUploadResponse.class);
                    FileUploadResponse fileUploadResponse = new FileUploadResponse();
                    fileUploadResponse.setWorkspaceId(file.getWorkspaceId());
                    fileUploadResponse.setSessionId(file.getSessionId());
                    fileUploadResponse.setUserId(file.getUuid());
                    fileUploadResponse.setName(file.getName());
                    fileUploadResponse.setSize(file.getSize());
                    return new DataProcess<>(fileUploadResponse);
                } else {
                    return new DataProcess<>(ErrorCode.ERR_FILE_UPLOAD_FAILED);
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<RoomProfileUpdateResponse> uploadProfile(
            String workspaceId,
            String sessionId,
            RoomProfileUpdateRequest roomProfileUpdateRequest) {
        return new RepoDecoder<Room, RoomProfileUpdateResponse>(RepoDecoderType.UPDATE) {
            @Override
            Room loadFromDatabase() {
                return sessionService.getRoom(workspaceId, sessionId);
            }

            @Override
            DataProcess<RoomProfileUpdateResponse> invokeDataProcess() {
                log.info("ROOM INFO UPDATE PROFILE BY SESSION ID => [{}, {}]", workspaceId, sessionId);
                RoomProfileUpdateResponse profileUpdateResponse = new RoomProfileUpdateResponse();
                StringBuilder stringBuilder;
                stringBuilder = new StringBuilder();
                stringBuilder.append(workspaceId).append("/").append(sessionId).append("/");
                String profileUrl = Default.ROOM_PROFILE.getValue();

                Room room = loadFromDatabase();
                if(room != null) {
                    if(room.getLeaderId().equals(roomProfileUpdateRequest.getUuid())) {
                        if (roomProfileUpdateRequest.getProfile() != null) {
                            try {
                                profileUrl = fileManagementService.uploadProfile(roomProfileUpdateRequest.getProfile(), stringBuilder.toString());
                                fileManagementService.deleteProfile(room.getProfile());
                            } catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
                                log.info("{}", exception.getMessage());
                                return new DataProcess<>(ErrorCode.ERR_FILE_UPLOAD_EXCEPTION.getCode(), exception.getMessage());
                            }
                        }
                        profileUpdateResponse.setSessionId(sessionId);
                        profileUpdateResponse.setProfile(profileUrl);
                        sessionService.updateRoom(room, profileUrl);
                        return new DataProcess<>(profileUpdateResponse);
                    } else {
                        return new DataProcess<>(ErrorCode.ERR_ROOM_INVALID_PERMISSION);
                    }
                } else {
                    return new DataProcess<>(ErrorCode.ERR_ROOM_NOT_FOUND);
                }
            }
        }.asApiResponse();
    }

    //will be deprecated
    public DataProcess<ResponseEntity<byte[]>> downloadFile(
            String workspaceId,
            String sessionId,
            String userId,
            String objectName) {
        return new RepoDecoder<File, ResponseEntity<byte[]>>(RepoDecoderType.READ) {
            @Override
            File loadFromDatabase() {
                return fileService.getFileByObjectName(
                        workspaceId,
                        sessionId,
                        objectName);
            }

            @Override
            DataProcess<ResponseEntity<byte[]>> invokeDataProcess() {
                File file = loadFromDatabase();
                log.info("file download: {}", file.getObjectName());
                try {
                    StringBuilder stringBuilder;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(workspaceId).append("/")
                            .append(sessionId).append("/")
                            .append(file.getObjectName());
                    //byte[] byteArray = localFileDownloadService.fileDownload(stringBuilder.toString());
                    byte[] byteArray = fileManagementService.fileDownload(stringBuilder.toString());
                    //String[] resources = file.getPath().split("/");
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.setContentLength(byteArray.length);
                    httpHeaders.setContentDispositionFormData("attachment", file.getName());
                    switch (file.getContentType()) {
                        case MediaType.IMAGE_JPEG_VALUE:
                            httpHeaders.setContentType(MediaType.IMAGE_JPEG);
                            break;
                        case MediaType.IMAGE_GIF_VALUE:
                            httpHeaders.setContentType(MediaType.IMAGE_GIF);
                            break;
                        case MediaType.IMAGE_PNG_VALUE:
                            httpHeaders.setContentType(MediaType.IMAGE_PNG);
                            break;
                        default:
                            httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                            break;
                    }
                    return new DataProcess<>(new ResponseEntity<>(byteArray, httpHeaders, HttpStatus.OK));
                } catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
                    log.info("{}", exception.getMessage());
                    return new DataProcess<>(ErrorCode.ERR_FILE_DOWNLOAD_EXCEPTION);
                }
            }
        }.asResponseData();
    }

    public ApiResponse<FilePreSignedResponse> downloadFileUrl(String workspaceId,
                                                           String sessionId,
                                                           String userId,
                                                           String objectName) {
        return new RepoDecoder<File, FilePreSignedResponse>(RepoDecoderType.READ) {
            @Override
            File loadFromDatabase() {
                return fileService.getFileByObjectName(
                        workspaceId,
                        sessionId,
                        objectName);
            }

            @Override
            DataProcess<FilePreSignedResponse> invokeDataProcess() {
                File file = loadFromDatabase();
                log.info("file download: {}", file.getObjectName());
                try {
                    StringBuilder stringBuilder;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(workspaceId).append("/")
                            .append(sessionId).append("/")
                            .append(file.getObjectName());
                    int expiry = 60 * 60 *24; //one day
                    //String url = localFileDownloadService.filePreSignedUrl(stringBuilder.toString(), expiry);
                    String url = fileManagementService.filePreSignedUrl(stringBuilder.toString(), expiry);
                    FilePreSignedResponse filePreSignedResponse = new FilePreSignedResponse();
                    filePreSignedResponse.setWorkspaceId(file.getWorkspaceId());
                    filePreSignedResponse.setSessionId(file.getSessionId());
                    filePreSignedResponse.setName(file.getName());
                    filePreSignedResponse.setObjectName(file.getObjectName());
                    filePreSignedResponse.setContentType(file.getContentType());
                    filePreSignedResponse.setUrl(url);
                    filePreSignedResponse.setExpiry(expiry);
                    return new DataProcess<>(filePreSignedResponse);
                } catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
                    log.info("{}", exception.getMessage());
                    return new DataProcess<>(new FilePreSignedResponse(), ErrorCode.ERR_FILE_GET_SIGNED_EXCEPTION);
                }
            }
        }.asApiResponse();
    }

    public ApiResponse<FileInfoListResponse> getFileInfoList(
            String workspaceId,
            String sessionId,
            String userId,
            boolean deleted,
            Pageable pageable
    ) {
        return new RepoDecoder<Page<File>, FileInfoListResponse>(RepoDecoderType.READ) {
            @Override
            Page<File> loadFromDatabase() {
                return fileService.getFileList(workspaceId, sessionId, pageable, deleted);
            }

            @Override
            DataProcess<FileInfoListResponse> invokeDataProcess() {
                log.info("getFileInfoList");
                //File file = fileService.getFile(workspaceId,sessionId)
                Page<File> filePage = loadFromDatabase();

                for (File file: filePage.toList()) {
                    log.info("getFileInfoList : {}", file.getObjectName());
                }

                List<FileInfoResponse> fileInfoList = filePage.toList()
                        .stream()
                        .map(file -> modelMapper.map(file, FileInfoResponse.class))
                        .collect(Collectors.toList());

                // Page Metadata
                PageMetadataResponse pageMeta = PageMetadataResponse.builder()
                        .currentPage(pageable.getPageNumber())
                        .currentSize(pageable.getPageSize())
                        .totalPage(filePage.getTotalPages())
                        .totalElements(filePage.getNumberOfElements())
                        .build();

                return new DataProcess<>(new FileInfoListResponse(fileInfoList, pageMeta));
            }
        }.asApiResponse();
    }

    public ApiResponse<FileDeleteResponse> removeFile(String workspaceId,
                                                      String sessionId,
                                                      String userId,
                                                      String objectName) {
        return new RepoDecoder<File, FileDeleteResponse>(RepoDecoderType.DELETE) {
            @Override
            File loadFromDatabase() {
                return fileService.getFileByObjectName(workspaceId, sessionId, objectName);
            }

            @Override
            DataProcess<FileDeleteResponse> invokeDataProcess() {
                File file = loadFromDatabase();
                fileService.deleteFile(file, false);

                //remove object
                boolean result = false;
                try {
                    StringBuilder stringBuilder;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(workspaceId).append("/")
                            .append(sessionId).append("/")
                            .append(file.getObjectName());
                    result = fileManagementService.removeObject(stringBuilder.toString());
                } catch (IOException | NoSuchAlgorithmException | InvalidKeyException exception) {
                    exception.printStackTrace();
                    log.info("{}", exception.getMessage());
                    return new DataProcess<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_DELETE_EXCEPTION);
                }
                if(result) {
                    FileDeleteResponse fileDeleteResponse = new FileDeleteResponse();
                    fileDeleteResponse.setWorkspaceId(file.getWorkspaceId());
                    fileDeleteResponse.setSessionId(file.getSessionId());
                    fileDeleteResponse.setFileName(file.getName());
                    return new DataProcess<>(fileDeleteResponse);
                } else {
                    return new DataProcess<>(new FileDeleteResponse(), ErrorCode.ERR_FILE_DELETE_FAILED);
                }

            }
        }.asApiResponse();
    }
}
