package com.virnect.process.dto.response;

import com.virnect.process.domain.TargetType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @author hangkee.min (henry)
 * @project PF-ProcessManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ProcessTargetResponse {
    @ApiModelProperty(value = "공정의 타겟 식별자")
    private Long id;
    @ApiModelProperty(value = "공정의 타겟 종류", position = 1, example = "QR")
    private TargetType type;
    @ApiModelProperty(value = "공정의 타겟 데이터", position = 2, example = "061cc38d-6c45-445b-bf56-4d164fcb5d29")
    private String data;

    @Builder
    public ProcessTargetResponse(Long id, TargetType type, String data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }
}
