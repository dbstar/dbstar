package com.dbstar.DbstarDVB.model;

import java.io.Serializable;

public class MediaData implements Serializable {
	private static final long serialVersionUID = 100L;
	// playback data
	public String mediaURL;
	
	// Publication data
	public String PublicationID;
	public String ColumnType;
	public String URI;
	public int    EpisodeIndex;
	
	// publication set data
	public String SetID;
	public String Name;
	public String Description;
	public String Poster;
	public String Trailer;
}