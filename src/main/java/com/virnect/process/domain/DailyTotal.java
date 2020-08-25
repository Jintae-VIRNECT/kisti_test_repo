package com.virnect.process.domain;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Audited
@Table(name = "daily_total")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyTotal extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "total_rate", nullable = false)
    private int totalRate;

    @Column(name = "total_count_processes", nullable = false)
    private int totalCountProcesses;

    @OneToMany(mappedBy = "dailyTotal", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DailyTotalWorkspace> dailyTotalWorkspaceList = new ArrayList<>();

    public void addDailyTotalWorkspace(DailyTotalWorkspace dailyTotalWorkspace) {
        dailyTotalWorkspace.setDailyTotal(this);
        dailyTotalWorkspaceList.add(dailyTotalWorkspace);
    }

    @Builder
    public DailyTotal(int totalRate, int totalCountProcesses, List<DailyTotalWorkspace> dailyTotalWorkspaceList) {
        this.totalRate = totalRate;
        this.totalCountProcesses = totalCountProcesses;
        this.dailyTotalWorkspaceList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "DailyTotal{" +
                "id=" + id +
                ", totalRate=" + totalRate +
                ", totalCountProcesses=" + totalCountProcesses +
                ", dailyTotalWorkspaceList=" + dailyTotalWorkspaceList +
                '}';
    }
}
