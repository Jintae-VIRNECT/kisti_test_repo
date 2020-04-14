package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-02-19
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
@Getter
@Setter
@Entity
@Table(name = "smart_tool_item")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SmartToolItem extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "smart_tool_item_id")
    private Long id;

    @Column(name = "batch_count", length = 10, nullable = false)
    private int batchCount;

    @Column(name = "working_toque", length = 10)
    private String workingToque;

    @Enumerated(EnumType.STRING)
    @Column(name = "result")
    private Result result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "smart_tool_id")
    private SmartTool smartTool;

    @Builder
    public SmartToolItem(int batchCount, String workingToque, Result result, SmartTool smartTool) {
        this.batchCount = batchCount;
        this.workingToque = workingToque;
        this.result = result;
        this.smartTool = smartTool;
    }

    @Override
    public String toString() {
        return "SmartToolItem{" +
                "id=" + id +
                ", batchCount=" + batchCount +
                ", workingToque='" + workingToque + '\'' +
                ", result=" + result +
//                ", smartTool=" + smartTool +      // 무한 toString 방지
                '}';
    }
}
