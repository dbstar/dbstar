package com.dbstar.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.dbstar.util.LogUtil;


public class GDSystemConfigure {
	private static final String TAG = "GDSystemConfigure";

	public static final String EBooKFolder = "ebook";

	// Default Property values
	public static final String DefaultStorageDisk = "/storage/external_storage/sda1";
	public static final String DefaultPushDir = "/storage/external_storage/sda1";

	private static final String ConfigureFile = "/data/dbstar/dbstar.conf";
	public static final String UserDatabaseFile = "/data/dbstar/userdb.db";
	public static final String SmartHomeDatabase = "/data/dbstar/Smarthome.db";
	public static final String DefaultDbstarDBFile = "/data/dbstar/Dbstar.db";
	public static final String DefaultColumnResDir = "/data/dbstar/ColumnRes";
	public static final String DefaultDesFile = "/info/desc/Publication.xml";
	public static final long DefaultReconnectTime = 30000;
	
	// some global property, is related to device but not storage(flash/harddisk) should only save in /data/dbstar/Dbstar.db,
	// such as igmp addr, PushDir, ProductSN, DeviceModel and so on
	public static final String DeviceGlobalDB = "/data/dbstar/Dbstar.db";
	
	// Property Name
	private static final String PROPERTY_LOCALIZATION = "language";
	private static final String PROPERTY_DBSTARDATABSE = "DbstarDatabase";
	private static final String PROPERTY_PUSH_DIR = "PushDir";
	private static final String PROPERTY_COLUMNRES_DIR = "ColumnRes";
	private static final String PROPERTY_GUODIANSERVER = "GuodianServer";
	private static final String PROPERTY_PUSHEDMESSAGE = "PushedMessage";
	private static final String PROPERTY_GUODIAN_RECONNECT_TIME = "GuodianReconnectTime";

	private String Property_GuoWangDongTai;
	private String Property_GuoWangKuaiXun;
	private String Property_ShiPinDongTai;
	private String Property_GuoJiaDianWangBao;
	private String Property_DingShiRenWu;
	private String Property_YiJianKongZhi;
	private String Property_WoDeDianQi;
	private String Property_HaoNengFenXi;
	private String Property_WoDeYongDian;
	private String Property_YongDianMingXi;
	private String Property_YongDianTiYan;
	private String Property_JieNengChangShi;

	private String Property_ZaZhi;
	private String Property_BaoZhi;

	// paramter used for Guodian urls
	private String[][] mCategoryContents = { { Property_GuoWangDongTai, "" },
			{ Property_GuoWangKuaiXun, "" }, { Property_ShiPinDongTai, "" },
			{ Property_GuoJiaDianWangBao, "" }, { Property_DingShiRenWu, "" },
			{ Property_YiJianKongZhi, "" }, { Property_WoDeDianQi, "" },
			{ Property_HaoNengFenXi, "" }, { Property_WoDeYongDian, "" },
			{ Property_YongDianMingXi, "" }, { Property_YongDianTiYan, "" },
			{ Property_JieNengChangShi, "" } };

	// These values: get from configure file or hard coded.
	private String mDefaultStorageDisk = null;
	private String mIconRootDir = null;
	private String mLocalization = GDCommon.LangCN;
	private String mGuodianServer = null;
	private String mDbstarDatabase = null;
	private long mGuodianReconnectTime = DefaultReconnectTime;

	// demo data for push message
	List<String> mPushedMessage = null;

	// TODO: these two string maybe access in different thread.
	// how to make it safe to multiple-thread!
	private String mStorageDisk = null;
	private String mStorageDir = null;

	// configure system variables
	// call this when system started.
	// it will read the default variables from configure files first.
	// if not set in configure file, it will use hard coded values.
	public boolean configureSystem() {

		// read configures
		boolean ret = parseConfigure();

		defaultValueInit();

		return ret;
	}

	private void defaultValueInit() {
		if (mDefaultStorageDisk == null || mDefaultStorageDisk.isEmpty()) {
			LogUtil.d(TAG, "defaultValueInit(): mDefaultStorageDisk is nothing");
			mDefaultStorageDisk = DefaultStorageDisk;
		}

		if (mIconRootDir == null || mIconRootDir.isEmpty()) {
			LogUtil.d(TAG, "defaultValueInit(): mIconRootDir is nothing");
			mIconRootDir = DefaultColumnResDir;
		}

		if (mDbstarDatabase == null || mDbstarDatabase.isEmpty()) {
			LogUtil.d(TAG, "defaultValueInit(): mDbstarDatabase is nothing");
			mDbstarDatabase = DefaultDbstarDBFile;
		}
		
		LogUtil.d(TAG, "defaultValueInit(): mDefaultStorageDisk[" + mDefaultStorageDisk + "], mIconRootDir[" + mIconRootDir + "], mDbstarDatabase[" + mDbstarDatabase + "]");
	}

