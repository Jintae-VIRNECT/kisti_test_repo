package com.virnect.process.dto.rest.response.content;

import com.virnect.process.domain.TargetType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    private String imgPath;
}
