package com.virnect.content.dto.request;

import com.virnect.content.domain.Types;
import com.virnect.content.domain.YesOrNo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jiyong.heo
 * @project PF-ContentManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.12
 */
@Getter
@Setter
public class ContentDeleteRequest {
    @NotNull
    @ApiModelProperty(value="컨텐츠 고유번호 배열", required=true, example="")
    String[] contentUUIDs;

    // 기존에는 컨텐츠 관리자 정보를 받음. 혹시 몰라 주석처리.
//    @NotBlank
//    @NotNull
//    @ApiModelProperty(value="삭제 요청 사용자  고유번호", required=true, example="498b1839dc29ed7bb2ee90ad6985c608")
//    String workerUUID;

    @ApiModelProperty(value="삭제 요청 워크스페이스 고유번호", required=true, example="4d6eab0860969a50acbfa4599fbb5ae8")
    String workspaceUUID;

}
