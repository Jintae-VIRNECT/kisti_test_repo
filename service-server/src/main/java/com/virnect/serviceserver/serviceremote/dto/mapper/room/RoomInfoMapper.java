package com.virnect.serviceserver.serviceremote.dto.mapper.room;

import org.mapstruct.Mapper;

import com.virnect.data.domain.room.Room;
import com.virnect.data.dto.response.room.RoomInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface RoomInfoMapper extends GenericMapper<RoomInfoResponse, Room> {
}
