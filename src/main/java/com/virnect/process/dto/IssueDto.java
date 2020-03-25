package com.virnect.process.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
public class IssueDto {
    @Getter
    @Setter
    @JsonPropertyOrder({ "id", "items" })
    public static class IssueInfo {
        private long id;
        private List<IssueDto.ItemInfo> items;

        @Override
        public String toString() {
            return "IssueInfo{" +
                    "id=" + id +
                    ", items=" + items +
                    '}';
        }
    }

    @Getter
    @Setter
    public static class ItemInfo {
        private long id;
        private int photoFile;
        private int caption;

        @Override
        public String toString() {
            return "ItemInfo{" +
                    "id=" + id +
                    ", photoFile=" + photoFile +
                    ", caption=" + caption +
                    '}';
        }
    }
}
