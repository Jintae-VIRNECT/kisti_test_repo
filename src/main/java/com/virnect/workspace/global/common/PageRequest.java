package com.virnect.workspace.global.common;

import org.springframework.data.domain.Sort;

import java.util.Objects;

/**
 * @author jeonghyeon.chang (johnmark)
 * @email practice1356@gmail.com
 * @since 2020.03.02
 */

public final class PageRequest {
    private int page = 1;
    private int size = 20;
    private String sort;

    public void setPage(int page) {
        this.page = page <= 0 ? 1 : page;
    }

    public void setSize(int size) {
        int DEFAULT_SIZE = 20;
        int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public org.springframework.data.domain.PageRequest of(Boolean paging) {
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

        if(paging) {
            //페이징을 워크스페이스에서 해야 한다면
            return org.springframework.data.domain.PageRequest.of(page - 1, size, Sort.Direction.valueOf(sort), properties);
        }else{
            //페이징을 타 서비스에서 해야 한다면
            return org.springframework.data.domain.PageRequest.of(page , size, Sort.Direction.valueOf(sort), properties);

        }
    }


}
