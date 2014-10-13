package com.settings.bean;

import java.io.Serializable;

public class Vapks implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int compressType;
	// 升级包地址
	private String profileUrl;
	// 版本
	private String version;
	private String remake;
	private String fileHash;
	private double profileSize;
	private String vapk;
	// 升级模式：1、强制升级，2、普通升级，3、静默升级
	private int upgradeMode;
	private String apkProfileName;

	private int rc;
	private String rm;

	public int getCompressType() {
		return compressType;
	}

	public void setCompressType(int compressType) {
		this.compressType = compressType;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRemake() {
		return remake;
	}

	public void setRemake(String remake) {
		this.remake = remake;
	}

	public String getFileHash() {
		return fileHash;
	}

	public void setFileHash(String fileHash) {
		this.fileHash = fileHash;
	}

	public double getProfileSize() {
		return profileSize;
	}

	public void setProfileSize(double profileSize) {
		this.profileSize = profileSize;
	}

	public String getVapk() {
		return vapk;
	}

	public void setVapk(String vapk) {
		this.vapk = vapk;
	}

	public int getUpgradeMode() {
		return upgradeMode;
	}

	public void setUpgradeMode(int upgradeMode) {
		this.upgradeMode = upgradeMode;
	}

	public String getApkProfileName() {
		return apkProfileName;
	}

	public void setApkProfileName(String apkProfileName) {
		this.apkProfileName = apkProfileName;
	}

	public int getRc() {
		return rc;
	}

	public void setRc(int rc) {
		this.rc = rc;
	}

	public String getRm() {
		return rm;
	}

	public void setRm(String rm) {
		this.rm = rm;
	}

}
