package com.virnect.content.application.process;

import com.virnect.content.dto.response.ProcessTargetInfoResponse;
import com.virnect.content.global.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description Process Rest Client Service Interface
 * @since 2020.03.13
 */

@FeignClient(name = "process-service", url = "${process.serverUrl}")
public interface ProcessRestService {

    @GetMapping("/target/{targetId}")
    ApiResponse<ProcessTargetInfoResponse> getProcessInfoByTargetValue(@PathVariable("targetId") Long targetId);
}
