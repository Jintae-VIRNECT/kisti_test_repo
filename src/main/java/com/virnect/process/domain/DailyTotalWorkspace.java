package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Audited
@Table(name = "daily_total_workspace")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyTotalWorkspace extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "total_rate", nullable = false)
    private int totalRate;

    @Column(name = "total_count_processes", nullable = false)
    private int totalCountProcesses;

    @Column(name = "workspace_uuid", nullable = false)
    private String workspaceUUID;

    @ManyToOne
    @JoinColumn(name = "daily_total_id")
    private DailyTotal dailyTotal;

    @Builder
    public DailyTotalWorkspace(int totalRate, int totalCountProcesses, String workspaceUUID) {
        this.totalRate = totalRate;
        this.totalCountProcesses = totalCountProcesses;
        this.workspaceUUID = workspaceUUID;
    }

    @Override
    public String toString() {
        return "DailyTotalWorkspace{" +
                "id=" + id +
                ", totalRate=" + totalRate +
                ", totalCountProcesses=" + totalCountProcesses +
                ", workspaceUUID='" + workspaceUUID + '\'' +
//                ", dailyTotal=" + dailyTotal +
                '}';
    }
}
