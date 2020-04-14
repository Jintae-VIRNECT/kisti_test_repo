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

    @Enumerated(value = EnumType.STRING)
    @Column(name = "state")
    private State state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aruco_id")
    private Aruco aruco;

    @OneToMany(mappedBy = "process", cascade = CascadeType.REMOVE)
    private List<SubProcess> subProcessList;

    public void addSubProcess(SubProcess subProcess) {
        subProcess.setProcess(this);
        log.info("CREATE SUB PROCESS ---> {}", subProcess.toString());
        subProcessList.add(subProcess);
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
    public Process(Long id, String name, String position, LocalDateTime startDate, LocalDateTime endDate, Conditions conditions, Integer progressRate, State state, Aruco aruco, List<SubProcess> subProcessList) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.startDate = startDate;
        this.endDate = endDate;
        this.conditions = conditions;
        this.progressRate = progressRate;
        this.state = state;
        this.aruco = aruco;
        this.subProcessList = subProcessList;
    }


    @Override
    public String toString() {
        return "Process{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", position='" + position + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", conditions=" + conditions +
                ", progressRate=" + progressRate +
                ", state=" + state +
//                ", aruco=" + aruco +      // 무한 toString 방지
                ", subProcessList=" + subProcessList +
                '}';
    }
}
