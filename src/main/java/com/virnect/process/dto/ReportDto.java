package com.virnect.process.dto;

import com.virnect.process.domain.ItemType;
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
public class ReportDto {
    @Getter
    @Setter
    public static class ReportInfo {
        private long id;
        private List<ReportDto.ItemInfo> items;

        @Override
        public String toString() {
            return "ReportInfo{" +
                    "id=" + id +
                    ", items=" + items +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class ItemInfo {
        private long id;
        private int priority;
        private ItemType type;
        private String title;
        private String answer;
        private String caption;

        @Override
        public String toString() {
            return "ItemInfo{" +
                    "id=" + id +
                    ", type=" + type +
                    ", title='" + title + '\'' +
                    ", answer='" + answer + '\'' +
                    ", caption='" + caption + '\'' +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class hourlyReport {
        private String hour;
        private int reportCount;

        @Override
        public String toString() {
            return "hourlyReport{" +
                    "hour='" + hour + '\'' +
                    ", reportCount=" + reportCount +
                    '}';
        }
    }
}
