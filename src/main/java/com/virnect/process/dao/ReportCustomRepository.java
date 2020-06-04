package com.virnect.process.dao;

import com.virnect.process.domain.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.20
 */
public interface ReportCustomRepository {
    Page<Report> getPages(String workspaceUUID, Long processId, Long subProcessId, String workerUUID, Boolean reported, Pageable pageable);
}
