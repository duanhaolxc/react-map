package cn.qiuxiang.react.amap3d.location.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.qiuxiang.react.amap3d.location.utils.FileUtils;

/**
 * Created by jianglei on 2016/4/6.
 */
public class DataBase extends SQLiteOpenHelper {
    public DataBase(Context context, int version_code) {
        super(context, FileUtils.getMyDatabaseName(DBConfig.DBNAME), null, version_code);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBConfig.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
