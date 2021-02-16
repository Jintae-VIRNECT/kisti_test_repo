package com.virnect.process.dao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.jpa.JPQLQuery;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.QProcess;
import com.virnect.process.domain.QSubProcess;
import com.virnect.process.domain.QTarget;
import com.virnect.process.domain.State;
import com.virnect.process.domain.SubProcess;
import com.virnect.process.domain.TargetType;
import com.virnect.process.domain.YesOrNo;

public class SubProcessCustomRepositoryImpl extends QuerydslRepositorySupport implements SubProcessCustomRepository {

	public SubProcessCustomRepositoryImpl() {
		super(SubProcess.class);
	}

	@Override
	public Page<SubProcess> getMyWorks(String workerUUID, /*long processId, String search, */Pageable pageable) {
		QSubProcess qSubProcess = QSubProcess.subProcess;
		JPQLQuery<SubProcess> query = from(qSubProcess);
		query.where(/*qSubProcess.id.eq(processId).and(qSubProcess.name.contains(search)).and*/
			(qSubProcess.workerUUID.eq(workerUUID)));
		long totalCount = query.fetchCount();
		List<SubProcess> results = getQuerydsl().applyPagination(pageable, query).fetch();
		return new PageImpl<>(results, pageable, totalCount);
	}

	@Override
	public boolean existsByIsRecentAndWorkerUUIDAndWorkspaceUUID(
		YesOrNo newWork, String workspaceUUID, String workerUUID
	) {
		QProcess qProcess = QProcess.process;
		QSubProcess qSubProcess = QSubProcess.subProcess;
		JPQLQuery<SubProcess> query = from(qSubProcess).where(qSubProcess.isRecent.eq(newWork));

		if (workerUUID != null) {
			query = query.where(qSubProcess.workerUUID.eq(workerUUID));
		}

		if (workspaceUUID != null) {
			query = query.join(qSubProcess.process, qProcess).where(qProcess.workspaceUUID.eq(workspaceUUID));
		}

		long totalCount = query.fetchCount();

		return totalCount > 0;
	}

	@Override
	public Page<SubProcess> getSubProcessPage(
		String workspaceUUID, Long processId, String search, List<String> userUUIDList, Pageable pageable
	) {
		QSubProcess qSubProcess = QSubProcess.subProcess;
		QProcess qProcess = QProcess.process;
		JPQLQuery<SubProcess> query = from(qSubProcess).join(qSubProcess.process, qProcess);

		if (userUUIDList != null && userUUIDList.size() > 0) {
			query = query.where(qSubProcess.workerUUID.in(userUUIDList));
		}

		if (search != null) {
			query = query.where(qSubProcess.name.contains(search));
		}

		if (processId != null) {
			query = query.where(qProcess.id.eq(processId));
		}

		if (workspaceUUID != null) {
			query = query.where(qProcess.workspaceUUID.eq(workspaceUUID));
		}

		List<SubProcess> subProcessList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(subProcessList, pageable, query.fetchCount());
	}

	@Override
	public Page<SubProcess> getFilteredSubProcessPage(
		String workspaceUUID, Long processId, String search, List<String> userUUIDList, Pageable pageable,
		List<Conditions> filter
	) {
		QSubProcess qSubProcess = QSubProcess.subProcess;
		QProcess qProcess = QProcess.process;
		JPQLQuery<SubProcess> query = from(qSubProcess).join(qSubProcess.process, qProcess);
		List<SubProcess> subProcessList = query.fetch();

		if (filter != null && !filter.isEmpty() && !filter.contains(Conditions.ALL)) {
			List<SubProcess> filterdSubProcessList = new ArrayList<>();
			subProcessList.forEach(subProcess -> {
				filter.forEach(conditions -> {
					if (conditions.equals(subProcess.getConditions())) {
						filterdSubProcessList.add(subProcess);
					}
				});
			});
			subProcessList = filterdSubProcessList;
			query = query.where(qSubProcess.in(subProcessList));
		}

		if (userUUIDList != null && userUUIDList.size() > 0) {
			query = query.where(qSubProcess.workerUUID.in(userUUIDList));
		}

		if (search != null) {
			query = query.where(qSubProcess.name.contains(search));
		}

		if (processId != null) {
			query = query.where(qProcess.id.eq(processId));
		}

		if (workspaceUUID != null) {
			query = query.where(qProcess.workspaceUUID.eq(workspaceUUID));
		}
		if (pageable.getSort()
			.stream()
			.anyMatch(order -> order.getProperty().equals("taskName") && order.getDirection().isDescending())) {
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
			query = query.orderBy(qProcess.name.desc());
		}
		if (pageable.getSort()
			.stream()
			.anyMatch(order -> order.getProperty().equals("taskName") && order.getDirection().isAscending())) {
			pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
			query = query.orderBy(qProcess.name.asc());
		}

		subProcessList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(subProcessList, pageable, query.fetchCount());
	}

	@Override
	public LocalDateTime getLastestReportedTime(String workspaceUUID, String userUUID) {
		QSubProcess qSubProcess = QSubProcess.subProcess;
		QProcess qProcess = QProcess.process;
		LocalDateTime reportedTime = from(qSubProcess)
			.select(qSubProcess.reportedDate.max())
			.join(qSubProcess.process, qProcess)
			.where(qSubProcess.workerUUID.eq(userUUID))
			.where(qProcess.workspaceUUID.eq(workspaceUUID))
			.fetchOne();

		return reportedTime;
	}

	@Override
	public List<SubProcess> getSubProcessList(String workspaceUUID, String userUUID) {
		QSubProcess qSubProcess = QSubProcess.subProcess;
		QProcess qProcess = QProcess.process;

		JPQLQuery<SubProcess> query = from(qSubProcess).join(qSubProcess.process, qProcess);

		query.where(qProcess.workspaceUUID.eq(workspaceUUID));
		query.where(qSubProcess.workerUUID.eq(userUUID));

		List<SubProcess> subProcessList = query.fetch();

		return subProcessList;
	}

	@Override
	public Page<SubProcess> getMyWorksInProcess(
		String workspaceUUID, String workerUUID, Long processId, String search, Pageable pageable, String targetType
	) {
		QSubProcess qSubProcess = QSubProcess.subProcess;
		QProcess qProcess = QProcess.process;
		QTarget qTarget = QTarget.target;

		JPQLQuery<SubProcess> query = from(qSubProcess).join(qSubProcess.process, qProcess);

		// (nativeQuery)작업의 상태가 CLOSED, DELETE가 아닌 것만 = 작업의 상태가 CREATED, UPDATED 인 것만
		query.where(qProcess.state.eq(State.CREATED).or(qProcess.state.eq(State.UPDATED)));

		query.where(qSubProcess.workerUUID.eq(workerUUID));

		if (Objects.nonNull(processId)) {
			query.where(qProcess.id.eq(processId));
		}

		if (Objects.nonNull(workspaceUUID)) {
			query.where(qProcess.workspaceUUID.eq(workspaceUUID));
		}

		if (Objects.nonNull(search)) {
			query.where(qSubProcess.name.contains(search));
		}

		if (Objects.nonNull(targetType) && !targetType.equalsIgnoreCase("ALL")) {
			query.join(qProcess.targetList, qTarget).where(qTarget.type.eq(TargetType.valueOf(targetType)));
		}

		List<SubProcess> subProcessList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(subProcessList, pageable, query.fetchCount());
	}

}
