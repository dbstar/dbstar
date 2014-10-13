package com.settings.bean;

import java.util.List;

public class UpgradeInfo {
	private int rc;
	private String rm;
	private List<Vapks> vapksList;

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

	public List<Vapks> getVapksList() {
		return vapksList;
	}

	public void setVapksList(List<Vapks> vapksList) {
		this.vapksList = vapksList;
	}

}
