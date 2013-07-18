package com.dbstar.guodian.parse;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


import com.dbstar.guodian.data.AreaInfo;
import com.dbstar.guodian.data.BusinessArea;
import com.dbstar.guodian.data.JsonTag;
import com.dbstar.util.LogUtil;

public class AreaInfoHandler {
	private static final String TAG = "AreaInfoHandler";

	public static AreaInfo parse(String data) {

	    LogUtil.d(TAG, "data = " + data);
		AreaInfo info = null;

		JSONTokener jsonParser = new JSONTokener(data);

		try {
			info = new AreaInfo();
			JSONArray array = (JSONArray) jsonParser.nextValue();

			JSONObject rootObject = (JSONObject) array.get(0);

			array = (JSONArray) rootObject
					.getJSONArray(JsonTag.TAGProvinceList);

			if (array.length() > 0) {
				ArrayList<AreaInfo.Area> areas = parseAreas(array);
				if (areas != null && areas.size() > 0) {
					info.Provinces = constructProvincesList(areas);
				}
			}

			JSONObject object = rootObject.getJSONObject(JsonTag.TAGDefaultArea);
			info.ProvinceId = object.getString(JsonTag.TAGProvinceId);
			info.ProvinceName = object.getString(JsonTag.TAGProvinceName);
			info.CityId = object.getString(JsonTag.TAGCityId);
			info.CityName = object.getString(JsonTag.TAGCityName);
			info.ZoneId = object.getString(JsonTag.TAGZoneId);
			info.ZoneName = object.getString(JsonTag.TAGZoneName);

			array = (JSONArray) rootObject
					.getJSONArray(JsonTag.TAGBusinessList);

			int length = array.length();
			if (length > 0) {
				info.BusinessList = new ArrayList<BusinessArea>();
				for (int i = 0; i < length; i++) {
					JSONObject item = array.getJSONObject(i);
					BusinessArea business = BusinessAreaHandler
							.parseBusiness(item);
					info.BusinessList.add(business);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return info;
	}
	public static ArrayList<AreaInfo.Area> parseAreas(String data){
	    try {
            JSONArray array = new JSONArray(data);
            return parseAreas(array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
	    return null;
	    
	}
	private static ArrayList<AreaInfo.Area> parseAreas(JSONArray array)
			throws JSONException {
		ArrayList<AreaInfo.Area> areas = new ArrayList<AreaInfo.Area>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = (JSONObject) array.get(i);
			AreaInfo.Area area = parseArea(object);
			areas.add(area);
		}
		return areas;
	}

	
	private static AreaInfo.Area parseArea(JSONObject object)
			throws JSONException {
		AreaInfo.Area area = new AreaInfo.Area();
		area.Guid = object.getString(JsonTag.TAGAreaGuid);
		area.Name = object.getString(JsonTag.TAGArea_Name);
		area.Pid = object.getString(JsonTag.TAGPid);
		return area;
	}

	private static ArrayList<AreaInfo.Area> constructProvincesList(
			ArrayList<AreaInfo.Area> areas) {
		ArrayList<AreaInfo.Area> provinces = new ArrayList<AreaInfo.Area>();
		// 1. get provinces
		int i = 0;
		while (areas.size() > 0 && i < areas.size()) {
			AreaInfo.Area area = areas.get(i);
			if (area.Pid != null && area.Pid.equals("1")) {
				areas.remove(i);
				area.Type = AreaInfo.TypeProvince;
				provinces.add(area);
			} else {
				i++;
			}
		}

		if (provinces.size() == 0 || areas.size() == 0) {
			return provinces;
		}

		// 2. get cities
		i = 0;
		while (areas.size() > 0 && i < areas.size()) {
			AreaInfo.Area area = areas.get(i);

			if (addToParent(provinces, area)) {
				area.Type = AreaInfo.TypeCity;
				areas.remove(i);
			} else {
				i++;
			}
		}

		// 3. get zones
		i = 0;
		while (areas.size() > 0 && i < areas.size()) {
			AreaInfo.Area area = areas.get(i);

			boolean ret = true;
			int size = provinces.size();

			for (int j = 0; j < size; j++) {
				AreaInfo.Area province = provinces.get(j);
				if (province.SubArea != null
						&& addToParent(province.SubArea, area)) {
					area.Type = AreaInfo.TypeZone;
					areas.remove(i);
					ret = false;
					break;
				}
			}

			if (ret)
				i++;
		}

		return provinces;
	}

	private static boolean addToParent(ArrayList<AreaInfo.Area> parents,
			AreaInfo.Area area) {
		for (int i = 0; i < parents.size(); i++) {
			AreaInfo.Area p = parents.get(i);
			if (p.Guid != null && p.Guid.equals(area.Pid)) {
				if (p.SubArea == null) {
					p.SubArea = new ArrayList<AreaInfo.Area>();
				}

				p.SubArea.add(area);

				return true;
			}
		}

		return false;
	}
}
