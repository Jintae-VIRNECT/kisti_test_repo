package com.virnect.api.dto.request;

import org.springframework.data.domain.Sort;

import java.util.Objects;

public final class PageRequest {
    private final static int PAGE_DEFAULT_SIZE = 20;
    private final static int PAGE_MAZ_SIZE = 50;

    private int page = 1;
    private int size = 20;
    private String sort;

    public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }

    public void setSize(int size) {
        this.size = size > PAGE_MAZ_SIZE ? PAGE_DEFAULT_SIZE : size;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public org.springframework.data.domain.PageRequest of() {
        String strSort = Objects.isNull(this.sort) || this.sort.isEmpty() ? "updatedDate, DESC" : this.sort;
        String[] sortQuery = strSort.split(",");
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
