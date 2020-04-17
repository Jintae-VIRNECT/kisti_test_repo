package com.virnect.process.application.content;

import com.virnect.process.dto.rest.request.content.ContentStatusChangeRequest;
import com.virnect.process.dto.rest.response.content.ContentRestDto;
import com.virnect.process.dto.rest.response.content.ContentStatusInfoResponse;
import com.virnect.process.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "content-server")
public interface ContentRestService {
    @GetMapping("/metadata")
    ApiResponse<ContentRestDto> getContentMetadata(@RequestParam("contentUUID") String contentUUID);

    @PostMapping("/status")
    ApiResponse<ContentStatusInfoResponse> changeContentStatus(ContentStatusChangeRequest contentStatusChangeRequest);
}
