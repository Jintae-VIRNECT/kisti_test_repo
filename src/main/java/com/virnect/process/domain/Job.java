package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-04
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Slf4j
@Getter
@Setter
@Entity
@Table(name = "job")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Job extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long id;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "name", nullable = false)
    private String name;

    @Transient
    @Getter(AccessLevel.PROTECTED)
    private Integer progressRate;

    @Transient
    @Getter(AccessLevel.PROTECTED)
    @Enumerated(EnumType.STRING)
    private Conditions conditions;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "is_reported")
    private YesOrNo isReported;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "result")
    private Result result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_process_id")
    private SubProcess subProcess;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "job", cascade = CascadeType.REMOVE)
    private List<Report> reportList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "job", cascade = CascadeType.REMOVE)
    private List<SmartTool> smartToolList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "job", cascade = CascadeType.REMOVE)
    private List<Issue> issueList;


    public void addReport(Report report) {
        report.setJob(this);
        log.info("CREATE REPORT ---> {}", report.toString());
        this.reportList.add(report);
    }

    public void addSmartTool(SmartTool smartTool) {
        smartTool.setJob(this);
        log.info("CREATE SMARTTOOL ---> {}", smartTool.toString());
        this.smartToolList.add(smartTool);
    }

    public void addIssue(Issue issue) {
        issue.setJob(this);
        log.info("CREATE ISSUE ---> {}", issue.toString());
        this.issueList.add(issue);
    }

    // 공정 상태 조회
    public Conditions getConditions() {
        return ProgressManager.getJobConditions(this);
    }

    // 공정률 조회
    public Integer getProgressRate() {
        return ProgressManager.getJobProgressRate(this);
    }

    @Builder
    public Job(Integer priority, String name, Integer progressRate, Conditions conditions, YesOrNo isReported, Result result, SubProcess subProcess, List<Report> reportList, List<SmartTool> smartToolList, List<Issue> issueList) {
        this.priority = priority;
        this.name = name;
        this.progressRate = progressRate;
        this.conditions = conditions;
        this.isReported = isReported;
        this.result = result;
        this.subProcess = subProcess;
        this.reportList = reportList;
        this.smartToolList = smartToolList;
        this.issueList = issueList;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", priority=" + priority +
                ", name='" + name + '\'' +
                ", progressRate=" + progressRate +
                ", conditions=" + conditions +
                ", isReported=" + isReported +
                ", result=" + result +
//                ", subProcess=" + subProcess +      // 무한 toString 방지
                ", reportList=" + reportList +
                ", smartToolList=" + smartToolList +
                ", issueList=" + issueList +
                '}';
    }
}