	// find the storage disk and push directory.
	// call this when disk is mounted or unmounted
	// it check whether the default disk and push dir exists.
	public boolean configureStorage() {
		mStorageDir = mStorageDisk = null;

		String disk = mDefaultStorageDisk;
		
		File file = new File(disk);
		if (file.exists()) {
			mStorageDir = disk;
			mStorageDisk = disk;
			LogUtil.d(TAG, "configureStorage(): disk[" + disk + "] is ready");
			return true;
		} else {
			LogUtil.d(TAG, "configureStorage(): disk[" + disk + "] is NOT ready");
			return false;
		}
	}

	// Parameters for Flash/Local storage
	public String getIconRootDir() {
		return mIconRootDir;
	}
	
	public void setIconRootDir(String vIconRootDir) {
		mIconRootDir = vIconRootDir;
	}

	public String getLocalization() {
		return mLocalization;
	}

	public void setLocalization(String localization) {
		mLocalization = localization;
	}

	public String getDVBDatabaseFile() {
		return mDbstarDatabase;
	}
	
	public String getDeviceGlobalDB() {
		return DeviceGlobalDB;
	}

	public void setDVBDatabaseFile(String vDbstarDatabase) {
		mDbstarDatabase = vDbstarDatabase;
	}

	public String getSmartHomeDBFile() {
		return SmartHomeDatabase;
	}

	// Need thread safe
	public String getStorageDisk() {
		return mStorageDisk;
	}
	
	public void setStorageDisk(String vStorageDisk) {
		mStorageDisk = vStorageDisk;
	}

	public String getStorageDir() {
		return mStorageDir;
	}
	
	public void setStorageDir(String vStorageDir) {
		mStorageDir = vStorageDir;
	}
	
	public long getGuodianReconnectTime() {
		return mGuodianReconnectTime;
	}

	public String getDetailsDataFile(ContentData content) {
		if (content.XMLFilePath == null || content.XMLFilePath.isEmpty())
			return "";

		String xmlFile = new String(mStorageDir + "/" + content.XMLFilePath
				+ DefaultDesFile);

		File file = new File(xmlFile);
		if (!file.exists()) {
			xmlFile = "";
		}

		return xmlFile;
	}

	public String getMediaFile(ContentData content) {
		String file = "";

		if (content == null || content.MainFile == null)
			return file;

		final String mainFile = content.MainFile.FileURI;
		if (mainFile != null && !mainFile.isEmpty()) {
			file = mStorageDir + "/" + mainFile;

			File f = new File(file);
			if (!f.exists()) {
				file = "";
			}
		}

		return file;
	}

	public String getDRMFile(ContentData content) {
		String file = "";

		final String drmFile = content.DRMFile;
		if (drmFile != null && !drmFile.isEmpty()) {
			file = mStorageDir + "/" + drmFile;
		}

		return file;
	}

	public String getThumbnailFile(ContentData content) {

		String file = "";
		List<ContentData.Poster> posters = content.Posters;
		if (posters == null || posters.size() == 0)
			return file;

		for (int i = 0; i < posters.size(); i++) {
			ContentData.Poster poster = posters.get(i);
			if (poster.URI == null || poster.URI.isEmpty())
				continue;

			String uri = new String(mStorageDir + "/" + poster.URI);

			File f = new File(uri);
			if (f.exists()) {
				file = uri;
				break;
			}
		}

		return file;
	}

	public String getPreviewFile(PreviewData data) {
		String file = "";
		if (data == null || data.URI == null || data.URI.isEmpty()) {
			return file;
		}

		file = mStorageDir + "/" + data.URI;
		LogUtil.d(TAG, "preivew path == " + file);
		
		File f = new File(file);
		if (!f.exists()) {
			file = "";
		}

		return file;
	}

	public String getEBookFolder() {
		return new String(mStorageDir + "/" + EBooKFolder);
	}

	public String getEbookFile(String category) {
		String categoryRoot = getEBookFolder();
		String ebookFile = "";
		if (category.equals(Property_ZaZhi)) {
			ebookFile = categoryRoot
					+ "/zazhi/jplife2/jplife2/content_index.html";
		} else if (category.equals(Property_BaoZhi)) {
			ebookFile = categoryRoot + "/baozhi/20120329/index.html";
		}

		LogUtil.d(TAG, "category = " + category + " path=" + ebookFile);

		return ebookFile;
	}

	public String getCategoryContent(String category) {
		String content = "";
		for (int i = 0; i < mCategoryContents.length; i++) {
			if (category.equals(mCategoryContents[i][0])) {
				content = mCategoryContents[i][1];
				break;
			}
		}
		return content;
	}

	public String getUserDatabaseFile() {
		return UserDatabaseFile;
	}

	public String getGuodianServer() {
		return mGuodianServer;
	}

	public void getPushedMessage(List<String> retMessages) {
		if (retMessages == null || mPushedMessage == null
				|| mPushedMessage.size() == 0)
			return;

		for (int i = 0; i < mPushedMessage.size(); i++) {
			retMessages.add(mPushedMessage.get(i));
		}
	}

