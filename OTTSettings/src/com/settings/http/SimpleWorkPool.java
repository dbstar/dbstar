package com.settings.http;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

import android.os.Handler;

import com.settings.http.HttpConnect.HttpConnectInstance;

public class SimpleWorkPool {

    private ExecutorService mPool;

    public static final int POOL_TYPE_SINGLE = 0;
    public static final int POOL_TYPE_CACHE = -1;

    private SimpleWorkPool() {
        initPool(2);
    }

    private void initPool(int count) {
        switch (count) {
        case POOL_TYPE_SINGLE:
            mPool = Executors.newSingleThreadExecutor();
            break;
        case POOL_TYPE_CACHE:
            mPool = Executors.newCachedThreadPool();
            break;
        default:
            if (count < -1 || count > 10) {
                mPool = Executors.newCachedThreadPool();
            } else {
                mPool = Executors.newFixedThreadPool(count);
            }
            break;
        }
    }

    public static class SimpleWorkPoolInstance {
        private static SimpleWorkPool mInstance = new SimpleWorkPool();

        public static SimpleWorkPool instance() {
            return mInstance;
        }
    }

    public void execute(Work work) {
        mPool.execute(new WorkRunable(work));
    }

    public void shutdown() {
        if (!mPool.isShutdown()) {
            mPool.shutdown();
        }
    }

    class WorkRunable implements Runnable {
        private Work mWork;

        public WorkRunable(Work work) {
            this.mWork = work;
        }

        @Override
        public void run() {
            mWork.execute();
        }
    }

    public static interface Work {
        void execute();
    }

    public static abstract class ConnectWork<Result> implements Work {

        private Result mResult;
        private String mUri;
        private int mType;
        private List<NameValuePair> mParams;

        public ConnectWork(int type, String uri, List<NameValuePair> params) {
            this.mType = type;
            this.mUri = uri;
            this.mParams = params;
        }

        private Handler handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                connectComplete(mResult);
            };
        };

        @Override
        public final void execute() {
            mResult = connect();
            handler.sendEmptyMessage(0);
        }

        private Result connect() {
            return processResult(HttpConnectInstance.instance().openConnect(this.mType, this.mUri, this.mParams));
        }

        // 抽象方法，需要子类实现，在这个方法里，处理联网获取到的HttpEntity
        public abstract Result processResult(HttpEntity entity);

        // 抽象方法，需要子类实现，在这个方法里实现与主线程的交互。
        public abstract void connectComplete(Result result);

    }
    
//    public static abstract class ReadSDCardData<Result> implements Work {
//    	
//    	private Result mResult;
//    	private String mFilePath;
//    	
//    	public ReadSDCardData(String filePath) {
//    		this.mFilePath = filePath;
//    	}
//    	
//    	private Handler handler = new Handler() {
//    		public void handleMessage(android.os.Message msg) {
//    			connectComplete(mResult);
//    		};
//    	};
//    	
//    	@Override
//        public final void execute() {
//            mResult = connect();
//            handler.sendEmptyMessage(0);
//        }
//
//        private Result connect() {
//        	return processResult(DbstarUtil.readQueryPosterFromSDcard(this.mFilePath));
//        }
//
//        // 抽象方法，需要子类实现，在这个方法里，处理从sd中读取的数据
//        public abstract Result processResult(HashMap<String, String> hashMap);
//    	
//    	// 抽象方法，需要子类实现，在这个方法里实现与主线程的交互。
//        public abstract void connectComplete(Result result);
//    }

}

