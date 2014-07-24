package com.dbstar.bean;

import java.io.Serializable;
import java.util.List;

public class QueryPosterData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int count;
	private int totalCount;
	private int pageSize;
	private int pageIndex;
	private List<QueryPoster> queryPosters;

	public List<QueryPoster> getQueryPosters() {
		return queryPosters;
	}

	public void setQueryPosters(List<QueryPoster> queryPosters) {
		this.queryPosters = queryPosters;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

}
