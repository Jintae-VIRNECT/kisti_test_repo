package com.virnect.content.global.common;

import java.util.Objects;

import org.springframework.data.domain.Sort;

/**
 * @author jeonghyeon.chang (johnmark)
 * @email practice1356@gmail.com
 * @since 2020.03.02
 */

public class PageRequest {
    private static final int MAX_SIZE = 50;
    protected int page = 1;
    protected int size = 20;
    protected String sort;

    public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }

    public void setSize(int size) {
        this.size = Math.min(size, MAX_SIZE);
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public org.springframework.data.domain.PageRequest of() {
        // sort nullable
        String sortStr = Objects.isNull(this.sort) || this.sort.isEmpty() ? "updatedDate,DESC" : this.sort;
        String[] sortQuery = sortStr.split(",");
        String properties = sortQuery[0];
        String sort = sortQuery[1].toUpperCase();

        if (!(sort.equals("ASC") || sort.equals("DESC"))) {
            sort = "DESC";
        }

        if (properties == null || properties.isEmpty()) {
            properties = "createdDate";
        }

        return org.springframework.data.domain.PageRequest.of(page - 1, size, Sort.Direction.valueOf(sort), properties);
    }
}
