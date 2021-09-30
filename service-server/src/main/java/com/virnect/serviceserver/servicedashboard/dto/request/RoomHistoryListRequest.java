package com.virnect.serviceserver.servicedashboard.dto.request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import lombok.Getter;

import com.virnect.data.domain.OrderType;
import com.virnect.data.domain.roomhistory.RoomHistorySortType;
import com.virnect.data.domain.roomhistory.RoomHistoryStatusType;
import com.virnect.data.global.validation.CustomEnumValid;

@Getter
public class RoomHistoryListRequest {

	private String sessionId;

	@NotNull
	private boolean paging;

	@Min(0)
	private int page;

	@Min(1)
	private int size;

	private String fromTo;

	private String searchWord;

	@CustomEnumValid(enumClass = RoomHistorySortType.class)
	private RoomHistorySortType sortProperties;

	@CustomEnumValid(enumClass = OrderType.class)
	private OrderType sortOrder;

	@CustomEnumValid(enumClass = RoomHistoryStatusType.class)
	private RoomHistoryStatusType status;

	@Override
	public String toString() {
		return "OptionTest{" +
			"paging=" + paging +
			", page=" + page +
			", size=" + size +
			", fromTo='" + fromTo + '\'' +
			", searchWord='" + searchWord + '\'' +
			", sortName=" + sortProperties.name() +
			", orderType=" + sortOrder.name() +
			'}';
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public void setPaging(boolean paging) {
		this.paging = paging;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setFromTo(String fromTo) {
		this.fromTo = fromTo;
	}

	public void setSearchWord(String searchWord) {
		try {
			searchWord = URLDecoder.decode(searchWord, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			searchWord = null;
		}
		this.searchWord = searchWord;
	}

	public void setStatus(RoomHistoryStatusType status) {
		this.status = status;
	}

	public void setSortProperties(RoomHistorySortType sortProperties) {
		this.sortProperties = sortProperties;
	}

	public void setSortOrder(OrderType sortOrder) {
		this.sortOrder = sortOrder;
	}

	public boolean fromToDateConditionIsAvailable(){
		if(StringUtils.isEmpty(this.fromTo)){
			return false;
		}
		return fromTo.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2},[0-9]{4}-[0-9]{2}-[0-9]{2}$");
	}


	public LocalDateTime getSearchStartDate(){
		if(!this.fromToDateConditionIsAvailable()){
			return null;
		}
		return LocalDate.parse(this.fromTo.split(",")[0]).atTime(0,0,0);
	}

	public LocalDateTime getSearchEndDate(){
		if(!this.fromToDateConditionIsAvailable()){
			return null;
		}
		return LocalDate.parse(this.fromTo.split(",")[1]).atTime(0,0,0);
	}
}
