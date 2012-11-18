package com.dbstar.DbstarDVB.VideoPlayer.alert;

public interface ViewStateManager {

	public ViewState getState(String id);

	public void addState(String id, ViewState state);

	public void changeToState(ViewState state, Object args);
}
