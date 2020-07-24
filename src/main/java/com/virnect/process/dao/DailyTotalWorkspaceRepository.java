package com.virnect.process.dao;

import com.virnect.process.domain.DailyTotalWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author hangkee.min (henry)
 * @project PF-ProcessManagement
 * @email hkmin@virnect.com
 * @description
 * @since 2020.04.21
 */
public interface DailyTotalWorkspaceRepository extends JpaRepository<DailyTotalWorkspace, Long> {
    @Query(value = "select * from daily_total_workspace where date_format(date_add(updated_at, interval 9 HOUR), '%Y-%m') = :month and workspace_uuid = :workspaceUUID", nativeQuery = true)
    List<DailyTotalWorkspace> getDailyTotalRateAtMonthWithWorkspace(@Param("workspaceUUID") String workspaceUUID, @Param("month") String month);
}
