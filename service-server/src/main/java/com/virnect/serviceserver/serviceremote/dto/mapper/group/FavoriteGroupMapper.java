package com.virnect.serviceserver.serviceremote.dto.mapper.group;

import org.mapstruct.Mapper;

import com.virnect.data.domain.group.FavoriteGroup;
import com.virnect.data.dto.response.group.FavoriteGroupResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface FavoriteGroupMapper extends GenericMapper<FavoriteGroupResponse, FavoriteGroup> {
}
