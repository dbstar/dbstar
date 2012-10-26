package com.dbstar.app;

import com.dbstar.service.GDDataProviderService;

public class GDFavoriteMovieActivity extends GDHDMovieActivity {
	public void onServiceStart() {
//		super.onServiceStart();

		mService.getFavoriteMovie(this);
	}
	
	public void updateData(int type, Object key, Object data) {
		if (type == GDDataProviderService.REQUESTTYPE_GETFAVORITEMOVIE) {
			super.updateData(GDDataProviderService.REQUESTTYPE_GETPUBLICATION, key, data);
		}
	}
}
