package com.virnect.uaa.domain.auth.dao.user;

import java.util.List;

import org.springframework.stereotype.Component;

import com.warrenstrange.googleauth.ICredentialRepository;

import lombok.RequiredArgsConstructor;

import com.virnect.uaa.domain.auth.domain.user.UserOTP;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-Auth
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.15
 */
@Component
@RequiredArgsConstructor
public class CredentialRepository implements ICredentialRepository {
	private final UserOTPRepository userOTPRepository;

	@Override

	public String getSecretKey(String email) {
		return userOTPRepository.findByEmail(email).getSecretKey();
	}

	@Override
	public void saveUserCredentials(
		String email,
		String secretKey,
		int validationCode,
		List<Integer> scratchCodes
	) {
		UserOTP userOTP = new UserOTP();
		userOTP.setEmail(email);
		userOTP.setSecretKey(secretKey);
		userOTP.setValidationCode(validationCode);
		userOTP.setScratchCodes(scratchCodes);
		userOTPRepository.save(userOTP);
	}

	public UserOTP getUser(String email) {
		return userOTPRepository.findByEmail(email);
	}

	//    @Getter
	//    @Setter
	//    @NoArgsConstructor
	//    @AllArgsConstructor
	//    class UserTOTP {
	//        private String username;
	//        private String secretKey;
	//        private int validationCode;
	//        private List<Integer> scratchCodes;
	//
	//        @Override
	//        public String toString() {
	//            return "UserTOTP{" +
	//                    "username='" + username + '\'' +
	//                    ", secretKey='" + secretKey + '\'' +
	//                    ", validationCode=" + validationCode +
	//                    '}';
	//        }
	//    }
}
