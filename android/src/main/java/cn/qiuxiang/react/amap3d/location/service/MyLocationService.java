package cn.qiuxiang.react.amap3d.location.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.orhanobut.logger.Logger;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra("token")) {
            String token = intent.getStringExtra("token");
            uid = intent.getStringExtra("uid");
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
        Logger.e(MyLocationService.class.getSimpleName(), "onCreate");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.e("MyLocationService", "onDestroy");
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
        if (aMapLocation.getErrorCode() == 0 && aMapLocation.getAccuracy() < 100) {
            insertDBLocation(aMapLocation, this);
            sendLocationBroadcast(aMapLocation);
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

        DataBaseOpenHelper.getInstance().insertSingleValues(DataBaseOperateToken.TOKEN_INSERT_SINGLE_INFO, DBConfig.TABLE_NAME, null, contentValues, new ISingleInsertCallback() {
            @Override
            public void onSingleInsertComplete(int token, long result) {
                Logger.e(TAG, "插入成功:" + "token=" + token);
                sendData(commonLocation);
                queryLocations(uid);
            }

            @Override
            public void onAsyncOperateFailed() {
                Logger.e(TAG, "插入失败");
            }
        });
    }

    private void queryLocations(String uid) {
        long todayZero = DateUtil.getTodayZero();
        DataBaseOpenHelper.getInstance().queryValues(DataBaseOperateToken.TOKEN_QUERY_TABLE, false, DBConfig.TABLE_NAME, null, "isHasSend = ? and uid = ? and locTime < ?", new String[]{String.valueOf(0), uid, String.valueOf(todayZero)}, null, null, "locTime", null, new IQueryCallback() {
            @Override
            public void onQueryComplete(int token, Cursor cursor) {
                Logger.e(TAG, "查询成功:" + "token=" + token);
                getAllInfo(cursor);
            }

            @Override
            public void onAsyncOperateFailed() {
                Logger.e(TAG, "查询失败");
            }
        });
    }

    private void getAllInfo(Cursor cursor) {
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CommonLocation commonLocation = CommonLocation.queryLocationItem(cursor);
                sendData(commonLocation);
            }
            cursor.close();
        }
    }

    private void sendData(CommonLocation loc) {
        HashMap<String, Object> dict = new HashMap<>();
        dict.put("id", loc.getId());
        dict.put("platform", "android");
        dict.put("lat", loc.getLatitude());
        dict.put("lng", loc.getLongitude());
        dict.put("speed", loc.getSpeed());
        dict.put("accuracy", loc.getAccuracy());
        dict.put("timestamp", loc.getTime());
        WsManager.getInstance().send(dict);

    }

}
