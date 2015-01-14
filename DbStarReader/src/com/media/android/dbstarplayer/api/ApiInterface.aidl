/*
 * This code is in the public domain.
 */

package com.media.android.dbstarplayer.api;

import com.media.android.dbstarplayer.api.ApiObject;

interface ApiInterface {
	ApiObject request(int method, in ApiObject[] parameters);
	List<ApiObject> requestList(int method, in ApiObject[] parameters);
	Map requestMap(int method, in ApiObject[] parameters);
}
