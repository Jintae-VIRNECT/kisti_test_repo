package com.virnect.uaa.domain.user.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@Getter
@Setter
public class UserIdentityCheckRequest {
	@NotBlank(message = "비밀번호 변경 대상 이메일 정보는 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "비밀번호 변경 대상 이메일",  example = "smic3")
	private String email;
	@NotBlank(message = "비밀번호 변경 질문은 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "비밀번호 변경 질문", position = 1, example = "첫 반려동물의 이름은 무엇입니까?")
	private String question;
	@NotBlank(message = "비밀번호 변경 질문 답변 정보는 반드시 입력되어야 합니다.")
	@ApiModelProperty(value = "비밀번호 변경 질문 답변", position = 2, example = "댕댕이")
	private String answer;
}
