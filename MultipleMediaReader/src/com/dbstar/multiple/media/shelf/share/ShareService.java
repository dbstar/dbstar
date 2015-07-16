package com.dbstar.multiple.media.shelf.share;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.dbstar.multiple.media.common.ShelfController;
import com.dbstar.multiple.media.data.Book;
import com.dbstar.multiple.media.data.NewsPaper;
import com.dbstar.multiple.media.util.GLog;
import com.dbstar.multiple.media.util.StringUtil;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.ethernet.EthernetManager;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class ShareService extends Service{
    ConnectivityManager mConnectManager;
    private ServerThread mServerThread;
    private EthernetManager mEthManager;
    private WifiManager mWifiManager;
    private LoaclBinder mBinder = new LoaclBinder();
    private GLog mLog;
    public static final int PORT = 8080;
    public String mBookColumnId;
    public String mNewsPaperColumnId;
    public String mMagazineColumnId;
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
   public class LoaclBinder extends Binder {
        public ShareService getService(){
            return ShareService.this;
        }
        
    }
   
       @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mLog = GLog.getLogger("RMShare");
        mLog.i("shareservice onCreate");
        mConnectManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        mEthManager = (EthernetManager)getSystemService(Context.ETH_SERVICE);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "ShareService");
        wakeLock.acquire();
        mWifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        reqisterConnectReceiver();
    }
    
    private void reqisterConnectReceiver() {
       registerReceiver(mNetworkReceiver,  new IntentFilter(
               ConnectivityManager.CONNECTIVITY_ACTION));
    }
    private void unregisterConnectReceiver() {
       unregisterReceiver(mNetworkReceiver);
    }
    
    
    
    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            initServerThread();
        }
    };
    private List<LoadData > mBookDatas;
    private List<LoadData > mNewsPaperDatas;
    
    private void initServerThread() {
        mLog.i("initServerThread +++++++++++++++++++++++++++++");
        try {
            if (isNetworkConnected()) {
                mLog.i("isNetworkConnected = true");
                String ip = getBestIpAddress();
                mLog.i("getBestIpAddress = " + ip);
                if (mServerThread == null || !mServerThread.isAlive()) {
                    mLog.i("mServerThread = null");
                    if (ip == null)
                        return;
                    
                    mServerThread = new ServerThread(ip, PORT, null,this);
                    mServerThread.setColumn(mBookColumnId,mNewsPaperColumnId, mMagazineColumnId);
                    mServerThread.start();
                } else {
                    mLog.i("mServerThread != null");
                    String curIp = mServerThread.getIp();
                    mLog.i("curIp = " + curIp);
                    String eIp = getEthernetIpAddress();
                    mLog.i("eIp = " + eIp);
                    String wIp = getWifiIpAddress();
                    mLog.i("wIp = " + wIp);
                    
                    if(eIp != null && wIp != null){ 
                        if(curIp.equals(eIp)){
                            return;
                        }else if(curIp.equals(wIp)){
                            mLog.i("mServerThread.getClientCount() = " + mServerThread.getClientCount());
                            if(mServerThread.getClientCount() == 0){
                                stopSeviceThread();
                                mServerThread = new ServerThread(eIp, PORT, null,this);
                                mServerThread.setColumn(mBookColumnId,mNewsPaperColumnId, mMagazineColumnId);
                                mServerThread.start();
                                return;
                            }
                        }
                        
                    }else if(eIp == null && wIp != null){
                        if(curIp.equals(wIp)){
                            return;
                        }else{
                            stopSeviceThread();
                            mServerThread = new ServerThread(wIp, PORT, null,this);
                            mServerThread.setColumn(mBookColumnId,mNewsPaperColumnId, mMagazineColumnId);
                            mServerThread.start();
                            return;
                        }
                        
                    }else if(eIp != null && wIp == null){
                        if(curIp.equals(eIp)){
                            return;
                        }else{
                            stopSeviceThread();
                            mServerThread = new ServerThread(eIp, PORT, null,this);
                            mServerThread.setColumn(mBookColumnId,mNewsPaperColumnId, mMagazineColumnId);
                            mServerThread.start();
                            return;
                        }
                    }else{
                        stopSeviceThread();
                    }
                }

            } else {
                stopSeviceThread();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void stopSeviceThread(){
        if(mServerThread != null){
            mServerThread.stopServer();
            mServerThread = null;
        }
            
    }
    private String getBestIpAddress(){
        String ip = getEthernetIpAddress();
        if(ip == null){
            ip = getWifiIpAddress();
        }
        return ip;
    }
    private String getWifiIpAddress(){
        String ip = null;
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if(wifiInfo.getSupplicantState() == SupplicantState.COMPLETED){
            ip = getAddress(wifiInfo.getIpAddress());
            mLog.i("getWifiIpAddress =" + ip);
        }
        return ip;
    }
    
    private String getEthernetIpAddress(){
        String ip = null;
        NetworkInfo ethernet = getEthernetInfo();
        if(ethernet.isConnected()){
            ip = getAddress(mEthManager.getDhcpInfo().ipAddress);
        }
        return ip; 
    }
    private String getAddress(int addr) {
        return NetworkUtils.intToInetAddress(addr).getHostAddress();
    }
    private boolean isNetworkConnected() {
        NetworkInfo wifiInfo = getWifiInfo();
        NetworkInfo ethernetInfo  = getEthernetInfo();
        boolean wifi = false;
        boolean eth = false;
        if(wifiInfo != null && wifiInfo.isConnected()){
            wifi = true;
        }
        
        if(ethernetInfo != null && ethernetInfo.isConnected()){
            eth = true;
        }
        
        if(wifi || eth){
            return true;
        }else{
            return false;
        }
        
    }

    private NetworkInfo getWifiInfo() {
        NetworkInfo wifiInfo = mConnectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo;
    }

    private NetworkInfo getEthernetInfo() {
        NetworkInfo ethernetInfo = mConnectManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        return ethernetInfo;
    }
    
    public String getIpAddress(){
        String ip = null;
        if(mServerThread == null || !mServerThread.isAlive()){
           initServerThread();
        }
        if(mServerThread != null){
            ip = mServerThread.getIp();
        }
        return ip;
    }
    public void setColumnId(String bookId,String newspaperId,String magazineId){
        this.mBookColumnId = bookId;
        this.mNewsPaperColumnId = newspaperId;
        this.mMagazineColumnId = magazineId;
        if(mServerThread != null)
            mServerThread.setColumn(mBookColumnId,mNewsPaperColumnId, mMagazineColumnId);
    }
//    private long loadTime; 
//    private List<LoadData> getBooks(){
//       long currentTime = System.currentTimeMillis();
//        if(mBookDatas == null || mBookDatas.isEmpty() || isNeedLoad()){
//            List<Book> books = ShelfController.getInstance(this).loadAllBooks();
//            loadTime = System.currentTimeMillis();
//            mBookDatas = new ArrayList<LoadData>();
//            if(books != null){
//                LoadData loadData;
//                for(int i = 0;i< books.size();i ++){
//                    Book  b = books.get(i);
//                    loadData = new LoadData();
//                    loadData.Name = b.Name;
//                    loadData.FilePath = b.Path;
//                    loadData.Date = "2013";
//                    File file = new File(loadData.FilePath);
//                    if(file.exists()){
//                        loadData.Size = StringUtil.formatFileSize(file.length());
//                    }
//                    mBookDatas.add(loadData);
//                }
//            }
//        }
//     return mBookDatas;
//    }
//    
//    public List<LoadData> getNewsPaper(){
//        if(mNewsPaperDatas == null || mNewsPaperDatas.isEmpty() || isNeedLoad()){
//            List<NewsPaper> papers = ShelfController.getInstance(this).loadAllNewsPapers();
//            loadTime = System.currentTimeMillis();
//            mNewsPaperDatas = new ArrayList<LoadData>();
//            if(papers != null){
//                LoadData loadData;
//                for(int i = 0;i< papers.size();i ++){
//                    NewsPaper  b = papers.get(i);
//                    loadData = new LoadData();
//                    loadData.Name = b.Name;
//                    loadData.FilePath = b.RootPath+".epub";
//                    loadData.Date = b.PublishTime;
//                    File file = new File(loadData.FilePath);
//                    if(file.exists()){
//                        loadData.Size = StringUtil.formatFileSize(file.length());
//                    }
//                    mNewsPaperDatas.add(loadData);
//                }
//            }
//        }
//        return mNewsPaperDatas;
//    }
//    
//    private boolean isNeedLoad(){
//        long currentTime = System.currentTimeMillis();
//        if((currentTime - loadTime) > 3600 * 1000){
//            return true;
//        }
//        return false;
//    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterConnectReceiver();
        stopSeviceThread();
    }
}
