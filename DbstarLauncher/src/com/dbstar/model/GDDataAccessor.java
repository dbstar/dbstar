package com.dbstar.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class GDDataAccessor {
	private static final String TAG = "GDDataAccessor";

	public static final String EBOOK_FOLDER = "ebook";
	public static final String MOVIE_FOLDER = "movie";
	public static final String MEDIA_FOLDER = "media";
	public static final String TV_FOLDER = "tv";
	public static final String DVBDATABASE_FOLDER = "database";
	public static final String DVBDATABASE_File = "Dbstar.db";
	public static final String DefaultStorageDisk = "/mnt/sda1";

	public static final String CONFIGURE_File = "dbstar.conf";
	public static final String SMARTHOMEDATABASE = "/data/dbstar/smarthome/database/smarthome.db";
	
	private static final String ConfigureFile = "/data/dbstar/dbstar.conf";
	private static final String WEATHER_CITYCODE_DATABASE = "/data/dbstar/weather_citycode.db";
	private static final String DefaultPushPath = "/videos1/pushvod/";
	private static final String GuodianServer = "GuodianServer";

	private static final String PROPERTY_STORAGE_DIR = "storage";
	private static final String PROPERTY_DEMO_MOVIE = "demo_movie";
	private static final String PROPERTY_DEMO_PIC = "demo_pic";
	private static final String PROPERTY_HOMEPAGE = "homepage";

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

	private String mStorageDisk = "";
	private String mStorageDir = "";
	private String mPushPath = "";
	private String mHomePage = "";
	private String mGuodianServer = "";
	private String mIconRootDir;

	private String[][] mCategoryContents = { { Property_GuoWangDongTai, "" },
			{ Property_GuoWangKuaiXun, "" }, { Property_ShiPinDongTai, "" },
			{ Property_GuoJiaDianWangBao, "" }, { Property_DingShiRenWu, "" },
			{ Property_YiJianKongZhi, "" }, { Property_WoDeDianQi, "" },
			{ Property_HaoNengFenXi, "" }, { Property_WoDeYongDian, "" },
			{ Property_YongDianMingXi, "" }, { Property_YongDianTiYan, "" },
			{ Property_JieNengChangShi, "" } };

	List<String> mPushedMessage = null;

	public void configure() {
		mStorageDir = "";

		File configureFile = new File(ConfigureFile);
		if (configureFile != null && configureFile.exists()) {
			parseConfigure(configureFile);
		}

		if (mPushPath.equals("")) {
			mPushPath = DefaultPushPath;
		}

		String paths[] = getMountedDisks();
		for (String path : paths) {
			File dbstarFolder = new File(path + "/dbstar");

			if (dbstarFolder != null && dbstarFolder.exists()) {
				File[] files = dbstarFolder.listFiles();
				if (files != null && files.length > 0) {
					if (mStorageDir.equals("")) {
						mStorageDisk = path;
						mStorageDir = path + "/dbstar";
						Log.d(TAG, "root dir = " + mStorageDir);
					}
					break;
				}
			}
		}
	}

	public boolean isStorageDisk(String disk) {
		boolean ret = false;

		File dataFolder = new File(disk + "/dbstar");
		if (dataFolder != null && dataFolder.exists()) {
			ret = true;
		}

		return ret;
	}

	public String getStorageDisk() {
		return mStorageDisk;
	}

	public String getStorageDir() {
		return mStorageDir;
	}

	public String getDefaultStorageDisk() {
		return DefaultStorageDisk;
	}

	public String getIconRootDir() {
		mIconRootDir = mStorageDir;
		return mIconRootDir;
	}

	public String getDatabaseFile() {
		if (mStorageDir == null || mStorageDir.isEmpty())
			return "";

		String dbFile = new String(mStorageDir + "/" + DVBDATABASE_File);

//		File file = new File(dbFile);
//		if (!file.exists()) {
//			dbFile = "";
//		}
		return dbFile;
	}

	public String getSmartHomeDBFile() {
		String dbFile = SMARTHOMEDATABASE;

//		File file = new File(dbFile);
//		if (!file.exists()) {
//			dbFile = "";
//		}

		return dbFile;
	}

	public String getDetailsDataFile(ContentData content) {
		String xmlFile = new String(mStorageDir + "/" + content.XMLFilePath);

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
		return new String(mStorageDir + "/" + EBOOK_FOLDER);
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

	public String getWeatherCityCodeBDFile() {
		return WEATHER_CITYCODE_DATABASE;
	}

	public String getHomePage() {
		return mHomePage;
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

	private String mDemoMovie;
	private String mDemoPic;

	public String getDemoMovie() {
		String file = "";
		if (!mDemoMovie.isEmpty()) {
			file = mStorageDir + "/" + MEDIA_FOLDER + "/" + mDemoMovie;
		}
		return file;
	}

	public String getDemoPic() {
		String file = "";
		if (!mDemoPic.isEmpty()) {
			file = mStorageDir + "/" + MEDIA_FOLDER + "/" + mDemoPic;
		}
		return file;
	}

	private void parseConfigure(File configureFile) {
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
					} else if (property[0].equals(PROPERTY_DEMO_MOVIE)) {
						mDemoMovie = property[1].trim();
					} else if (property[0].equals(PROPERTY_DEMO_PIC)) {
						mDemoPic = property[1].trim();
					} else if (property[0].equals(PROPERTY_HOMEPAGE)) {
						mHomePage = property[1].trim();
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
					} else if (property[0].equals("pushpath")) {
						mPushPath = property[1].trim();
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
		}
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
				/*
				 * File[] disks = file.listFiles(); if (disks != null) { for
				 * (File disk : disks) { list.add(disk.toString()); } }
				 */

				list.add(file.toString());
			}
		}

		paths = (String[]) list.toArray(new String[list.size()]);

		return paths;
	}
}
