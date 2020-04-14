package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDateTime;
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
@Table(name = "sub_process")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubProcess extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_process_id")
    private Long id;

    @Column(name = "priority", nullable = false)
    private Integer priority;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "reported_date")
    private LocalDateTime reportedDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "is_recent")
    private YesOrNo isRecent;

    @Transient
    @Getter(AccessLevel.PROTECTED)
    private Integer progressRate;

    @Transient
    @Getter(AccessLevel.PROTECTED)
    @Enumerated(value = EnumType.STRING)
    private Conditions conditions;

    @Column(name = "worker_uuid")
    private String workerUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private Process process;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subProcess", cascade = CascadeType.ALL)
    private List<Job> jobList;

    public void addJob(Job job) {
        job.setSubProcess(this);
        log.info("CREATE JOB ---> {}", job.toString());
        jobList.add(job);
    }

    // 공정 상태 조회
    public Conditions getConditions() {
        return ProgressManager.getSubProcessConditions(this);
    }

    // 공정률 조회
    public Integer getProgressRate() {
        return ProgressManager.getSubProcessProgressRate(this);
    }

    @Builder
    public SubProcess(Integer priority, String name, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime reportedDate, YesOrNo isRecent, Integer progressRate, Conditions conditions, String workerUUID, Process process, List<Job> jobList) {
        this.priority = priority;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportedDate = reportedDate;
        this.isRecent = isRecent;
        this.progressRate = progressRate;
        this.conditions = conditions;
        this.workerUUID = workerUUID;
        this.process = process;
        this.jobList = jobList;
    }

    @Override
    public String toString() {
        return "SubProcess{" +
                "id=" + id +
                ", priority=" + priority +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reportedDate=" + reportedDate +
                ", isRecent=" + isRecent +
                ", progressRate=" + progressRate +
                ", conditions=" + conditions +
                ", workerUUID='" + workerUUID + '\'' +
//                ", process=" + process +      // 무한 toString 방지
                ", jobList=" + jobList +
                '}';
    }
}
