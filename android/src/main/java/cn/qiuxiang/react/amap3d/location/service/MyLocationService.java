package cn.qiuxiang.react.amap3d.location.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.orhanobut.logger.Logger;

import cn.qiuxiang.react.amap3d.location.db.DBConfig;
import cn.qiuxiang.react.amap3d.location.db.DataBaseOpenHelper;
import cn.qiuxiang.react.amap3d.location.db.DataBaseOperateToken;
import cn.qiuxiang.react.amap3d.location.db.IDeleteCallback;
import cn.qiuxiang.react.amap3d.location.utils.FileUtils;
import cn.qiuxiang.react.amap3d.location.websocket.WsManager;

/**
 * Created by duanzhenwei on 2017/11/24.
 */

public class MyLocationService extends Service implements AMapLocationListener {
    public static final String RECEIVER_ACTION = "location_in_background";
    public static final String ACTION_STOP_SERVICE = "action_stop_service";
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String token = (String) FileUtils.getSharedPreferences(this, "token", "token");
        WsManager.getInstance().init(token);
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
        if (aMapLocation.getErrorCode() == 0&&aMapLocation.getAccuracy()<100) {
            sendLocationBroadcast(aMapLocation);
        }
    }

    private void sendLocationBroadcast(AMapLocation aMapLocation) {
        Intent mIntent = new Intent(RECEIVER_ACTION);
        mIntent.putExtra("result", aMapLocation);
        //发送广播
        sendBroadcast(mIntent);
    }


}
