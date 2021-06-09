package com.virnect.serviceserver.serviceremote.dto.mapper;

import org.mapstruct.Mapper;

import com.virnect.data.domain.room.Room;
import com.virnect.data.dto.response.room.RoomDetailInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface RoomDetailMapper extends GenericMapper<RoomDetailInfoResponse, Room> {
}
