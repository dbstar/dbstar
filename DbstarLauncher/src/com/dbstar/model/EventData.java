package com.dbstar.model;

public class EventData {
	public static final int EVENT_DELETE = 0;
	public static final int EVENT_DATASIGNAL = 1;
	
	public static class DeleteEvent {
		public String PublicationId;
		public String PublicationSetId;
	}
	
	public static class DataSignalEvent {
		public boolean hasSignal;
	} 
}
