package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-04
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@Entity
@Audited
@Table(name = "smart_tool")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SmartTool extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "smart_tool_id")
    private Long id;

    @Column(name = "smart_tool_job_id", length = 50, nullable = false)
    private String jobId;

    @Column(name = "normal_toque", length = 10, nullable = false)
    private String normalToque;

    @Transient
    @Getter(AccessLevel.PROTECTED)
    private Integer progressRate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "smartTool", cascade = CascadeType.REMOVE)
    private List<SmartToolItem> smartToolItemList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    public void addSmartToolItem(SmartToolItem smartToolItem) {
        smartToolItem.setSmartTool(this);
        smartToolItemList.add(smartToolItem);
    }

    // 공정률 조회
    public Integer getProgressRate() {
        return ProgressManager.getSmartToolProgressRate(this);
    }

    @Builder
    public SmartTool(String jobId, String normalToque, Integer progressRate, List<SmartToolItem> smartToolItemList, Job job) {
        this.jobId = jobId;
        this.normalToque = normalToque;
        this.progressRate = progressRate;
        this.smartToolItemList = new ArrayList<>();
        this.job = job;
    }

    @Override
    public String toString() {
        return "SmartTool{" +
                "id=" + id +
                ", jobId='" + jobId + '\'' +
                ", normalToque='" + normalToque + '\'' +
                ", progressRate=" + progressRate +
                ", smartToolItemList=" + smartToolItemList +
//                ", job=" + job +      // 무한 toString 방지
                '}';
    }
}
