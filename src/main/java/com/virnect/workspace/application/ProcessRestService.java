package com.virnect.workspace.application;

import com.virnect.workspace.dto.rest.SubProcessCountResponse;
import com.virnect.workspace.global.common.ApiResponse;
import com.virnect.workspace.global.config.NetflixFeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Project: PF-Workspace
 * DATE: 2020-03-24
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@FeignClient(name = "process-server", configuration = NetflixFeignConfiguration.class)
public interface ProcessRestService {
    @GetMapping("/subProcesses/count/onWorker/{workerUUID}")
    ApiResponse<SubProcessCountResponse> getSubProcessCount(@PathVariable("workerUUID") String workerUUID);

}
