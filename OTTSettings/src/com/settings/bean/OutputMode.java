package com.settings.bean;


public class OutputMode {

	public String modeStr;
	public String modeValue;
	public String frequecy;
	public boolean isSelected;

	public OutputMode() {
		modeStr = "";
		modeValue = "";
		frequecy = "";
		isSelected = false;
	}

	public boolean equals(OutputMode other) {
		return modeValue.equalsIgnoreCase(other.modeValue)
				&& frequecy.equalsIgnoreCase(other.frequecy);
	}

}
