package com.virnect.serviceserver.servicedashboard.dto.mapper.room;

import org.mapstruct.Mapper;

import com.virnect.data.domain.room.Room;
import com.virnect.data.infra.utils.GenericMapper;
import com.virnect.serviceserver.servicedashboard.dto.response.RoomHistoryInfoResponse;

@Mapper(componentModel = "spring")
public interface OngoingRoomInfoMapper extends GenericMapper<RoomHistoryInfoResponse, Room> {
}
