package com.virnect.process.dto.rest.request.content;

import lombok.Getter;
import lombok.Setter;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ProcessManagement
 * @email practice1356@gmail.com
 * @description
 * @since 2020.03.18
 */
@Getter
@Setter
public class ContentStatusChangeRequest {
    private String contentUUID;
    private String status;
}
