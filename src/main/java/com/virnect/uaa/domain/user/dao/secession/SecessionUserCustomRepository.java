package com.virnect.uaa.domain.user.dao.secession;

import java.util.List;

import com.virnect.uaa.domain.user.domain.SecessionUser;

public interface SecessionUserCustomRepository {
	List<SecessionUser> findSecessionUserInformationByUUIDListWithSearchQuery(List<String> uuidList, String search);
}