	private boolean parseConfigure() {

		File configureFile = new File(ConfigureFile);
		if (configureFile == null || !configureFile.exists()) {
			LogUtil.d(TAG, "there is no configureFile");
			return false;
		}

		try {
			String UTF8 = "utf8";
			int BUFFER_SIZE = 8192;

			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(configureFile), UTF8), BUFFER_SIZE);

			String line = br.readLine();
			String splitter = "";
			if (line != null && !line.equals("")) {
				String[] property = new String[2];

				int start = line.indexOf(":");
				property[0] = line.substring(0, start);
				property[1] = line.substring(start + 1);

				splitter = property[1].trim();
				// Log.d(TAG, "splitter = " + splitter);

				while ((line = br.readLine()) != null) {
					if (line.isEmpty())
						continue;

					start = line.indexOf(splitter);
					property[0] = line.substring(0, start);
					property[1] = line.substring(start + 1);

					//Log.d(TAG, property[0] + "=" + property[1]);

					if (property[0].equals(PROPERTY_DBSTARDATABSE)) {
						mDbstarDatabase = property[1].trim();
					} else if (property[0].equals(PROPERTY_PUSH_DIR)) {
						mDefaultStorageDisk = property[1].trim();
						LogUtil.d(TAG, "config file property[" + PROPERTY_PUSH_DIR + "]:[" + mDefaultStorageDisk + "]");
					} else if (property[0].equals(PROPERTY_COLUMNRES_DIR)) {
						mIconRootDir = property[1].trim();
					} else if (property[0].equals(PROPERTY_LOCALIZATION)) {
						setLocalization(property[1].trim());
					} else if (property[0].equals("Property_GuoWangDongTai")) {
						mCategoryContents[0][0] = property[1].trim();
					} else if (property[0].equals("Property_GuoWangKuaiXun")) {
						mCategoryContents[1][0] = property[1].trim();
					} else if (property[0].equals("Property_ShiPinDongTai")) {
						mCategoryContents[2][0] = property[1].trim();
					} else if (property[0].equals("Property_GuoJiaDianWangBao")) {
						mCategoryContents[3][0] = property[1].trim();
					} else if (property[0].equals("Property_DingShiRenWu")) {
						mCategoryContents[4][0] = property[1].trim();
					} else if (property[0].equals("Property_YiJianKongZhi")) {
						mCategoryContents[5][0] = property[1].trim();
					} else if (property[0].equals("Property_WoDeDianQi")) {
						mCategoryContents[6][0] = property[1].trim();
					} else if (property[0].equals("Property_HaoNengFenXi")) {
						mCategoryContents[7][0] = property[1].trim();
					} else if (property[0].equals("Property_WoDeYongDian")) {
						mCategoryContents[8][0] = property[1].trim();
					} else if (property[0].equals("Property_YongDianMingXi")) {
						mCategoryContents[9][0] = property[1].trim();
					} else if (property[0].equals("Property_YongDianTiYan")) {
						mCategoryContents[10][0] = property[1].trim();
					} else if (property[0].equals("Property_JieNengChangShi")) {
						mCategoryContents[11][0] = property[1].trim();
					} else if (property[0].equals("Property_ZaZhi")) {
						Property_ZaZhi = property[1].trim();
					} else if (property[0].equals("Property_BaoZhi")) {
						Property_BaoZhi = property[1].trim();
					} else if (property[0].equals(PROPERTY_GUODIANSERVER)) {
						mGuodianServer = property[1].trim();
					} else if (property[0].equals(PROPERTY_PUSHEDMESSAGE)) {
						if (mPushedMessage == null) {
							mPushedMessage = new ArrayList<String>();
						}

						mPushedMessage.add(property[1].trim());
					} else if (property[0].equals(PROPERTY_GUODIAN_RECONNECT_TIME)) {
						String time = property[1].trim();
						if (time.length()>0) {
							mGuodianReconnectTime = Long.parseLong(time);
						}
					} else {
						String category = property[0].trim();
						for (int i = 0; i < mCategoryContents.length; i++) {
							if (category.equals(mCategoryContents[i][0])) {
								mCategoryContents[i][1] = property[1].trim();
								break;
							}
						}
					}
				}

			}
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private String[] getMountedDisks() {
		String[] paths = null;
		ArrayList<String> list = new ArrayList<String>();

		File mnt = new File("/mnt");
		if (mnt == null || !mnt.exists()) {
		    LogUtil.d(TAG, "No /mnt folder!");
			return null;
		}

		File[] mnts = mnt.listFiles();
		if (mnts == null)
			return null;

		for (File file : mnts) {
			// Log.d(TAG, " path: " + file);
			if (!("/mnt/sata".equals(file.toString())
					|| "/mnt/sata".equals(file.toString())
					|| "/mnt/asec".equals(file.toString()) || "/mnt/secure"
						.equals(file.toString())))// ||"/mnt/sdcard".equals(file.toString())))
			{
				list.add(file.toString());
			}
		}

		paths = (String[]) list.toArray(new String[list.size()]);

		return paths;
	}
}
