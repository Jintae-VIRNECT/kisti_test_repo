package com.virnect.uaa.domain.auth.account.dao;

import java.util.List;

public interface CustomUserOTPRepository {
	long deleteAllByEmailIn(List<String> emailList);
}
