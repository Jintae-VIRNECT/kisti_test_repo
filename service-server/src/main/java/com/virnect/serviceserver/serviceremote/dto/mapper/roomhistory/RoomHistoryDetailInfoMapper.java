package com.virnect.serviceserver.serviceremote.dto.mapper.roomhistory;

import org.mapstruct.Mapper;

import com.virnect.data.domain.roomhistory.RoomHistory;
import com.virnect.data.dto.response.room.RoomHistoryDetailInfoResponse;
import com.virnect.data.infra.utils.GenericMapper;

@Mapper(componentModel = "spring")
public interface RoomHistoryDetailInfoMapper extends GenericMapper<RoomHistoryDetailInfoResponse, RoomHistory> {
}
