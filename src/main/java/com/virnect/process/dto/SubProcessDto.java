package com.virnect.process.dto;

import com.virnect.process.domain.Conditions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class SubProcessDto {
    @Getter
    @Setter
    public static class SubTaskInfo {
        private long id;
        private int priority;
        private String name;
        private int stepTotal;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Conditions conditions;
        private long progressRate;
        private String workerUUID;
        private LocalDateTime syncDate;
        private String syncUserUUID;
        private String newSubTask;
        private List<JobDto.JobInfo> steps;

        @Override
        public String toString() {
            return "SubTaskInfo{" +
                    "id=" + id +
                    ", priority=" + priority +
                    ", name='" + name + '\'' +
                    ", stepTotal=" + stepTotal +
                    ", startDate=" + startDate +
                    ", endDate=" + endDate +
                    ", conditions=" + conditions +
                    ", progressRate=" + progressRate +
                    ", workerUUID='" + workerUUID + '\'' +
                    ", syncDate=" + syncDate +
                    ", syncUserUUID='" + syncUserUUID + '\'' +
                    ", newSubTask='" + newSubTask + '\'' +
                    ", steps=" + steps +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class UploadWorkResult {
        @NotNull
        private long taskId;
        @NotBlank
        private String metadata;
        @NotBlank
        private String actorUUID;

        @Override
        public String toString() {
            return "UploadWorkResult{" +
                    "taskId=" + taskId +
                    ", metadata='" + metadata + '\'' +
                    ", actorUUID='" + actorUUID + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class NewSubTask {
        @NotNull
        private Date startDate;
        @NotNull
        private Date endDate;
        @NotBlank
        private String workerUUID;
    }
}
