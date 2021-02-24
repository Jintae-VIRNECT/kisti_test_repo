package com.virnect.process.dao.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.extern.slf4j.Slf4j;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.QIssue;
import com.virnect.process.domain.QJob;
import com.virnect.process.domain.QProcess;
import com.virnect.process.domain.QSubProcess;
import com.virnect.process.domain.QTarget;
import com.virnect.process.domain.State;
import com.virnect.process.domain.TargetType;

@Slf4j
public class ProcessCustomRepositoryImpl extends QuerydslRepositorySupport implements ProcessCustomRepository {
	private final JPAQueryFactory queryFactory;

	QProcess qProcess = QProcess.process;
	QSubProcess qSubProcess = QSubProcess.subProcess;
	QTarget qTarget = QTarget.target;
	QJob qJob = QJob.job;
	QIssue qIssue = QIssue.issue;

	public ProcessCustomRepositoryImpl(JPAQueryFactory queryFactory) {
		super(Process.class);
		this.queryFactory = queryFactory;
	}

	@Override
	public Optional<Process> findByTargetDataAndState(String targetData, State state) {
		return Optional.ofNullable(
			queryFactory
				.select(qProcess)
				.from(qProcess)
				.join(qProcess.targetList, qTarget)
				.where(qProcess.state.eq(state)
					.and(qTarget.data.eq(targetData)))
				.fetchOne());
	}

	@Override
	public Optional<Process> findByContentUUIDAndStatus(String contentUUID, State state) {
		return Optional.ofNullable(
			queryFactory
				.select(qProcess)
				.from(qProcess)
				.where(qProcess.state.eq(state)
					.and(qProcess.contentUUID.eq(contentUUID)))
				.fetchOne());
	}

	@Override
	public Page<Process> getProcessPageSearchUser(
		List<Conditions> filterList,
		String workspaceUUID, String search, List<String> userUUIDList, Pageable pageable, String targetType
	) {
		JPAQuery<Process> query = queryFactory.select(qProcess)
			.from(qProcess)
			.join(qProcess.subProcessList, qSubProcess)
			.join(qProcess.targetList, qTarget)
			.where(
				eqWorkspaceUUID(workspaceUUID), inWorkerUUIDOrEqSearch(userUUIDList, search), eqTargetType(targetType))
			.groupBy(qProcess.id);

		// 공정 상태 필터링
		if (filterList != null && filterList.size() > 0 && !filterList.contains(Conditions.ALL)) {
			List<Process> filterdProcessList = new ArrayList<>();
			List<Process> processList = query.fetch();
			processList.forEach(process -> {
				if (filterList.contains(process.getConditions())) {
					filterdProcessList.add(process);
				}
			});
			query = query.where(qProcess.in(filterdProcessList));
		}
		
		final List<Process> result = getQuerydsl().applyPagination(pageable, query).fetch();
		return new PageImpl<>(result, pageable, query.fetchCount());
	}

	private BooleanExpression eqTargetType(String targetType) {
		if (StringUtils.hasText(targetType) && !targetType.equalsIgnoreCase("ALL")) {
			return qTarget.type.eq(TargetType.valueOf(targetType));
		}
		return null;
	}

	private BooleanExpression inWorkerUUIDOrEqSearch(List<String> userUUIDList, String search) {
		if (!userUUIDList.isEmpty()) {
			return qSubProcess.workerUUID.in(userUUIDList);
		}

		if (StringUtils.hasText(search)) {
			// or 뒤의 조건은 프로필 -> 바로가기 때문에 추가
			return qProcess.name.contains(search).or(qSubProcess.workerUUID.eq(search));
		}

		return null;
	}

	@Override
	public int getCountIssuesInProcess(Long processId) {
		return (int)queryFactory
			.select(qProcess)
			.from(qProcess)
			.join(qProcess.subProcessList, qSubProcess)
			.join(qSubProcess.jobList, qJob)
			.join(qJob.issueList, qIssue)
			.where(qProcess.id.eq(processId))
			.fetchCount();
	}

	@Override
	public Optional<Process> getProcessUnClosed(String workspaceUUID, String targetData) {
		return Optional.ofNullable(
			queryFactory.select(qProcess)
				.from(qProcess)
				.join(qProcess.targetList, qTarget)
				.where(qTarget.data.eq(targetData)
					.and(qProcess.state.eq(State.CREATED)
						.or(qProcess.state.eq(State.UPDATED))
						.and(eqWorkspaceUUID(workspaceUUID))))
				.fetchOne());
	}

	@Override
	public Page<Process> getMyTask(
		List<Conditions> filterList, String myUUID, String workspaceUUID, String title, Pageable pageable,
		String targetType
	) {
		JPAQuery<Process> query = queryFactory.select(qProcess)
			.from(qProcess)
			.join(qProcess.subProcessList, qSubProcess)
			.join(qProcess.targetList, qTarget)
			.where(eqWorkspaceUUID(workspaceUUID), eqWorkerUUID(myUUID), eqTitle(title), eqTargetType(targetType))
			.groupBy(qProcess.id);

		//TODO: 작업 상태 필터링 부분 쿼리 개선 필요
		// 공정 상태 필터링
		if (filterList != null && filterList.size() > 0 && !filterList.contains(Conditions.ALL)) {
			List<Process> filteredList = new ArrayList<>();
			List<Process> processList = query.fetch();
			processList.forEach(process -> {
				if (filterList.contains(process.getConditions())) {
					filteredList.add(process);
				}
			});
			query = query.where(qProcess.in(filteredList));
		}
		final List<Process> result = getQuerydsl().applyPagination(pageable, query).fetch();
		return new PageImpl<>(result, pageable, query.fetchCount());
	}

	private BooleanExpression eqWorkspaceUUID(String workspaceUUID) {
		if (StringUtils.isEmpty(workspaceUUID)) {
			return null;
		}
		return qProcess.workspaceUUID.eq(workspaceUUID);
	}

	private BooleanExpression eqWorkerUUID(String workerUUID) {
		if (StringUtils.isEmpty(workerUUID)) {
			return null;
		}
		return qSubProcess.workerUUID.eq(workerUUID);
	}

	private BooleanExpression eqTitle(String title) {
		if (StringUtils.isEmpty(title)) {
			return null;
		}
		return qProcess.name.contains(title);
	}

	@Override
	public List<Process> findByWorkspaceUUIDAndTargetType(String workspaceUUID, String targetType) {
		return
			queryFactory.select(qProcess)
				.from(qProcess)
				.join(qProcess.targetList, qTarget)
				.where(eqWorkspaceUUID(workspaceUUID), eqTargetType(targetType)).fetch();
	}
}