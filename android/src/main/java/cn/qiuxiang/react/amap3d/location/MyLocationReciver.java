package cn.qiuxiang.react.amap3d.location;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.orhanobut.logger.Logger;

import java.util.HashMap;

import cn.qiuxiang.react.amap3d.location.db.DBConfig;
import cn.qiuxiang.react.amap3d.location.db.DataBaseOpenHelper;
import cn.qiuxiang.react.amap3d.location.db.DataBaseOperateToken;
import cn.qiuxiang.react.amap3d.location.db.IQueryCallback;
import cn.qiuxiang.react.amap3d.location.db.ISingleInsertCallback;
import cn.qiuxiang.react.amap3d.location.utils.DateUtil;
import cn.qiuxiang.react.amap3d.location.utils.FileUtils;
import cn.qiuxiang.react.amap3d.location.websocket.WsManager;

/**
 * Created by duanzhenwei on 2017/11/24.
 */

public class MyLocationReciver extends BroadcastReceiver {
    private static final String TAG = "MyLocationReciver";
    private static final long todayZero = DateUtil.getTodayZero();

    @Override
    public void onReceive(Context context, Intent intent) {
        AMapLocation location = intent.getParcelableExtra("result");
        insertDBLocation(location,context);
    }

    private void insertDBLocation(AMapLocation location,Context context) {
        final String uid = (String) FileUtils.getSharedPreferences(context, "uid", "00000");
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
