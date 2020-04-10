package com.virnect.content.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.03.18
 */
@Getter
@Setter
@ToString
public class ContentStatusChangeRequest {
    private String contentUUID;
    private String status;
}
