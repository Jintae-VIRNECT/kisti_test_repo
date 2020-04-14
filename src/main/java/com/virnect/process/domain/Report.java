package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;

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
@Table(name = "report")
@NoArgsConstructor
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Transient
    @Getter(AccessLevel.PROTECTED)
    private Integer progressRate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "report", cascade = CascadeType.REMOVE)
    private List<Item> itemList = new ArrayList<>();

    public void addItem(Item item) {
        item.setReport(this);
        itemList.add(item);
    }

    // 공정률 조회
    public Integer getProgressRate() {
        return ProgressManager.getReportProgressRate(this);
    }

    @Builder
    public Report(Integer progressRate, Job job, List<Item> itemList) {
        this.progressRate = progressRate;
        this.job = job;
        this.itemList = itemList;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", progressRate=" + progressRate +
//                ", job=" + job +      // 무한 toString 방지
                ", itemList=" + itemList +
                '}';
    }
}
