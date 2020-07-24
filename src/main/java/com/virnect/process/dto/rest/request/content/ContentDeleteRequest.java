package com.virnect.process.dto.rest.request.content;

import lombok.Getter;
import lombok.Setter;

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
    String[] contentUUIDs;
    String workerUUID;
}
