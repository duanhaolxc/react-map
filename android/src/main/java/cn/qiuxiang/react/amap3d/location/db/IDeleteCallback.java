package cn.qiuxiang.react.amap3d.location.db;

/**
 * Created by jianglei on 2016/4/7.
 */
public interface IDeleteCallback extends IAsyncHandlerCallback {

    /**
     * 删除成功
     */
    void onDeleteComplete(int token, long result);
}
