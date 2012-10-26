package com.dbstar.app;

import com.dbstar.service.GDDataProviderService;

public class GDFavoriteTVActivity extends GDTVActivity {
	public void onServiceStart() {
//		super.onServiceStart();

		mService.getFavoriteTV(this);
	}
	
	public void updateData(int type, Object key, Object data) {
		if (type == GDDataProviderService.REQUESTTYPE_GETFAVORITETV) {
			super.updateData(GDDataProviderService.REQUESTTYPE_GETPUBLICATIONSET, key, data);
		}
	}
}
