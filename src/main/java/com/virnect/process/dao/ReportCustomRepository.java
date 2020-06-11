package com.virnect.process.dao;

import com.virnect.process.domain.Report;
import com.virnect.process.dto.response.HourlyReportCountOfaDayResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author jiyong.heo
 * @project PF-ProcessManagement
 * @email jiyong.heo@virnect.com
 * @description
 * @since 2020.05.20
 */
public interface ReportCustomRepository {
    Page<Report> getPages(String myUUID, String workspaceUUID, Long processId, Long subProcessId, List<String> userUUIDList, Boolean reported, Pageable pageable);

    List<HourlyReportCountOfaDayResponse> selectHourlyReportsTemp(String targetDate);
}
