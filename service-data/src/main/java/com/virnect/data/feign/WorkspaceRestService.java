package com.virnect.data.feign;


import com.virnect.data.ApiResponse;
import com.virnect.data.dto.rest.WorkspaceMemberInfoListResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

//url = "http://192.168.6.3:8082"
@FeignClient(name = "workspace-server", url = "http://192.168.6.3:8082", fallbackFactory = WorkspaceRestFallbackFactory.class)
public interface WorkspaceRestService {
    @GetMapping("/workspaces/{workspaceId}/members")
    ApiResponse<WorkspaceMemberInfoListResponse> getWorkspaceMemberInfoList(
            @PathVariable("workspaceId") String workspaceId,
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
            );
}
