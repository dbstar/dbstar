/*
 * Copyright (C) 2010-2013 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package com.media.android.dbstarplayer.preferences;

import android.content.Context;
import android.preference.Preference;
import android.view.View;
import android.widget.TextView;

import com.media.android.dbstarplayer.R;
import com.media.zlibrary.core.util.ZLColor;
import com.media.zlibrary.ui.android.util.ZLAndroidColorUtil;

public abstract class ColorPreference extends Preference {
	protected ColorPreference(Context context) {
		super(context);
		setWidgetLayoutResource(R.layout.color_preference);
	}

	public abstract String getTitle();
	protected abstract ZLColor getSavedColor();
	protected abstract void saveColor(ZLColor color);

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		((TextView)view.findViewById(R.id.color_preference_title)).setText(getTitle());
		final ZLColor color = getSavedColor();
		view.findViewById(R.id.color_preference_widget).setBackgroundColor(
			color != null ? ZLAndroidColorUtil.rgb(color) : 0
		);
	}

	@Override
	protected void onClick() {

	}
}
