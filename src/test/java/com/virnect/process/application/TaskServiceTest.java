package com.virnect.process.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.process.dao.IssueRepository;
import com.virnect.process.dao.ItemRepository;
import com.virnect.process.dao.JobRepository;
import com.virnect.process.dao.ReportRepository;
import com.virnect.process.dao.SubProcessRepository;
import com.virnect.process.dao.dailytotal.DailyTotalRepository;
import com.virnect.process.dao.dailytotalworkspace.DailyTotalWorkspaceRepository;
import com.virnect.process.dao.process.ProcessRepository;
import com.virnect.process.dao.target.TargetRepository;
import com.virnect.process.domain.DailyTotal;
import com.virnect.process.domain.DailyTotalWorkspace;
import com.virnect.process.domain.Issue;
import com.virnect.process.domain.Item;
import com.virnect.process.domain.Job;
import com.virnect.process.domain.Process;
import com.virnect.process.domain.Report;
import com.virnect.process.domain.Result;
import com.virnect.process.domain.State;
import com.virnect.process.domain.SubProcess;
import com.virnect.process.domain.Target;
import com.virnect.process.domain.TargetType;
import com.virnect.process.domain.YesOrNo;
import com.virnect.process.infra.file.upload.FileUploadService;

/**
 * Project: PF-ProcessManagement
 * DATE: 2021-06-28
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
//@DataJpaTest
@SpringBootTest
@RequiredArgsConstructor
class TaskServiceTest {
	@Autowired
	JPAQueryFactory jpaQueryFactory;

	@Autowired
	ProcessRepository processRepository;
	@Autowired
	SubProcessRepository subProcessRepository;
	@Autowired
	JobRepository jobRepository;
	@Autowired
	IssueRepository issueRepository;
	@Autowired
	ReportRepository reportRepository;
	@Autowired
	ItemRepository itemRepository;

	@Autowired
	TargetRepository targetRepository;

	@Autowired
	DailyTotalRepository dailyTotalRepository;

	@Autowired
	DailyTotalWorkspaceRepository dailyTotalWorkspaceRepository;
	String WORKSPACE_UUID = "workspace_abc";
	String USER_UUID = "user_abc";

	@Test
	@DisplayName("작업 전체 삭제 테스트를 위한 더미데이터 생성")
	void deleteAllTaskInfo_dummyData() {
		Process process1 = Process.builder()
			.contentManagerUUID(USER_UUID)
			.contentUUID("contents_abc")
			.endDate(LocalDateTime.now())
			.name("더미 작업 1")
			.position("더미 작업 1 위치")
			.reportedDate(LocalDateTime.now())
			.startDate(LocalDateTime.now())
			.state(State.CREATED)
			.workspaceUUID(WORKSPACE_UUID)
			.build();
		Process process2 = Process.builder()
			.contentManagerUUID(USER_UUID)
			.contentUUID("contents_abc")
			.endDate(LocalDateTime.now())
			.name("더미 작업 2")
			.position("더미 작업 2 위치")
			.reportedDate(LocalDateTime.now())
			.startDate(LocalDateTime.now())
			.state(State.CREATED)
			.workspaceUUID(WORKSPACE_UUID)
			.build();
		processRepository.save(process1);
		processRepository.save(process2);
		System.out.println("==========작업 등록 완료 !===========");

		SubProcess subProcess1 = SubProcess.builder()
			.endDate(LocalDateTime.now())
			.isRecent(YesOrNo.NO)
			.name("더미 하위 작업 1")
			.priority(1)
			.reportedDate(LocalDateTime.now())
			.startDate(LocalDateTime.now())
			.workerUUID(USER_UUID)
			.process(process1)
			.build();
		SubProcess subProcess2 = SubProcess.builder()
			.endDate(LocalDateTime.now())
			.isRecent(YesOrNo.NO)
			.name("더미 하위 작업 2")
			.priority(2)
			.reportedDate(LocalDateTime.now())
			.startDate(LocalDateTime.now())
			.workerUUID(USER_UUID)
			.process(process1)
			.build();

		SubProcess subProcess3 = SubProcess.builder()
			.endDate(LocalDateTime.now())
			.isRecent(YesOrNo.NO)
			.name("더미 하위 작업 3")
			.priority(3)
			.reportedDate(LocalDateTime.now())
			.startDate(LocalDateTime.now())
			.workerUUID(USER_UUID)
			.process(process1)
			.build();
		subProcessRepository.save(subProcess1);
		subProcessRepository.save(subProcess2);
		subProcessRepository.save(subProcess3);
		System.out.println("==========하위 작업 등록 완료 !===========");

		Job job1 = Job.builder()
			.isReported(YesOrNo.NO)
			.name("더미 단계 1")
			.priority(1)
			.result(Result.OK)
			.subProcess(subProcess1)
			.build();
		Job job2 = Job.builder()
			.isReported(YesOrNo.NO)
			.name("더미 단계 2")
			.priority(2)
			.result(Result.OK)
			.subProcess(subProcess1)
			.build();
		Job job3 = Job.builder()
			.isReported(YesOrNo.NO)
			.name("더미 단계 3")
			.priority(3)
			.result(Result.OK)
			.subProcess(subProcess1)
			.build();
		jobRepository.save(job1);
		jobRepository.save(job2);
		jobRepository.save(job3);
		System.out.println("==========잡 등록 완료 !===========");

		Issue issue1 = Issue.builder()
			.content("더미 트러블 메모 이슈")
			.path(null)
			.workerUUID(USER_UUID)
			.build();//트러블 메모
		Issue issue2 = Issue.builder()
			.content("더미 작업 이슈")
			.path(null)
			.workerUUID(USER_UUID)
			.build();
		issue2.setJob(job1);//작업 이슈

		issueRepository.save(issue1);
		issueRepository.save(issue2);
		System.out.println("==========이슈 등록 완료 !===========");
		Report report1 = Report.builder()
			.job(job1)
			.build();
		Report report2 = Report.builder()
			.job(job1)
			.build();
		Report report3 = Report.builder()
			.job(job1)
			.build();
		reportRepository.save(report1);
		reportRepository.save(report2);
		reportRepository.save(report3);
		System.out.println("==========리포트 등록 완료 !===========");

		Item item1 = Item.builder()
			.answer("더미 아이템 앤서 1")
			.path(null)
			.priority(1)
			.result(Result.OK)
			.title("더미 아이템 타이틀 1")
			.type(null)
			.report(report1)
			.build();
		Item item2 = Item.builder()
			.answer("더미 아이템 앤서 2")
			.path(null)
			.priority(2)
			.result(Result.OK)
			.title("더미 아이템 타이틀 2")
			.type(null)
			.report(report1)
			.build();
		Item item3 = Item.builder()
			.answer("더미 아이템 앤서 3")
			.path(null)
			.priority(3)
			.result(Result.OK)
			.title("더미 아이템 타이틀 3")
			.type(null)
			.report(report1)
			.build();
		itemRepository.save(item1);
		itemRepository.save(item2);
		itemRepository.save(item3);
		System.out.println("==========아이템 등록 완료 !===========");

		Target target1 = Target.builder()
			.data("target_data 1 ")
			.type(TargetType.VTarget)
			.imgPath(null)
			.process(process1)
			.build();
		Target target2 = Target.builder()
			.data("target_data 2 ")
			.type(TargetType.VTarget)
			.imgPath(null)
			.process(process2)
			.build();
		targetRepository.save(target1);
		targetRepository.save(target2);
		System.out.println("==========타겟 등록 완료 !===========");

		DailyTotalWorkspace dailyTotalWorkspace = DailyTotalWorkspace.builder()
			.totalRate(10)
			.totalCountProcesses(8)
			.workspaceUUID(WORKSPACE_UUID)
			.build();
		DailyTotal dailyTotal = DailyTotal.builder()
			.totalCountProcesses(10)
			.totalRate(8)
			.build();

		dailyTotal.addDailyTotalWorkspace(dailyTotalWorkspace);
		dailyTotalRepository.save(dailyTotal);
		dailyTotalWorkspaceRepository.save(dailyTotalWorkspace);

		System.out.println("==========통계 테이블 등록 완료 !===========");

		System.out.println("==========끝 !===========");

	}

	@Autowired
	TaskService taskService;

	@Autowired
	FileUploadService fileUploadService;

	@Test
	@Transactional
	@Rollback(value = false)
	void deleteAllTaskInfo_test() {
		//검색
		taskService.deleteAllTaskInfo("workspace_abc", "user_abc");
		List<DailyTotalWorkspace> dailyTotalWorkspaceList = dailyTotalWorkspaceRepository.findAllByWorkspaceUUID(
			WORKSPACE_UUID);
		System.out.println("dailyTotalWorkspaceList = " + dailyTotalWorkspaceList.size());
		List<DailyTotal> dailyTotalList = dailyTotalWorkspaceList.stream()
			.map(dailyTotalWorkspace -> dailyTotalWorkspace.getDailyTotal())
			.collect(
				Collectors.toList());
		System.out.println("dailyTotalList = " + dailyTotalList.size());
		List<Process> processList = processRepository.findByWorkspaceUUID(WORKSPACE_UUID);
		System.out.println("processList = " + processList.size());
		List<SubProcess> subProcessList = subProcessRepository.findByProcessIn(processList);
		System.out.println("subProcessList = " + subProcessList.size());
		List<Job> jobList = jobRepository.findBySubProcessIn(subProcessList);
		System.out.println("jobList = " + jobList.size());
		List<Report> reportList = reportRepository.findByJobIn(jobList);
		System.out.println("reportList = " + reportList.size());
		List<Item> itemList = itemRepository.findByReportIn(reportList);
		System.out.println("itemList = " + itemList.size());
		List<Issue> issueList = issueRepository.findByJobIn(jobList);
		System.out.println("issueList = " + issueList.size());
		List<Issue> troubleMemoList = issueRepository.findAllByWorkerUUIDAndJobIsNull(USER_UUID);
		System.out.println("troubleMemoList = " + troubleMemoList.size());
		List<Target> targetList = targetRepository.findByProcessIn(processList);
		System.out.println("targetList = " + targetList.size());

	}
}
