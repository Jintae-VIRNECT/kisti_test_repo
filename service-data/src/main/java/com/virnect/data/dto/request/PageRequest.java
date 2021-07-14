package com.virnect.data.dto.request;

import java.util.Objects;

import org.springframework.data.domain.Sort;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PageRequest {
    private final static int PAGE_DEFAULT_SIZE = 20;
    private final static int PAGE_MAZ_SIZE = 50;

    //private int page = 1;

    private int page = 0;                           //page index starts 0
    private int size = PAGE_MAZ_SIZE;               //page size is derived from max size
    private String sort;

    /*public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }*/

    public void setPage(int page) {
        this.page = Math.max(page, 0);
    }

    public void setSize(int size) {
        this.size = size > PAGE_MAZ_SIZE ? PAGE_DEFAULT_SIZE : size;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public org.springframework.data.domain.PageRequest ofSortBy() {
        String strSort = Objects.isNull(this.sort) || this.sort.isEmpty() ? "updatedDate, DESC" : this.sort;
        String[] sortQuery = strSort.split(",");
        String properties = sortQuery[0].trim();
        String sort = sortQuery[1].toLowerCase().trim();

        if (properties.isEmpty()) {
            properties = "createdDate";
        }

        log.info("PAGING::#ofSortBy::sort::[{}]", sort);
        if(sort.equalsIgnoreCase("ASC")) {
            log.info("PAGING::#ofSortBy::sorting::[{}, {}]", properties, sort);
            return org.springframework.data.domain.PageRequest.of(page, size, Sort.by(properties).ascending());
        } else if(sort.equalsIgnoreCase("DESC")) {
            log.info("PAGING::#ofSortBy::sorting::[{}, {}]", properties, sort);
            return org.springframework.data.domain.PageRequest.of(page, size, Sort.by(properties).descending());
        } else {
            //default decening
            log.error("PAGING::#ofSortBy::default sorting::[{}, {}]", properties, sort);
            return org.springframework.data.domain.PageRequest.of(page, size, Sort.by(properties).descending());
        }
    }

    @Deprecated
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

        log.info("PAGING::#of::sorting::[{}]", strSort);

        return org.springframework.data.domain.PageRequest.of(page, size, Sort.Direction.valueOf(sort), properties);

        //return org.springframework.data.domain.PageRequest.of(page - 1, size, Sort.Direction.valueOf(sort), properties);
    }
}