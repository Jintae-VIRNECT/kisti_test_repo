package com.virnect.dashboard.dto.request;

import java.util.Objects;

import org.springframework.data.domain.Sort;

public final class PageRequest {
	private final static int PAGE_DEFAULT_SIZE = 20;
	private final static int PAGE_MAZ_SIZE = 9999;

	//private int page = 1;

	private int page = 0;                           //page index starts 0
	private int size = PAGE_MAZ_SIZE;               //page size is derived from max size
	private String sort;

	private boolean paging;

	public boolean isPaging() {
		return paging;
	}

	public void setPaging(boolean paging) {
		this.paging = paging;
	}
	
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

	public String getSort() { return this.sort; }

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

		return org.springframework.data.domain.PageRequest.of(page, size, Sort.Direction.valueOf(sort), properties);

		//return org.springframework.data.domain.PageRequest.of(page - 1, size, Sort.Direction.valueOf(sort), properties);
	}

}
