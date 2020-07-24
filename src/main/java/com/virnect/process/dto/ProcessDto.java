package com.virnect.process.dto;

import com.virnect.process.domain.Conditions;
import com.virnect.process.domain.State;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class ProcessDto {
    @Getter
    @Setter
    public static class ProcessInfo {
        private long id;
        private String name;
        private String managerUUID;
        private int subTaskTotal;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Conditions conditions;
        private State state;
        private long progressRate;
        private List<SubProcessDto.SubTaskInfo> subTasks;

        @Override
        public String toString() {
            return "TaskInfo{" +
                    "id=" + id +
                    "name='" + name + '\'' +
                    "managerUUID='" + managerUUID + '\'' +
                    "subProcessTotal=" + subTaskTotal +
                    "startDate=" + startDate +
                    "endDate=" + endDate +
                    "conditions=" + conditions +
                    "state=" + state +
                    "progressRate=" + progressRate +
                    "subTasks=" + subTasks +
                    '}';
        }
    }


    @Getter
    @Setter
    public static class RegisterNewProcess {
        @NotBlank
        private String contentUUID;

        @NotBlank
        private String actorUUID;

        @NotBlank
        private String ownerUUID;

        @NotNull
        private Date startDate;

        @NotNull
        private Date endDate;

        @NotNull
        private String position;

        @NotNull
        private List<SubProcessDto.NewSubTask> subProcesses = new ArrayList<>();
    }
}
