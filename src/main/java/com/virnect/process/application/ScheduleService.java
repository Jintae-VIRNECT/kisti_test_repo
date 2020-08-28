package com.virnect.process.application;

import com.virnect.process.dao.DailyTotalRepository;
import com.virnect.process.dao.DailyTotalWorkspaceRepository;
import com.virnect.process.dao.process.ProcessRepository;
import com.virnect.process.domain.DailyTotal;
import com.virnect.process.domain.DailyTotalWorkspace;
import com.virnect.process.domain.Process;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-08-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {
    private final DailyTotalRepository dailyTotalRepository;
    private final DailyTotalWorkspaceRepository dailyTotalWorkspaceRepository;
    private final ProcessRepository processRepository;

    // 일자별 총계
    @Transactional
//    @Scheduled(cron = "55 59 23 * * *", zone = "GMT+9:00")
    // UTC 시간으로 스케쥴링함. 스케줄에 zone 설정시 정상적으로 KST로 스케줄이 돌지만 저장되는 날짜와 시간은 UTC로 저장되어 DB를 긁어서 뷰에 전달하게되면 UTC를 반환함.
    // 때문에 UTC로 스케줄을 돌리고 저장되는 일시도 UTC로 저장됨. 그러므로 '일자별 통계' API에서 일시 반환시 UTC -> KST로 서버에서 변환하여 보내기로 함.
    @Scheduled(cron = "55 59 14 * * *")
    public void saveDailyTotal() {
        // 공정목록 조회
        List<Process> processes = this.processRepository.findByWorkspaceUUID(null);

        int totalRate = 0, totalProcesses = 0;
        List<String> workspaces = new ArrayList<>();
        List<Integer> rates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        HashMap<String, Integer> workspaceRate = new HashMap<>();
        HashMap<String, Integer> workspaceCount = new HashMap<>();
        for (Process process : processes) {
            int progressRate = process.getProgressRate();
            totalRate = totalRate + progressRate;
            totalProcesses++;

            if (workspaces.contains(process.getWorkspaceUUID())) {
                int index = workspaces.indexOf(process.getWorkspaceUUID());
                rates.set(index, rates.get(index) + progressRate);
                counts.set(index, counts.get(index) + 1);
            } else {
                workspaces.add(process.getWorkspaceUUID());
                rates.add(progressRate);
                counts.add(1);
            }
        }

        // 공정률 총계
        totalRate = totalProcesses == 0 ? 0 : totalRate / totalProcesses;

        // 일자별 총계 엔티티생성
        DailyTotal dailyTotal = DailyTotal.builder()
                .totalRate(totalRate)
                .totalCountProcesses(totalProcesses)
                .build();

        this.dailyTotalRepository.save(dailyTotal);

        // 워크스페이스별 총계
        DailyTotalWorkspace dailyTotalWorkspace = null;
        int i = 0;
        while (i < workspaces.size()) {
            dailyTotalWorkspace = dailyTotalWorkspace.builder()
                    .workspaceUUID(workspaces.get(i))
                    .totalRate(rates.get(i))
                    .totalCountProcesses(counts.get(i))
                    .build();

            log.debug("IDX : [{}], dailyTotalWorkspace : [{}]", i, dailyTotalWorkspace.toString());
            this.dailyTotalWorkspaceRepository.save(dailyTotalWorkspace);

            dailyTotal.addDailyTotalWorkspace(dailyTotalWorkspace);
            i++;
        }
    }
}
