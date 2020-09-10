package com.virnect.content.application.process;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.virnect.content.dto.rest.ProcessInfoResponse;
import com.virnect.content.global.common.ApiResponse;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-SMIC_CUSTOM
 * @email practice1356@gmail.com
 * @description Process Rest Client Service Interface
 * @since 2020.03.13
 */

@FeignClient(name = "process-server")
public interface ProcessRestService {

	@GetMapping("/processes/target/{targetId}")
	ApiResponse<ProcessInfoResponse> getProcessInfo(@PathVariable("taskId") Long taskId);
}
