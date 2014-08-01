package com.dbstar.bean;

import java.io.Serializable;

import android.graphics.Bitmap;

public class QueryRecommand implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String rESName;
	private String code;
	// 例如：网络电视、星影院
	private String name;
	// 图片地址
	private String icon1Url;
	private String linkType;
	private String linkURI;
	private String parentCode;
	private String columnType;
	// 排序
	private int sequence;
	// 图片
	private Bitmap icon;

	public String getrESName() {
		return rESName;
	}

	public void setrESName(String rESName) {
		this.rESName = rESName;
	}

	public Bitmap getIcon() {
		return icon;
	}

	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon1Url() {
		return icon1Url;
	}

	public void setIcon1Url(String icon1Url) {
		this.icon1Url = icon1Url;
	}

	public String getLinkType() {
		return linkType;
	}

	public void setLinkType(String linkType) {
		this.linkType = linkType;
	}

	public String getLinkURI() {
		return linkURI;
	}

	public void setLinkURI(String linkURI) {
		this.linkURI = linkURI;
	}

	public String getParentCode() {
		return parentCode;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
