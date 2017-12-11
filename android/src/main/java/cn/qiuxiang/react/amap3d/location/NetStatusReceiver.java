package cn.qiuxiang.react.amap3d.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.orhanobut.logger.Logger;

import cn.qiuxiang.react.amap3d.location.websocket.WsManager;

/**
 * Created by MSI on 2017/6/9.
 */

public class NetStatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            // 获取网络连接管理器
            ConnectivityManager connectivityManager
                = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
            // 获取当前网络状态信息
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();

            if (info != null && info.isAvailable()) {
                Logger.t("WsManager").d("监听到可用网络切换,调用重连方法");
                WsManager.getInstance().reconnect();//wify 4g切换重连websocket
            }

        }
    }
}
