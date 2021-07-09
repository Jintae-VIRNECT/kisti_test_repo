package com.virnect.process.application;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.virnect.process.dao.dailytotal.DailyTotalRepository;
import com.virnect.process.dao.process.ProcessRepository;
import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.DailyTotal;
import com.virnect.process.domain.DailyTotalWorkspace;
import com.virnect.process.domain.Process;
import com.virnect.process.dto.response.MonthlyStatisticsResponse;
import com.virnect.process.dto.response.ProcessesStatisticsResponse;
import com.virnect.process.global.common.ApiResponse;
import com.virnect.process.global.common.ResponseMessage;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-08-27
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
@Service
@RequiredArgsConstructor
public class StaticsService {
	private final ProcessRepository processRepository;
	private final DailyTotalRepository dailyTotalRepository;

	public ResponseMessage getTotalRate(String workspaceUUID) {
		// 전체 공정 진행률 조회
		List<Process> processes = this.processRepository.findByWorkspaceUUID(workspaceUUID);
		return new ResponseMessage().addParam("totalRate", ProgressManager.getAllProcessesTotalProgressRate(processes));
	}

	public ApiResponse<ProcessesStatisticsResponse> getStatistics(String workspaceUUID) {
		// 전체 공정 진행률 및 공정진행상태별 현황 조회
		int totalRate = 0, totalProcesses = 0, categoryWait = 0, categoryStarted = 0, categoryEnded = 0, wait = 0, unprogressing = 0, progressing = 0, completed = 0, incompleted = 0, failed = 0, success = 0, fault = 0;
		List<Process> processes = this.processRepository.findByWorkspaceUUID(workspaceUUID);
		for (Process process : processes) {
			totalRate = totalRate + process.getProgressRate();
			Conditions conditions = process.getConditions();
			if (conditions == Conditions.WAIT) {
				categoryWait++;
				wait++;
			} else if (conditions == Conditions.UNPROGRESSING) {
				categoryStarted++;
				unprogressing++;
			} else if (conditions == Conditions.PROGRESSING) {
				categoryStarted++;
				progressing++;
			} else if (conditions == Conditions.COMPLETED) {
				categoryStarted++;
				completed++;
			} else if (conditions == Conditions.INCOMPLETED) {
				categoryStarted++;
				incompleted++;
			} else if (conditions == Conditions.FAILED) {
				categoryEnded++;
				failed++;
			} else if (conditions == Conditions.SUCCESS) {
				categoryEnded++;
				success++;
			} else if (conditions == Conditions.FAULT) {
				categoryEnded++;
				fault++;
			}
			totalProcesses++;
		}
		totalRate = totalProcesses == 0 ? 0 : totalRate / totalProcesses;

		ProcessesStatisticsResponse processesStatisticsResponse = ProcessesStatisticsResponse.builder()
			.totalRate(totalRate)
			.totalTasks(totalProcesses)
			.categoryWait(categoryWait)
			.categoryStarted(categoryStarted)
			.categoryEnded(categoryEnded)
			.wait(wait)
			.unprogressing(unprogressing)
			.progressing(progressing)
			.completed(completed)
			.incompleted(incompleted)
			.failed(failed)
			.success(success)
			.fault(fault)
			.build();
		return new ApiResponse<>(processesStatisticsResponse);
	}

	public ApiResponse<MonthlyStatisticsResponse> getDailyTotalRateAtMonth(String workspaceUUID, String month) {
		if (Objects.isNull(workspaceUUID)) {
			List<DailyTotal> dailyTotals = this.dailyTotalRepository.getDailyTotalRateAtMonth(month);

			return new ApiResponse<>(MonthlyStatisticsResponse.builder()
				.dailyTotal(dailyTotals.stream().map(dailyTotal -> {
					return MonthlyStatisticsResponse.DailyTotalResponse.builder()
						.id(dailyTotal.getId())
						// UTC -> KST 로 9시간을 더해서 날짜산정 해야할 것 같은데 결과는 하지 않아도 KST로 주는 것 같음. 자바로 넘어오면서 KST로 바뀌는 것 같음.
						.onDay(dailyTotal.getUpdatedDate().toLocalDate())
						.totalRate(dailyTotal.getTotalRate())
						.totalTasks(dailyTotal.getTotalCountProcesses())
						.build();
				}).collect(Collectors.toList()))
				.build());
		} else {
			//            List<DailyTotalWorkspace> dailyTotalWorkspaceList = this.dailyTotalWorkspaceRepository.getDailyTotalRateAtMonthWithWorkspace(workspaceUUID, month);
			List<DailyTotalWorkspace> dailyTotalWorkspaceList = this.dailyTotalRepository.getDailyTotalRateAtMonthWithWorkspace(
				workspaceUUID, month);

			return new ApiResponse<>(MonthlyStatisticsResponse.builder()
				.dailyTotal(dailyTotalWorkspaceList.stream().map(dailyTotalWorkspace -> {
					return MonthlyStatisticsResponse.DailyTotalResponse.builder()
						.id(dailyTotalWorkspace.getId())
						// UTC -> KST 로 9시간을 더해서 날짜산정 해야할 것 같은데 결과는 하지 않아도 KST로 주는 것 같음. 자바로 넘어오면서 KST로 바뀌는 것 같음.
						.onDay(dailyTotalWorkspace.getUpdatedDate().toLocalDate())
						.totalRate(dailyTotalWorkspace.getTotalRate())
						.totalTasks(dailyTotalWorkspace.getTotalCountProcesses())
						.build();
				}).collect(Collectors.toList()))
				.build());
		}
	}
}
