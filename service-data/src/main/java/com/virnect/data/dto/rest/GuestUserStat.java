package com.virnect.data.dto.rest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GuestUserStat {

	private int totalSeatUser;
	private int allocateSeatUserTotal;
	private int deallocateSeatUserTotal;

	@Override
	public String toString() {
		return "GuestUserStat{" +
			"totalSeatUser=" + totalSeatUser +
			", allocateSeatUserTotal=" + allocateSeatUserTotal +
			", deallocateSeatUserTotal=" + deallocateSeatUserTotal +
			'}';
	}
}
