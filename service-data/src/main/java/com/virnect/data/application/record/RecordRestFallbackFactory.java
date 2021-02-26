package com.virnect.data.application.record;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import com.virnect.data.dto.rest.RecordServerFileInfoListResponse;
import com.virnect.data.dto.rest.SuccessResponse;
import com.virnect.data.dto.rest.StopRecordingResponse;
import com.virnect.data.global.common.ApiResponse;

@Slf4j
@Component
public class RecordRestFallbackFactory implements FallbackFactory<RecordRestService> {

    @Override
    public RecordRestService create(Throwable cause) {
        log.info(cause.getMessage(), cause);
        return new RecordRestService() {
            @Override
            public ApiResponse<StopRecordingResponse> stopRecordingBySessionId(String workspaceId, String userId, String sessionId) {
                log.info("[RECORD API FALLBACK] => USER_ID: {}", userId);
                StopRecordingResponse empty = new StopRecordingResponse();
                return new ApiResponse<>(empty);
            }

            @Override
            public ApiResponse<RecordServerFileInfoListResponse> getServerRecordFileList(
                String workspaceId, String userId
            ) {
                log.error(
                    "[USER REMOTE RECORD SERVER INFO LIST API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId,
                    cause.getMessage()
                );
                RecordServerFileInfoListResponse empty = new RecordServerFileInfoListResponse();
                empty.setInfos(new ArrayList<>());
                return new ApiResponse<RecordServerFileInfoListResponse>(empty);
            }

            @Override
            public ApiResponse<RecordServerFileInfoListResponse> getServerRecordFileList(
                String workspaceId, String userId, String sessionId
            ) {
                log.error(
                    "[USER REMOTE RECORD SERVER INFO LIST API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId,
                    cause.getMessage()
                );
                RecordServerFileInfoListResponse empty = new RecordServerFileInfoListResponse();
                empty.setInfos(new ArrayList<>());
                return new ApiResponse<RecordServerFileInfoListResponse>(empty);
            }

            @Override
            public ApiResponse<RecordServerFileInfoListResponse> getServerRecordFileList(
                String workspaceId,
                String userId,
                String order,
                String sessionId
            ) {
                log.error(
                    "[USER REMOTE RECORD SERVER INFO LIST API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId,
                    cause.getMessage()
                );
                RecordServerFileInfoListResponse empty = new RecordServerFileInfoListResponse();
                empty.setInfos(new ArrayList<>());
                return new ApiResponse<RecordServerFileInfoListResponse>(empty);
            }

            @Override
            public ApiResponse<Object> deleteServerRecordFile(
                String workspaceId, String userId, String id
            ) {
                log.error(
                    "[USER REMOTE RECORD SERVER DELETE RECORD FILE API FALLBACK] => WORKSAPCE_ID: {}, {}", workspaceId,
                    cause.getMessage()
                );
                SuccessResponse empty = new SuccessResponse();
                empty.setData(null);
                return new ApiResponse<Object>(empty);
            }

            @Override
            public ApiResponse<String> getServerRecordFileDownloadUrl(
                String workspaceId, String userId, String id
            ) {
                log.error(
                    "[USER REMOTE RECORD SERVER DOWNLOAD RECORD FILE URL API FALLBACK] => WORKSAPCE_ID: {}, {}",
                    workspaceId, cause.getMessage()
                );
                SuccessResponse empty = new SuccessResponse();
                empty.setData(null);
                return new ApiResponse<String>();
            }

        };



    }
}
