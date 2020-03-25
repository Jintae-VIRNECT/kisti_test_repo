package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
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

    @Builder
    public DailyTotal(int totalRate, int totalCountProcesses) {
        this.totalRate = totalRate;
        this.totalCountProcesses = totalCountProcesses;
    }

    @Override
    public String toString() {
        return "DailyTotalRate{" +
                "id=" + id +
                ", totalRate=" + totalRate +
                ", totalCountProcesses=" + totalCountProcesses +
                '}';
    }
}
