package cn.qiuxiang.react.amap3d.location.db;

/**
 * Created by duanzhenwei on 2017/11/24.
 */

public class DBConfig {
    public static final String DBNAME = "engine_location.db";
    private static final String CREATE = "CREATE TABLE IF NOT EXISTS ";
    public static final String TABLE_NAME = "location_table";
    public static final String CREATE_TABLE = CREATE + TABLE_NAME + "(ID integer PRIMARY KEY AUTOINCREMENT, " +
            "locTime REAL,locLatitude REAL,locLongitude REAL,speed REAL,accuracy REAL,distance REAL,isHasSend integer,uid integer)";

    //用于备份未上传的附表
    public static final String TABLE_NAME_UPLOAD = "location_table_upload";
    public static final String CREATE_TABLE_UPLOAD = CREATE + TABLE_NAME_UPLOAD + "(ID integer PRIMARY KEY AUTOINCREMENT, " +
            "locTime REAL,locLatitude REAL,locLongitude REAL,speed REAL,accuracy REAL,distance REAL,isHasSend integer,uid integer)";


    public static final class location {
        public static final String id = "ID";
        public static final String locTime = "locTime"; // 定位时间 <long> 用于操作数据库
        public static final String locLatitude = "locLatitude"; // 纬度信息 <double>
        public static final String locLongitude = "locLongitude"; // 经度信息 <double>
        public static final String speed = "speed"; // GPS位置中：当前速度，单位：公里每小时 <float>
        public static final String accuracy = "accuracy";
        public static final String distance = "distance";
        public static final String isHasSend = "isHasSend";
        public static final String uid = "uid";
    }
}
