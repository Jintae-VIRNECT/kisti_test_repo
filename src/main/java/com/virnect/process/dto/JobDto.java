package com.virnect.process.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.virnect.process.domain.Conditions;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-ProcessManagement
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class JobDto {
    @Getter
    @Setter
    @JsonPropertyOrder({ "id", "", "" })
    public static class JobInfo {
        private long id;
        private int priority;
        private String name;
        private int subJobTotal;
        private Conditions conditions;
        private long progressRate;
        private List<ReportDto.ReportInfo> reports;
        private List<SmartToolDto.SmartToolInfo> smartTools;
        private List<IssueDto.IssueInfo> issues;

        @Override
        public String toString() {
            return "SubProcessInfo{" +
                    "id=" + id +
                    "priority=" + priority +
                    "name='" + name + '\'' +
                    "subJobTotal=" + subJobTotal +
                    "conditions=" + conditions +
                    "progressRate=" + progressRate +
                    "reports=" + reports +
                    "smartTools=" + smartTools +
                    "issues=" + issues +
                    '}';
        }
    }
}
