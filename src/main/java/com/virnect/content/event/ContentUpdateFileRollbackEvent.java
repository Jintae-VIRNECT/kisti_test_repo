package com.virnect.content.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-ContentManagement
 * @email practice1356@gmail.com
 * @description: Content Update Envent
 * @since 2020.03.05
 */

@RequiredArgsConstructor
@Getter
public class ContentUpdateFileRollbackEvent {
    private final File contentFile;
}
