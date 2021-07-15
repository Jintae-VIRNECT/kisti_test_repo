package com.virnect.serviceserver.servicedashboard.dto.mapper.roomhistory;

import org.mapstruct.Mapper;

import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.infra.utils.GenericMapper;
import com.virnect.serviceserver.servicedashboard.dto.response.RoomHistoryInfoResponse;

@Mapper(componentModel = "spring")
public interface DashboardRoomHistoryInfoMapper extends GenericMapper<RoomHistoryInfoResponse, RoomHistory> {
}
