package cn.qiuxiang.react.amap3d.location;

import android.database.Cursor;

import com.amap.api.location.AMapLocation;

import java.io.Serializable;

import cn.qiuxiang.react.amap3d.location.db.DBConfig;

public class CommonLocation implements Serializable {
    private int id;
    private double accuracy;
    private double speed;
    private double latitude;
    private double longitude;
    private long time;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    private float distance;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    /**
     * 拼装 CommonLocation
     *
     * @param bdLocation
     * @return
     */
    public static CommonLocation assemblyLocation(AMapLocation bdLocation) {
        CommonLocation location = new CommonLocation();
        location.setLatitude(bdLocation.getLatitude());
        location.setLongitude(bdLocation.getLongitude());
        location.setSpeed(bdLocation.getSpeed());
        location.setTime(bdLocation.getTime());
        location.setAccuracy(bdLocation.getAccuracy());
        return location;
    }
    /**
     * 解析查询得到的 Cursor
     *
     * @param cursor
     * @return
     */
    public static CommonLocation queryLocationItem(Cursor cursor) {
        CommonLocation hooweLocation = new CommonLocation();
        hooweLocation.setTime(cursor.getLong(cursor.getColumnIndex(DBConfig.location.locTime)));
        hooweLocation.setLatitude(cursor.getDouble(cursor.getColumnIndex(DBConfig.location.locLatitude)));
        hooweLocation.setLongitude(cursor.getDouble(cursor.getColumnIndex(DBConfig.location.locLongitude)));
        hooweLocation.setSpeed(cursor.getFloat(cursor.getColumnIndex(DBConfig.location.speed)));
        hooweLocation.setDistance(cursor.getFloat(cursor.getColumnIndex(DBConfig.location.distance)));
        hooweLocation.setId(cursor.getInt(cursor.getColumnIndex(DBConfig.location.id)));
        return hooweLocation;
    }
    @Override
    public String toString() {
        return "CommonLocation{" +
                "accuracy=" + accuracy +
                ", speed=" + speed +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", time=" + time +
                ", distance=" + distance +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
