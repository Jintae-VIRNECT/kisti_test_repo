package com.virnect.workspace.global.common;

import com.virnect.workspace.dto.rest.PageMetadataRestResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: PF-Workspace
 * DATE: 2020-11-11
 * AUTHOR: jkleee (Jukyoung Lee)
 * EMAIL: ljk@virnect.com
 * DESCRIPTION:
 */
public class CustomPageHandler<T> {
    private int temp = 0;

    public CustomPageResponse paging(int page, int size, List<T> beforePagingList) {
        int totalElements = beforePagingList.size();
        int totalPage = totalElements / size;
        int resultPage = totalPage;
        int lastElements = totalElements % size;
        int currentPage = page + 1;
        if (lastElements > 0) {
            totalPage = totalPage + 1;
            resultPage = resultPage + 1;
        }

        List<List<T>> result = new ArrayList<>();

        while (totalPage > 0) {
            List<T> afterList = beforePagingList.stream().skip(temp).limit(size).collect(Collectors.toList());
            result.add(afterList);
            temp = temp + size;
            totalPage = totalPage - 1;
        }

        PageMetadataRestResponse pageMetadataResponse = new PageMetadataRestResponse();
        pageMetadataResponse.setTotalElements(totalElements);
        pageMetadataResponse.setTotalPage(resultPage);
        pageMetadataResponse.setCurrentPage(currentPage);
        pageMetadataResponse.setCurrentSize(size);

        if (currentPage > result.size()) {
            return new CustomPageResponse(new ArrayList<>(), pageMetadataResponse);
        }
        return new CustomPageResponse(result.get(page), pageMetadataResponse);
    }
}
