package com.virnect.workspace.event.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Project: PF-Workspace
 * DATE: 2021-10-22
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Getter
@RequiredArgsConstructor
public class GuestUserDeletedEvent {
	private final String receiveWorkspaceId;
	private final String sendUserId;
	private final String receiveUserId;
	private final List<String> guestUserProductList;

	@Override
	public String toString() {
		return "GuestUserDeletedEvent{" +
			"receiveWorkspaceId='" + receiveWorkspaceId + '\'' +
			", sendUserId='" + sendUserId + '\'' +
			", receiveUserId='" + receiveUserId + '\'' +
			", guestUserProductList=" + guestUserProductList +
			'}';
	}
}
