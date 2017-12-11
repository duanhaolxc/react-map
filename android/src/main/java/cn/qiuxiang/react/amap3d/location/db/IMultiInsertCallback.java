package cn.qiuxiang.react.amap3d.location.db;

/**
 * Created by jianglei on 2016/4/7.
 */
public interface IMultiInsertCallback extends IAsyncHandlerCallback {

    /**
     * 多条插入成功
     */
    void onMultiInsertComplete(int token, long result);
}
