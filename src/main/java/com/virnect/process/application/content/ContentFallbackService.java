package com.virnect.process.application.content;

import com.virnect.process.dto.rest.request.content.ContentStatusChangeRequest;
import com.virnect.process.dto.rest.response.content.ContentRestDto;
import com.virnect.process.dto.rest.response.content.ContentStatusInfoResponse;
import com.virnect.process.global.common.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public class ContentFallbackService implements ContentRestService {
    @Override
    public ApiResponse<ContentRestDto> getContentMetadata(String contentUUID) {
        return null;
    }

    @Override
    public ApiResponse<ContentStatusInfoResponse> changeContentStatus(ContentStatusChangeRequest contentStatusChangeRequest) {
        return null;
    }
}
