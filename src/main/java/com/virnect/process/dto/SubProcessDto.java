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
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class SubProcessDto {
    @Getter
    @Setter
    public static class SubProcessInfo {
        private long id;
        private int priority;
        private String name;
        private int jobTotal;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private Conditions conditions;
        private long progressRate;
        private String workerUUID;
        private LocalDateTime syncDate;
        private String syncUserUUID;
        private String newSubProcess;
        private List<JobDto.JobInfo> jobs;

        @Override
        public String toString() {
            return "SubProcessInfo{" +
                    "id=" + id +
                    ", priority=" + priority +
                    ", name='" + name + '\'' +
                    ", jobTotal=" + jobTotal +
                    ", startDate=" + startDate +
                    ", endDate=" + endDate +
                    ", conditions=" + conditions +
                    ", progressRate=" + progressRate +
                    ", workerUUID='" + workerUUID + '\'' +
                    ", syncDate=" + syncDate +
                    ", syncUserUUID='" + syncUserUUID + '\'' +
                    ", newSubProcess='" + newSubProcess + '\'' +
                    ", jobs=" + jobs +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class UploadWorkResult {
        @NotNull
        private long processId;
        @NotBlank
        private String metadata;
        @NotBlank
        private String actorUUID;

        @Override
        public String toString() {
            return "UploadWorkResult{" +
                    "processId=" + processId +
                    ", metadata='" + metadata + '\'' +
                    ", actorUUID='" + actorUUID + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class NewSubProcess {
        @NotNull
        private Date startDate;
        @NotNull
        private Date endDate;
        @NotBlank
        private String workerUUID;
    }
}
