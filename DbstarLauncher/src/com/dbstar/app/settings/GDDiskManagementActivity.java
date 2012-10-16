package com.dbstar.app.settings;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.dbstar.R;
import com.dbstar.app.GDBaseActivity;
import com.dbstar.model.GDCommon;
import com.dbstar.model.GDDiskInfo;
import com.dbstar.util.StringUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class GDDiskManagementActivity extends GDBaseActivity {

	private static final String TAG = "GDDiskManagmentActivity";
	
	private static final int SERIES_DISKUSED = 0;
	private static final int SERIES_DISKSPACE = 1;

	private static final double GAURDPERCENT = 0.05;
	
	private static int[] COLORS = new int[] { Color.BLUE,
			Color.MAGENTA };

	private CategorySeries mSeries = new CategorySeries("");
	private DefaultRenderer mRenderer = new DefaultRenderer();
	private GraphicalView mChartView;
	private String mDisk;
	
	private String[] mSeriesNames = null;
	
	private TextView mDiskSizeView, mDiskUsedView, mDiskEmptyView, mDiskGuardView;
	private long mDiskSize, mDiskUsedSize, mDiskEmptySpace, mDiskGuardSpace;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mDisk = intent.getStringExtra(GDCommon.KeyDisk);
		Log.d(TAG, "disk = " + mDisk);
		
		setContentView(R.layout.diskmanagement_view);
		
		mDiskSizeView = (TextView) findViewById(R.id.text_disksize);
		mDiskUsedView = (TextView) findViewById(R.id.text_diskused);
		mDiskEmptyView = (TextView) findViewById(R.id.text_diskempty);
		mDiskGuardView = (TextView) findViewById(R.id.text_diskguard);
		
		mSeriesNames = new String[2];
		mSeriesNames[SERIES_DISKUSED] = getResources().getString(R.string.diskmanagement_usedspace);
		mSeriesNames[SERIES_DISKSPACE] = getResources().getString(R.string.diskmanagement_emptyspace);

		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.argb(100, 50, 50, 50));
		mRenderer.setChartTitleTextSize(20);
		mRenderer.setLabelsTextSize(15);
		mRenderer.setLegendTextSize(15);
		mRenderer.setMargins(new int[] { 20, 30, 15, 0 });
		//mRenderer.setZoomButtonsVisible(true);
		mRenderer.setStartAngle(90);
		
		
		initializeView();
		
		mMenuPath = intent.getStringExtra(INTENT_KEY_MENUPATH);
		showMenuPath(mMenuPath.split(MENU_STRING_DELIMITER));
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		
		showDiskInfo();
		
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart_container);
			mChartView = ChartFactory.getPieChartView(this, mSeries, mRenderer);
			//mRenderer.setClickEnabled(true);
			//mRenderer.setSelectableBuffer(10);
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		} else {
			mChartView.repaint();
		}
	}

	private void showDiskInfo() {
		GDDiskInfo.DiskInfo diskInfo = GDDiskInfo.getDiskInfo(mDisk, true);
		if (diskInfo == null)
			return;

		mDiskSize = diskInfo.RawDiskSize;
		mDiskEmptySpace = diskInfo.RawDiskSpace;
		mDiskUsedSize = diskInfo.RawDiskSize - diskInfo.RawDiskSpace;
		mDiskGuardSpace = (long)(mDiskSize*GAURDPERCENT);

		
		StringUtil.SizePair diskSizePair = StringUtil.formatSize(mDiskUsedSize);
		String diskUeseSize = StringUtil.formatFloatValue(diskSizePair.Value)
				+ StringUtil.getUnitString(diskSizePair.Unit);
		
		mDiskSizeView.setText(diskInfo.DiskSize);
		mDiskUsedView.setText(diskUeseSize);
		mDiskEmptyView.setText(diskInfo.DiskSpace);
		
		diskSizePair = StringUtil.formatSize(mDiskGuardSpace);
		String diskGuardSize = StringUtil.formatFloatValue(diskSizePair.Value)
				+ StringUtil.getUnitString(diskSizePair.Unit);
		mDiskGuardView.setText(diskGuardSize);
		
		addSeries(SERIES_DISKSPACE, mDiskEmptySpace);
		addSeries(SERIES_DISKUSED, mDiskUsedSize);
	}
	
	private void addSeries(int seriesIndex, long seriesValue) {
		double value = (double)seriesValue;

		mSeries.add(mSeriesNames[seriesIndex], value);
		SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
		renderer.setColor(COLORS[seriesIndex % COLORS.length]);
		mRenderer.addSeriesRenderer(renderer);
	}
}