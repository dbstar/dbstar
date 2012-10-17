package com.dbstar.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class GDSystemConfigure {
	private static final String TAG = "GDSystemConfigure";

	public static final String EBooKFolder = "ebook";
	private static final String GuodianServer = "GuodianServer";
	
	public static final String DefaultStorageDisk = "/mnt/sda1";
	private static final String ConfigureFile = "/data/dbstar/dbstar.conf";
	public static final String SmartHomeDatabase = "/data/dbstar/smarthome/database/smarthome.db";	
	public static final String DVBDatabaseFile = "Dbstar.db";
	public static final String UserDatabaseFile = "userdb.db";

	public static final String DefaultDesFile = "/info/desc/Publication.xml";

	private static final String PROPERTY_STORAGE_DIR = "storage";
	private static final String PROPERTY_LOCALIZATION = "language";

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

	// the storage disk:
	// 1. it maybe set in the configure file
	// 2. if not set in configure file, we will first get the default one,
	//    if the default one is not available, try to get from the mounted disk
	//    that has the "dbstar" folder.
	private String mStorageDisk = "";
	private String mStorageDir = "";
	private String mIconRootDir;
		
	private String mLocalization = GDCommon.LangCN;

	// paramter used for Guodian urls
	private String[][] mCategoryContents = { { Property_GuoWangDongTai, "" },
			{ Property_GuoWangKuaiXun, "" }, { Property_ShiPinDongTai, "" },
			{ Property_GuoJiaDianWangBao, "" }, { Property_DingShiRenWu, "" },
			{ Property_YiJianKongZhi, "" }, { Property_WoDeDianQi, "" },
			{ Property_HaoNengFenXi, "" }, { Property_WoDeYongDian, "" },
			{ Property_YongDianMingXi, "" }, { Property_YongDianTiYan, "" },
			{ Property_JieNengChangShi, "" } };
	// demo data for push message
	List<String> mPushedMessage = null;

	private String mGuodianServer = "";
	
	// read configure from file
	// call this method every time the disk is mounted or unmounted
	public boolean readConfigure() {

		File configureFile = new File(ConfigureFile);
		if (configureFile == null || !configureFile.exists()) {
			return false;
		}

		// clear the cached path value
		mStorageDir = "";

		// read configures
		if (!parseConfigure(configureFile)) {
			return false;
		}

		return true;
	}

	// get the storage directory
	// call this method every time the disk is mounted or unmounted
	public boolean configureStorage() {
		// if this disk is already set, check whether it is valid

		if (!mStorageDir.equals("")) {
			File storageDir = new File(mStorageDir);
			if (storageDir != null && storageDir.exists())
				return true;
		}
		
		// clear the cached disk path
		mStorageDir = "";
		
		// get default disk
		File defaultDir = new File(DefaultStorageDisk + "/dbstar");
		if (defaultDir.exists()) {
			mStorageDisk = DefaultStorageDisk;
			mStorageDir = DefaultStorageDisk + "/dbstar";
		} else {
			// get from mounted disks
			String paths[] = getMountedDisks();
			for (String path : paths) {
				File dbstarFolder = new File(path + "/dbstar");

				if (dbstarFolder.exists()) {
					File[] files = dbstarFolder.listFiles();
					if (files != null && files.length > 0) {
						mStorageDisk = path;
						mStorageDir = path + "/dbstar";
						Log.d(TAG, "root dir = " + mStorageDir);
						break;
					}
				}
			}
		}

		if (mStorageDir.isEmpty()) {
			return false;
		}
		
		return true;
	}

	public boolean isStorageDisk(String disk) {
		boolean ret = false;

		File dataFolder = new File(disk + "/dbstar");
		if (dataFolder != null && dataFolder.exists()) {
			ret = true;
		}

		return ret;
	}
	
	public boolean isDiskAvailable() {
		boolean available = false;
		Log.d(TAG, "check disk available " + mStorageDisk);

		if(mStorageDisk == null || mStorageDisk.isEmpty())
			return available;
		
		File file = new File(mStorageDisk);
		if (file != null && file.exists())
			available = true;
		Log.d(TAG, "=" + available);
		return available;
	}

	public String getStorageDisk() {
		return mStorageDisk;
	}

	public String getStorageDir() {
		return mStorageDir;
	}
	
	public String getIconRootDir() {
		mIconRootDir = mStorageDir;
		return mIconRootDir;
	}

	public String getLocalization() {
		return mLocalization;
	}
	
	private void setLocalization(String localization) {
		mLocalization = localization;
	}
	
	public String getDVBDatabaseFile() {
		if (mStorageDir == null || mStorageDir.isEmpty())
			return "";

		String dbFile = new String(mStorageDir + "/" + DVBDatabaseFile);
		return dbFile;
	}

	public String getSmartHomeDBFile() {
		String dbFile = SmartHomeDatabase;

		return dbFile;
	}
	
	public String getUserDatabaseFile() {
		if (mStorageDir == null || mStorageDir.isEmpty())
			return "";

		String dbFile = new String(mStorageDir + "/" + UserDatabaseFile);
		return dbFile;
	}
	
	public String getDetailsDataFile(ContentData content) {
		String xmlFile = new String(mStorageDir + "/" + content.XMLFilePath + DefaultDesFile);

		File file = new File(xmlFile);
		if (!file.exists()) {
			xmlFile = "";
		}

		return xmlFile;
	}

	public String getMediaFile(ContentData content) {
		String file;

		file = mStorageDir + "/" + content.MainFile.FileURI;

		return file;
	}
	
	public String getDRMFile(ContentData content) {
		String file;

		file = mStorageDir + "/" + content.DRMFile;

		return file;
	}

	public String getThumbnailFile(ContentData content) {

		// if (content != null) {
		// // for test
		// String str = new String(mStorageDir + mPushPath + content.XMLFilePath
		// + "/pic");
		// File dir = new File(str);
		// String[] fs = dir.list();
		//
		// Log.d(TAG, "file=" + str);
		//
		// if (fs.length > 0) {
		// return str + "/" + fs[0];
		// }
		// }

		String file = "";
		List<ContentData.Poster> posters = content.Posters;
		if (posters != null && posters.size() > 0) {
			for (int i = 0; i < posters.size(); i++) {
				file = new String(mStorageDir + "/" + posters.get(i).URI);
				Log.d(TAG, "file = " + file);
				File f = new File(file);
				if (f.exists()) {
					break;
				} else {
					file = "";
				}
			}
		}

		return file;
	}

	public String getDescritpionFile(ContentData content) {
		String movie = content.XMLFilePath;
		String file = new String(mStorageDir + "/" + movie);
		file = file + "/data/description.txt";

		File descriptionFile = new File(file);
		if (!descriptionFile.exists()) {
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

		Log.d(TAG, "category = " + category + " path=" + ebookFile);

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

	private boolean parseConfigure(File configureFile) {

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

					// Log.d(TAG, property[0] + "=" + property[1]);

					if (property[0].equals(PROPERTY_STORAGE_DIR)) {
						mStorageDir = property[1].trim();
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
					} else if (property[0].equals("GuodianServer")) {
						mGuodianServer = property[1].trim();
					} else if (property[0].equals("PushedMessage")) {
						if (mPushedMessage == null) {
							mPushedMessage = new ArrayList<String>();
						}

						mPushedMessage.add(property[1].trim());

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
			Log.d(TAG, "No /mnt folder!");
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
