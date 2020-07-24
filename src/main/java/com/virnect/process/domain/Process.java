package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Audited
@Table(name = "process")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Process extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "process_id")
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "position", length = 100)
    private String position;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "reported_date")
    private LocalDateTime reportedDate;

    @Transient
    @Getter(AccessLevel.PROTECTED)
    @Enumerated(value = EnumType.STRING)
    private Conditions conditions;

    @Transient
    @Getter(AccessLevel.PROTECTED)
    private Integer progressRate;

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    private State state;

    @Column(name = "content_uuid", unique = true)
    private String contentUUID;

    @Column(name = "content_manager_uuid")
    private String contentManagerUUID;

    @Column(name = "workspace_uuid")
    private String workspaceUUID;

    @OneToMany(mappedBy = "process", cascade = CascadeType.REMOVE)
    private List<Target> targetList = new ArrayList<>();

    @OneToMany(mappedBy = "process", cascade = CascadeType.REMOVE)
    private List<SubProcess> subProcessList = new ArrayList<>();

    public void addTarget(Target target) {
        target.setProcess(this);
        log.info("CREATE TARGET ---> {}", target.toString());
        this.targetList.add(target);
    }

    public void addSubProcess(SubProcess subProcess) {
        subProcess.setProcess(this);
        log.info("CREATE SUB PROCESS ---> {}", subProcess.toString());
        this.subProcessList.add(subProcess);
    }

    // 공정 상태 조회
    public Conditions getConditions() {
        return ProgressManager.getProcessConditions(this);
    }

    // 공정률 조회
    public Integer getProgressRate() {
        return ProgressManager.getProcessProgressRate(this);
    }

    @Builder
    public Process(String name, String position, LocalDateTime startDate, LocalDateTime endDate, LocalDateTime reportedDate, Conditions conditions, Integer progressRate, State state, String contentUUID, String contentManagerUUID, String workspaceUUID, List<Target> targetList, List<SubProcess> subProcessList) {
        this.name = name;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reportedDate = reportedDate;
        this.conditions = conditions;
        this.progressRate = progressRate;
        this.state = state;
        this.contentUUID = contentUUID;
        this.contentManagerUUID = contentManagerUUID;
        this.workspaceUUID = workspaceUUID;
        this.targetList = new ArrayList<>();
        this.subProcessList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Process{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", reportedDate=" + reportedDate +
                ", conditions=" + conditions +
                ", progressRate=" + progressRate +
                ", state=" + state +
                ", contentUUID='" + contentUUID + '\'' +
                ", contentManagerUUID='" + contentManagerUUID + '\'' +
                ", workspaceUUID='" + workspaceUUID + '\'' +
                ", targetList=" + targetList +
                ", subProcessList=" + subProcessList +
                '}';
    }
}
