package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.uaa.domain.user.domain.AcceptOrReject;
import com.virnect.uaa.domain.user.validator.ValueOfEnum;
import com.virnect.uaa.global.validator.PasswordFormatPolicyValidate;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-User
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.06
 */

@Getter
@Setter
@ApiModel
public class UserInfoModifyRequest {
	@ApiModelProperty(value = "변경할 사용자 이름", notes = "변경할 경우 입력하면됩니다.", example = "길동")
	private String firstName;
	@ApiModelProperty(value = "변경할 사용자 이름의 성", position = 1, notes = "변경할 경우 입력합니다.", example = "홍")
	private String lastName;
	@ApiModelProperty(value = "변경할 닉네임", position = 2, notes = "변경할 경우 입력하면됩니다.", example = "닉넴")
	private String nickname;
	@ApiModelProperty(value = "변경할 비밀번호", position = 3, notes = "변경할 경우 입력하면됩니다.", example = "test12345")
	@PasswordFormatPolicyValidate(emptyIgnore = true)
	private String password;
	@ApiModelProperty(value = "변경할 생년월일", position = 4, notes = "변경할 경우 입력하면됩니다.", example = "2020-02-20")
	@Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
	private String birth;
	@ApiModelProperty(value = "변경할 휴대전화번호 -[전화번호는 국가코드 포함 다음과 같은 형식이여야 합니다. +(국가코드)-('-'을 제외한 전화번호 최소 10자리)]", position = 5, notes = "변경할 경우 입력하면됩니다.", example = "+82-001044321234")
	private String mobile;
	@Email
	@ApiModelProperty(value = "변경할 복구 이메일 주소", position = 6, notes = "변경할 경우 입력하면됩니다.", example = "test@test.com")
	private String recoveryEmail;
	@ValueOfEnum(enumClass = AcceptOrReject.class)
	@ApiModelProperty(value = "마케팅 정보 수신 동의 여부", position = 7, notes = "변경할 경우 입력하면됩니다.", example = "ACCEPT")
	private String marketInfoReceive;
	@ApiModelProperty(value = "비밀번호 찾기 질문", position = 8, example = "집에 가고 싶나요?")
	private String question;
	@ApiModelProperty(value = "비밀번호 찾기 대답", position = 9, example = "네네 선장님!")
	private String answer;

	@ApiModelProperty(hidden = true)
	public String getName() {
		if (lastName == null || firstName == null) {
			return null;
		}
		return lastName + firstName;
	}

	@ApiModelProperty(hidden = true)
	public String getInternationalNumber() {
		if (mobile == null) {
			return null;
		}
		return mobile.split("-")[0];
	}

	@ApiModelProperty(hidden = true)
	public String getPhoneNumber() {
		if (mobile == null) {
			return null;
		}
		return mobile.substring(getInternationalNumber().length() + 1);
	}

	@Override
	public String toString() {
		return "UserInfoModifyRequest{" +
			"firstName='" + firstName + '\'' +
			", lastName='" + lastName + '\'' +
			", nickname='" + nickname + '\'' +
			", password='" + "*****************" + '\'' +
			", birth='" + birth + '\'' +
			", mobile='" + mobile + '\'' +
			", recoveryEmail='" + recoveryEmail + '\'' +
			", marketInfoReceive='" + marketInfoReceive + '\'' +
			", question='" + question + '\'' +
			", answer='" + answer + '\'' +
			'}';
	}
}
