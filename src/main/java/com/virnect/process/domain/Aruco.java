package com.virnect.process.domain;

import com.virnect.process.model.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
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
@Table(name = "aruco")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Aruco extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aruco_id")
    private Long id;

    @Column(name = "content_uuid")
    private String contentUUID;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "aruco")
    private List<Process> processList;

    public void addProcess(Process process) {
        process.setAruco(this);
        processList.add(process);
    }

    @Builder
    public Aruco(String contentUUID, List<Process> processList) {
        this.contentUUID = contentUUID;
        this.processList = processList;
    }

    @Override
    public String toString() {
        return "Aruco{" +
                "id=" + id +
                ", contentUUID='" + contentUUID + '\'' +
                ", processList=" + processList +
                '}';
    }
}
