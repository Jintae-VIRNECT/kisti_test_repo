package com.virnect.uaa.domain.user.dto.request;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.uaa.domain.user.domain.Status;
import com.virnect.uaa.domain.user.validator.UserLoginIdValidation;

/**
 * Project: user
 * DATE: 2020-01-08
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION: 회원가입 요청 처리
 */
@Getter
@Setter
@ApiModel
public class RegisterRequest {
	@ApiModelProperty(value = "로그인 이메일", example = "test@test.com")
	@NotBlank(message = "로그인 아이디는 반드시 입력되어야 합니다.")
	@UserLoginIdValidation
	private String email;

	@ApiModelProperty(value = "사용자 이름의 성", position = 1, example = "홍")
	@NotBlank(message = "사용자 이름의 성 정보는 반드시 입력되어야 합니다.")
	@Length(min = 1)
	private String firstName;

	@ApiModelProperty(value = "사용자 이름", position = 2, example = "길동")
	@NotBlank(message = "사용자 이름 정보는 반드시 입력되어야 합니다.")
	@Length(min = 1)
	private String lastName;

	@ApiModelProperty(value = "로그인 비밀번호", position = 3, example = "test1234")
	@NotBlank(message = "로그인 패스워드는 반드시 입력되어야 합니다.")
	@Length(min = 2)
	private String password;

	@ApiModelProperty(value = "회원가입 세션 코드", position = 4, example = "A213NZS3")
	@NotBlank(message = "인증 세션 코드가 입력되지 않았습니다.")
	private String sessionCode;

	@ApiModelProperty(value = "프로필 이미지", position = 5)
	private MultipartFile profile;

	@ApiModelProperty(value = "휴대전화번호 -[전화번호는 국가코드 포함 다음과 같은 형식이여야 합니다. +(국가코드)-('-'을 제외한 전화번호 최소 10자리)]", position = 6, example = "+82-01012341234")
	private String mobile = "";

	@Email
	@ApiModelProperty(value = "복구 이메일", position = 7)
	private String recoveryEmail = "";

	private String description = "";

	@ApiModelProperty(value = "생년월일(YYYY-MM-DD) ISO DATE 포맷", position = 8, example = "2020-01-01")
	@NotNull(message = "생년월일 데이터는 반드시 입력되어야 합니다.")
	private LocalDate birth;

	@ApiModelProperty(value = "서비스 분야(설문)", dataType = "string", position = 9, example = "화학 공장 원격 지원")
	@NotBlank(message = "서비스 정보는 반드시 입력되어야 합니다.")
	private String serviceInfo;

	@ApiModelProperty(value = "가입경로(설문)", position = 10, example = "버넥트 공식 사이트")
	@NotBlank(message = "가입 경로 정보는 반드시 입력되어야 합니다.")
	private String joinInfo;

	@ApiModelProperty(value = "광고 수신 동의 여부", position = 11, example = "ACTIVE")
	@NotNull(message = "광고 수신 동의 여부 정보는 반드시 입력되어야 합니다.")
	private Status marketInfoReceive;

	@ApiModelProperty(value = "닉네임", position = 12, example = "닉넴")
	private String nickname = "";

	public void setBirth(String birth) {
		this.birth = LocalDate.parse(birth, DateTimeFormatter.ISO_DATE);
	}

	@Override
	public String toString() {
		return "RegisterRequest{" +
			"email='" + email + '\'' +
			", firstName='" + firstName + '\'' +
			", lastName='" + lastName + '\'' +
			", password='" + "********" + '\'' +
			", sessionCode='" + sessionCode + '\'' +
			", profile=" + profile +
			", mobile='" + mobile + '\'' +
			", recoveryEmail='" + recoveryEmail + '\'' +
			", description='" + description + '\'' +
			", birth=" + birth +
			", serviceInfo='" + serviceInfo + '\'' +
			", joinInfo='" + joinInfo + '\'' +
			", marketInfoReceive=" + marketInfoReceive +
			", nickname='" + nickname + '\'' +
			'}';
	}
}
