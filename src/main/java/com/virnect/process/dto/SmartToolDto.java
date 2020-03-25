package com.virnect.process.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Project: PF-SMIC_CUSTOM
 * DATE: 2020-01-28
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class SmartToolDto {
    @Getter
    @Setter
    public static class SmartToolInfo {
        private long id;
        private long jobId;
        private String normalTorque;
        private List<SmartToolDto.ItemInfo> items;

        @Override
        public String toString() {
            return "ReportInfo{" +
                    "id=" + id +
                    "jobId=" + jobId +
                    ", normalTorque=" + normalTorque +
                    ", items=" + items +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class ItemInfo {
        private long id;
        private int batchCount;
        private int workingTorque;
        private int result;

        @Override
        public String toString() {
            return "ItemInfo{" +
                    "id=" + id +
                    ", batchCount=" + batchCount +
                    ", workingTorque=" + workingTorque +
                    ", result=" + result +
                    '}';
        }
    }
}
