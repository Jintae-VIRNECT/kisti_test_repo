package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Audited
@Table(name = "item")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "priority")
    private Integer priority;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "type")
    private ItemType type;

    @Column(name = "title")
    private String title;

    @Column(name = "photo_file_path")
    private String path;

    @Column(name = "answer")
    private String answer;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "result")
    private Result result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @Builder
    public Item(Integer priority, ItemType type, String title, String path, String answer, Result result, Report report) {
        this.priority = priority;
        this.type = type;
        this.title = title;
        this.path = path;
        this.answer = answer;
        this.result = result;
        this.report = report;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", priority=" + priority +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", path='" + path + '\'' +
                ", answer='" + answer + '\'' +
                ", result=" + result +
//                ", report=" + report +      // 무한 toString 방지
                '}';
    }
}
