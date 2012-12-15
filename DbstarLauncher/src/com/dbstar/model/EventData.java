package com.dbstar.model;

public class EventData {
	public static final int EVENT_DELETE = 0;
	public static final int EVENT_DATASIGNAL = 1;
	public static final int EVENT_UPDATE_PROPERTY = 2;
	public static final int EVENT_SMARTCARD_STATUS = 3;
	
	public static class DeleteEvent {
		public String PublicationId;
		public String PublicationSetId;
	}
	
	public static class DataSignalEvent {
		public boolean hasSignal;
	}
	
	public static class UpdatePropertyEvent {
		public String PublicationId;
		public String PropertyName;
		public Object PropertyValue;
	}
	
	public static class SmartcardStatus {
		public boolean isPlugIn;
	}
}
