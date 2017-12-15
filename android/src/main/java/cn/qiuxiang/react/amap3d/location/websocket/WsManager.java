package cn.qiuxiang.react.amap3d.location.websocket;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.TextUtils;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.Logger;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cn.qiuxiang.react.amap3d.location.db.DBConfig;
import cn.qiuxiang.react.amap3d.location.db.DataBaseOpenHelper;
import cn.qiuxiang.react.amap3d.location.db.DataBaseOperateToken;
import cn.qiuxiang.react.amap3d.location.db.IUpdateCallback;

/**
 * Created by MSI on 2017/6/8.
 */

public class WsManager {
    private static WsManager mInstance;
    private final String TAG = this.getClass().getSimpleName();
    /**
     * WebSocket config
     */
    private static final int FRAME_QUEUE_SIZE = 5;
    private static final int CONNECT_TIMEOUT = 5000;
    private static final String DEF_TEST_URL = "ws://trace-pharos.ofo.com/ws?token=";//测试服默认地址
    private static final String DEF_RELEASE_URL = "ws://trace-pharos.ofo.com/ws?token=";//正式服默认地址
    private static final String DEF_URL = BuildConfig.DEBUG ? DEF_TEST_URL : DEF_TEST_URL;
    private String url;
    private WsStatus mStatus;
    private WebSocket ws;
    private WsListener mListener;
    private static Context sContext;

    private WsManager() {
    }

    public static void init(Context ctx) {
        sContext = ctx;
        getInstance();
    }

    public static WsManager getInstance() {
        if (mInstance == null) {
            synchronized (WsManager.class) {
                if (mInstance == null) {
                    mInstance = new WsManager();
                }
            }
        }
        return mInstance;
    }


    public void init(String token) {
        try {
            /**
             * configUrl其实是缓存在本地的连接地址
             * 这个缓存本地连接地址是app启动的时候通过http请求去服务端获取的,
             * 每次app启动的时候会拿当前时间与缓存时间比较,超过6小时就再次去服务端获取新的连接地址更新本地缓存
             */
            String configUrl = "";
            url = TextUtils.isEmpty(configUrl) ? DEF_URL + token : configUrl;
            ws = new WebSocketFactory().createSocket(url, CONNECT_TIMEOUT)
                    .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                    .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                    .addListener(mListener = new WsListener())//添加回调监听
                    .connectAsynchronously();//异步连接
            setStatus(WsStatus.CONNECTING);
            Logger.t(TAG).d("第一次连接");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(HashMap<String, Object> dict) {
        try {
            JSONObject obj = new JSONObject(dict);
            if (ws != null) {
                ws.sendText(obj.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 继承默认的监听空实现WebSocketAdapter,重写我们需要的方法
     * onTextMessage 收到文字信息
     * onConnected 连接成功
     * onConnectError 连接失败
     * onDisconnected 连接关闭
     */
    class WsListener extends WebSocketAdapter {
        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            Logger.t(TAG).d(text);
            updateDBLocation(text);
        }

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers)
                throws Exception {
            super.onConnected(websocket, headers);
            Logger.t(TAG).d("连接成功");
            setStatus(WsStatus.CONNECT_SUCCESS);
            cancelReconnect();//连接成功的时候取消重连,初始化连接次数
        }


        @Override
        public void onConnectError(WebSocket websocket, WebSocketException exception)
                throws Exception {
            super.onConnectError(websocket, exception);
            Logger.t(TAG).d("连接错误");
            setStatus(WsStatus.CONNECT_FAIL);
            reconnect();//连接错误的时候调用重连方法
        }


        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer)
                throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            Logger.t(TAG).d("断开连接");
            setStatus(WsStatus.CONNECT_FAIL);
            reconnect();//连接断开的时候调用重连方法
        }
    }


    private void setStatus(WsStatus status) {
        this.mStatus = status;
    }


    public WsStatus getStatus() {
        return mStatus;
    }


    public void disconnect() {
        if (ws != null) {
            ws.disconnect();
        }
    }


    private Handler mHandler = new Handler();

    private int reconnectCount = 0;//重连次数
    private long minInterval = 3000;//重连最小时间间隔
    private long maxInterval = 60000;//重连最大时间间隔


    public void reconnect() {
        if (!isNetConnect()) {
            reconnectCount = 0;
            Logger.t(TAG).d("重连失败网络不可用");
            return;
        }

        //这里其实应该还有个用户是否登录了的判断 因为当连接成功后我们需要发送用户信息到服务端进行校验
        //由于我们这里是个demo所以省略了
        if (ws != null &&
                !ws.isOpen() &&//当前连接断开了
                getStatus() != WsStatus.CONNECTING) {//不是正在重连状态
            reconnectCount++;
            setStatus(WsStatus.CONNECTING);

            long reconnectTime = minInterval;
            if (reconnectCount > 3) {
                url = DEF_URL;
                long temp = minInterval * (reconnectCount - 2);
                reconnectTime = temp > maxInterval ? maxInterval : temp;
            }

            Logger.t(TAG).d("准备开始第%d次重连,重连间隔%d -- url:%s", reconnectCount, reconnectTime, url);
            mHandler.postDelayed(mReconnectTask, reconnectTime);
        }
    }


    private Runnable mReconnectTask = new Runnable() {

        @Override
        public void run() {
            try {
                ws = new WebSocketFactory().createSocket(url, CONNECT_TIMEOUT)
                        .setFrameQueueSize(FRAME_QUEUE_SIZE)//设置帧队列最大值为5
                        .setMissingCloseFrameAllowed(false)//设置不允许服务端关闭连接却未发送关闭帧
                        .addListener(mListener = new WsListener())//添加回调监听
                        .connectAsynchronously();//异步连接
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    private void cancelReconnect() {
        reconnectCount = 0;
        mHandler.removeCallbacks(mReconnectTask);
    }


    private boolean isNetConnect() {
        ConnectivityManager connectivity = (ConnectivityManager) sContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    private void updateDBLocation(String stautsInfo) {
        if (stautsInfo.contains("ACK")) {
            String id = stautsInfo.split("/")[1];
            ContentValues values4 = new ContentValues();
            values4.put("isHasSend", 1);
            DataBaseOpenHelper.getInstance().updateValues(DataBaseOperateToken.TOKEN_UPDATE_CURRENT_INFO, DBConfig.TABLE_NAME, values4, "ID=?", new String[]{id}, new IUpdateCallback() {
                @Override
                public void onUpdateComplete(int token, long result) {
                    Logger.e(TAG, "更新成功");
                }

                @Override
                public void onAsyncOperateFailed() {
                    Logger.e(TAG, "更新失败");
                }
            });
        }
    }
}
