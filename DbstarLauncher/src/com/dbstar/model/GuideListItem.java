package com.dbstar.model;

public class GuideListItem {
	// date format: yyyy-MM-dd HH:mm:ss
	public String Date; // Date read from database
	public String NormalizedDate; // Date with format yyyy-mm-dd 00:00:00, used to compare items.
	public String ColumnType;
	public String PublicationID;
	public String GuideListID;
	public String Name;
	public boolean originalSelected;
	public boolean isSelected;
}
