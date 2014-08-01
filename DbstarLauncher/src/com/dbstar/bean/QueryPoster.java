package com.dbstar.bean;

import java.io.Serializable;

import android.graphics.Bitmap;

public class QueryPoster implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String title;
	// 图片路径。需要做一下拼接
	private String iconUrl;
	// 图片
	private Bitmap icon;
	private String parentCode;
	// 顺序编号
	private int sequence;
	private int type;
	private String code;
	private int ratingLevel;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getRatingLevel() {
		return ratingLevel;
	}

	public void setRatingLevel(int ratingLevel) {
		this.ratingLevel = ratingLevel;
	}

}
