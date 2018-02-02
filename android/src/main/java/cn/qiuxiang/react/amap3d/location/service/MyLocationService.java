package cn.qiuxiang.react.amap3d.location.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import cn.qiuxiang.react.amap3d.location.CommonLocation;
import cn.qiuxiang.react.amap3d.location.db.DBConfig;
import cn.qiuxiang.react.amap3d.location.db.DataBaseOpenHelper;
import cn.qiuxiang.react.amap3d.location.db.DataBaseOperateToken;
import cn.qiuxiang.react.amap3d.location.db.IDeleteCallback;
import cn.qiuxiang.react.amap3d.location.db.IQueryCallback;
import cn.qiuxiang.react.amap3d.location.db.ISingleInsertCallback;
import cn.qiuxiang.react.amap3d.location.utils.DateUtil;
import cn.qiuxiang.react.amap3d.location.utils.FileUtils;
import cn.qiuxiang.react.amap3d.location.websocket.WsManager;

/**
 * Created by duanzhenwei on 2017/11/24.
 */

public class MyLocationService extends Service implements AMapLocationListener {
    private static final String TAG = MyLocationService.class.getSimpleName();
    public static final String RECEIVER_ACTION = "location_in_background";
    public static final String ACTION_STOP_SERVICE = "action_stop_service";
    private AMapLocationClient mLocationClient;
    private String uid = "00000";
    private AMapLocationClientOption mLocationOption;
    private Object lock = new Object();

    private LocCache locCache = LocCache.Companion.getCache();

    //设定如果1分钟之内的点都没有精确度<100,那么强制上传一次
    private int INTERVAL_TIME = 30 * 1000;
    //记录上次成功上传的时间
    private long successTime;

