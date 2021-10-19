package com.virnect.data.dto.response.group;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel
public class FavoriteGroupListResponse {

	private List<FavoriteGroupResponse> favoriteGroupResponses;

	@Builder
	public FavoriteGroupListResponse(
		List<FavoriteGroupResponse> favoriteGroupResponses
	) {
		this.favoriteGroupResponses = favoriteGroupResponses;
	}
}
