package com.virnect.content.dto.response;

import com.virnect.content.domain.TargetType;
import lombok.*;

/**
 * @author hangkee.min (henry)
 * @project PF-ContentManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.08
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ContentTargetResponse {
    private Long id;
    private TargetType type;
    private String data;

    @Builder
    public ContentTargetResponse(Long id, TargetType type, String data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }
}