    private int ACCURACY_THRESHOLD = 100;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!UploadThread.Companion.getThread().isAlive()) {
            UploadThread.Companion.getThread().start();
        }
        if (intent != null && intent.hasExtra("token")) {
            String token = intent.getStringExtra("token");
            FileUtils.putSharedPreferences(this, "token", token);
            uid = intent.getStringExtra("uid");
            FileUtils.putSharedPreferences(this, "uid", uid);
            if (locCache.isEmpty()) {
                queryLocations(uid);
            }
            WsManager.getInstance().init(token);
        } else {
            String token = (String) FileUtils.getSharedPreferences(this, "token", "0000");
            String uid = (String) FileUtils.getSharedPreferences(this, "token", "0000");
            if (locCache.isEmpty()) {
                queryLocations(uid);
            }
            WsManager.getInstance().init(token);
        }
        startLocation();
        //注册receiver，接收Activity发送的广播，停止线程，停止service
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_STOP_SERVICE);
        registerReceiver(broadcastReceiver, filter);
        return START_STICKY;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();//在service中停止service
            deleteUserLocations();
        }
    };


    private void deleteUserLocations() {
        String uid = (String) FileUtils.getSharedPreferences(this, "uid", "00000");
        DataBaseOpenHelper.getInstance().deleteValues(DataBaseOperateToken.TOKEN_DELETE_TABLE, DBConfig.TABLE_NAME, "uid = ?", new String[]{uid}, new IDeleteCallback() {
            @Override
            public void onDeleteComplete(int token, long result) {
                Log.e(MyLocationService.class.getSimpleName(), "删除用户数据");
            }

            @Override
            public void onAsyncOperateFailed() {

            }
        });
    }

    /**
     * 启动定位
     */
    void startLocation() {
        if (null != mLocationClient) {
            mLocationClient.stopLocation();
        }
        if (null == mLocationClient) {
            mLocationClient = new AMapLocationClient(this.getApplicationContext());
        }
        mLocationOption = new AMapLocationClientOption();
        // 使用连续
        mLocationOption.setOnceLocation(false);
        mLocationOption.setLocationCacheEnable(false);
        // 每10秒定位一次
        mLocationOption.setInterval(2000);
        // 地址信息
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(this);
        mLocationClient.startLocation();
        mLocationOption.setLocationCacheEnable(false);

    }

    /**
     * 停止定位
     */
    void stopLocation() {
        if (null != mLocationClient) {
            mLocationClient.stopLocation();
        }
        WsManager.getInstance().disconnect();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.t("轨迹上传").d(MyLocationService.class.getSimpleName(), "onCreate");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocation();
        unregisterReceiver(broadcastReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //暂时注释
        if (aMapLocation.getErrorCode() == 0) {
            if (aMapLocation.getAccuracy() < ACCURACY_THRESHOLD) {
                successTime = System.currentTimeMillis();
                insertDBLocation(aMapLocation, this);
                sendLocationBroadcast(aMapLocation);
            } else {
                if (System.currentTimeMillis() - successTime > INTERVAL_TIME) {
                    successTime = System.currentTimeMillis();
                    insertDBLocation(aMapLocation, this);
                    sendLocationBroadcast(aMapLocation);
                }
            }
        }
    }

    private void sendLocationBroadcast(AMapLocation aMapLocation) {
        Intent mIntent = new Intent(RECEIVER_ACTION);
        mIntent.putExtra("result", aMapLocation);
        //发送广播
        sendBroadcast(mIntent);
    }

    private void insertDBLocation(AMapLocation location, Context context) {
        final CommonLocation commonLocation = CommonLocation.assemblyLocation(location);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConfig.location.locTime, System.currentTimeMillis() / 1000);
        contentValues.put(DBConfig.location.locLatitude, commonLocation.getLatitude());
        contentValues.put(DBConfig.location.locLongitude, commonLocation.getLongitude());
        contentValues.put(DBConfig.location.speed, commonLocation.getSpeed());
        contentValues.put(DBConfig.location.accuracy, commonLocation.getAccuracy());
        contentValues.put(DBConfig.location.distance, commonLocation.getDistance());
        contentValues.put(DBConfig.location.isHasSend, 0);
        try {
            contentValues.put(DBConfig.location.uid, Integer.parseInt(uid));
            Log.e(TAG, "uid==" + uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!UploadThread.Companion.getThread().isAlive()) {
            UploadThread.Companion.getThread().start();
        } else {
           // Logger.t("轨迹上传").d("active" + UploadThread.Companion.getThread().getName());
        }

        DataBaseOpenHelper.getInstance().insertSingleValues(DataBaseOperateToken.TOKEN_INSERT_SINGLE_INFO, DBConfig.TABLE_NAME, null, contentValues, new ISingleInsertCallback() {
            @Override
            public void onSingleInsertComplete(int token, long result, int id) {
              /*  sendData(commonLocation);
                queryLocations(uid);*/
                commonLocation.setId(id);
                locCache.addElement(commonLocation);
            }

            @Override
            public void onAsyncOperateFailed() {
            }
        });
    }

    private void queryLocations(String uid) {
        long todayZero = DateUtil.getTodayZero();
        DataBaseOpenHelper.getInstance().queryValues(DataBaseOperateToken.TOKEN_QUERY_TABLE, false, DBConfig.TABLE_NAME, null, "isHasSend = ? and uid = ? and locTime < ?", new String[]{String.valueOf(0), uid, String.valueOf(todayZero)}, null, null, "locTime", null, new IQueryCallback() {
            @Override
            public void onQueryComplete(int token, Cursor cursor) {
                getAllInfo(cursor);
            }

            @Override
            public void onAsyncOperateFailed() {
               /* synchronized (lock) {
                    lock.notify();
                }*/
            }
        });
    }

    private void getAllInfo(Cursor cursor) {
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CommonLocation commonLocation = CommonLocation.queryLocationItem(cursor);
                LocCache.Companion.getCache().addElement(commonLocation);
            }
            cursor.close();
        }
       /* synchronized (lock) {
            lock.notify();
        }*/
    }

}
