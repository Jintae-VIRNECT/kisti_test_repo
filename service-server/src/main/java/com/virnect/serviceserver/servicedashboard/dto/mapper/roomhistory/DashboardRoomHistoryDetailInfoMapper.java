package com.virnect.serviceserver.servicedashboard.dto.mapper.roomhistory;

import org.mapstruct.Mapper;

import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.infra.utils.GenericMapper;
import com.virnect.serviceserver.servicedashboard.dto.response.RoomHistoryDetailInfoResponse;

@Mapper(componentModel = "spring")
public interface DashboardRoomHistoryDetailInfoMapper extends GenericMapper<RoomHistoryDetailInfoResponse, RoomHistory> {
}
