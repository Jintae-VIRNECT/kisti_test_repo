package com.virnect.process.dao;

import com.virnect.process.domain.DailyTotal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyTotalRepository extends JpaRepository<DailyTotal, Long>, DailyTotalCustomRepository {
//    List<DailyTotal> findByUpdatedDateContaining(LocalDate atStartOfDay);
//
//    // updated_at은 UTC이므로 +9시간의 날짜를 조회해야 함.
//    @Query(value = "select * from daily_total where date_format(date_add(updated_at, interval 9 HOUR), '%Y-%m') = :month", nativeQuery = true)
//    List<DailyTotal> getDailyTotalRateAtMonth(@Param("month") String month);
//
//    @Query(value = "select * from daily_total_workspace where date_format(date_add(updated_at, interval 9 HOUR), '%Y-%m') = :month and workspace_uuid = :workspaceUUID", nativeQuery = true)
//    List<DailyTotalWorkspace> getDailyTotalRateAtMonthWithWorkspace(@Param("workspaceUUID") String workspaceUUID, @Param("month") String month);
}
