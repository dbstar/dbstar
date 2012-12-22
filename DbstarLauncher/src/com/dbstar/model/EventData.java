package com.dbstar.model;

public class EventData {
	public static final int EVENT_DELETE = 0x10001;
	public static final int EVENT_DATASIGNAL = 0x10002;
	public static final int EVENT_UPDATE_PROPERTY = 0x10003;
	public static final int EVENT_SMARTCARD_STATUS = 0x10004;
	public static final int EVENT_NEWMAIL = 0x10005;
	
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
		public boolean isValid;
		
		public SmartcardStatus() {
			isPlugIn = false;
			isValid = false;
		}
	}
	
	public static class NewMail {
		
	}
}
