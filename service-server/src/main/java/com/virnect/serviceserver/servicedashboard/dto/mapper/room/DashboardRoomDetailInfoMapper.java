package com.virnect.serviceserver.servicedashboard.dto.mapper.room;

import org.mapstruct.Mapper;

import com.virnect.data.domain.room.Room;
import com.virnect.data.infra.utils.GenericMapper;
import com.virnect.serviceserver.servicedashboard.dto.response.RoomDetailInfoResponse;

@Mapper(componentModel = "spring")
public interface DashboardRoomDetailInfoMapper extends GenericMapper<RoomDetailInfoResponse, Room> {
}
