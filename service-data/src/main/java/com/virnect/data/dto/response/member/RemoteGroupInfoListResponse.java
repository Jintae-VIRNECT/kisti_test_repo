package com.virnect.data.dto.response.member;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
@Builder
public class RemoteGroupInfoListResponse {

	private List<RemoteGroupInfoResponse> groupInfoResponseList;

}
