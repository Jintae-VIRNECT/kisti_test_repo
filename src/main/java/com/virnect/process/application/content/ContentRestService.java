package com.virnect.process.application.content;

import com.virnect.process.domain.TargetType;
import com.virnect.process.domain.YesOrNo;
import com.virnect.process.dto.rest.request.content.ContentStatusChangeRequest;
import com.virnect.process.dto.rest.response.content.*;
import com.virnect.process.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "ContentServerRestClient", url = "${content.serverUrl}")
public interface ContentRestService {
    @GetMapping("/metadata")
    ApiResponse<ContentRestDto> getContentMetadata(@RequestParam("contentUUID") String contentUUID);

    @PutMapping("/convert/{contentUUID}")
    ApiResponse<ContentInfoResponse> contentConvertHandler(@PathVariable("contentUUID") String contentUUID, @RequestParam("converted") YesOrNo converted);

    @GetMapping("/convertTask/{taskId}")
    ApiResponse<ContentUploadResponse> taskToContentConvertHandler(@PathVariable("taskId") Long taskId, @RequestParam(value = "userUUID") String userUUID);

    @DeleteMapping
    ApiResponse<ContentDeleteListResponse> contentDeleteRequestHandler(@RequestParam(value = "contentUUID") String[] contentUUIDs, @RequestParam(value = "workerUUID") String workerUUID);

    @PostMapping("/status")
    ApiResponse<ContentStatusInfoResponse> changeContentStatus(ContentStatusChangeRequest contentStatusChangeRequest);

    @PostMapping("/duplicate/{contentUUID}")
    ApiResponse<ContentUploadResponse> contentDuplicate(
            @PathVariable("contentUUID") String contentUUID
            , @RequestParam("workspaceUUID") String workspaceUUID
            , @RequestParam("userUUID") String userUUID);

    @GetMapping("/{contentUUID}")
    ApiResponse<ContentInfoResponse> getContentInfo(@PathVariable("contentUUID") String contentUUID);
}
