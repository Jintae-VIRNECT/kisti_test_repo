package com.virnect.process.dao.process;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;

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
	public ProcessCustomRepositoryImpl() {
		super(Process.class);
	}

	QProcess qProcess = QProcess.process;
	QSubProcess qSubProcess = QSubProcess.subProcess;
	QTarget qTarget = QTarget.target;
	QJob qJob = QJob.job;
	QIssue qIssue = QIssue.issue;

	@Override
	public Optional<Process> findByTargetDataAndState(String targetData, State state) {
		Process process = from(qProcess)
			.where(qProcess.state.eq(state))
			.join(qProcess.targetList, qTarget)
			.where(qTarget.data.eq(targetData)).fetchOne();
		return Optional.ofNullable(process);
	}

	@Override
	public Optional<Process> findByContentUUIDAndStatus(String contentUUID, State state) {
		Process process = from(qProcess)
			.where(qProcess.state.eq(state)
				.and(qProcess.contentUUID.eq(contentUUID)))
			.fetchOne();
		return Optional.ofNullable(process);
	}

	@Override
	public Page<Process> getProcessPageSearchUser(
		List<Conditions> filterList,
		String workspaceUUID, String search, List<String> userUUIDList, Pageable pageable, String targetType
	) {
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ProcessCustomRepository in");

		JPQLQuery<Process> query = from(qProcess);

		query = query.join(qProcess.subProcessList, qSubProcess);

		query = query.join(qProcess.targetList, qTarget);

		// 워크스페이스UUID
		if (workspaceUUID != null) {
			query = query.where(qProcess.workspaceUUID.eq(workspaceUUID));
		}

		if (!userUUIDList.isEmpty()) {
			query = query.where(qSubProcess.workerUUID.in(userUUIDList));
		} else {
			if (Objects.nonNull(search)) {
				// or 뒤의 조건은 프로필 -> 바로가기 때문에 추가
				query = query.where(qProcess.name.contains(search).or(qSubProcess.workerUUID.eq(search)));
			}
		}
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

		if (StringUtils.hasText(targetType) && !targetType.equalsIgnoreCase("ALL")) {
			query.join(qProcess.targetList, qTarget).where(qTarget.type.eq(TargetType.valueOf(targetType)));
		}

		log.debug(">>>>>>>>>>>>>>>>>>>>>>> : {}", query);

		query.groupBy(qProcess.id);

		final List<Process> processList = getQuerydsl().applyPagination(pageable, query)
			.fetch(); //.stream().distinct().collect(Collectors.toList());

		return new PageImpl<>(processList, pageable, query.fetchCount());
	}

	@Override
	public int getCountIssuesInProcess(Long processId) {
		JPQLQuery<Process> query = from(qProcess);

		query.join(qProcess.subProcessList, qSubProcess);

		query.join(qSubProcess.jobList, qJob);

		query.join(qJob.issueList, qIssue);

		query.where(qProcess.id.eq(processId));

		return (int)query.fetchCount();
	}

	@Override
	public Optional<Process> getProcessUnClosed(String workspaceUUID, String targetData) {
		JPQLQuery<Process> query = from(qProcess);

		query.join(qProcess.targetList, qTarget);

		query.where(qTarget.data.eq(targetData));

		query.where(qProcess.state.eq(State.CREATED).or(qProcess.state.eq(State.UPDATED)));

		if (Objects.nonNull(workspaceUUID)) {
			query.where(qProcess.workspaceUUID.eq(workspaceUUID));
		}

		Process result = query.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public Page<Process> getMyTask(
		List<Conditions> filterList, String myUUID, String workspaceUUID, String title, Pageable pageable,
		String targetType
	) {
		/*QProcess qProcess = QProcess.process;
		QSubProcess qSubProcess = QSubProcess.subProcess;
		QTarget qTarget = QTarget.target;

		JPQLQuery<Process> query = from(qProcess);
		query.join(qProcess.subProcessList, qSubProcess);
		query.join(qProcess.targetList, qTarget);

		query.where(qSubProcess.workerUUID.eq(myUUID));

		// 검색어가 들어왔을 경우
		if (Objects.nonNull(title)) {
			query.where(qProcess.name.contains(title));
		}
		query.groupBy(qProcess.id);

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

		if (StringUtils.hasText(targetType) && !targetType.equalsIgnoreCase("ALL")) {
			query.join(qProcess.targetList, qTarget).where(qTarget.type.eq(TargetType.valueOf(targetType)));
		}

		final List<Process> myList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(myList, pageable, query.fetchCount());*/
		JPQLQuery<Process> query = from(qProcess);
		query.join(qProcess.subProcessList, qSubProcess);
		query.join(qProcess.targetList, qTarget);
		query.where(eqWorkspaceUUID(workspaceUUID), eqWorkerUUID(myUUID), eqTitle(title));
		query.groupBy(qProcess.id);

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

		if (StringUtils.hasText(targetType) && !targetType.equalsIgnoreCase("ALL")) {
			query.where(qTarget.type.eq(TargetType.valueOf(targetType)));
		}

		final List<Process> myList = getQuerydsl().applyPagination(pageable, query).fetch();

		return new PageImpl<>(myList, pageable, query.fetchCount());
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
		QProcess qProcess = QProcess.process;
		QTarget qTarget = QTarget.target;

		JPQLQuery<Process> query = from(qProcess);
		query.where(qProcess.workspaceUUID.eq(workspaceUUID));

		if (StringUtils.hasText(targetType) && !targetType.equalsIgnoreCase("ALL")) {
			query.join(qProcess.targetList, qTarget).where(qTarget.type.eq(TargetType.valueOf(targetType)));
		}

		return query.fetch();
	}
}