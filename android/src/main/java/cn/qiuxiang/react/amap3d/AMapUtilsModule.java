package cn.qiuxiang.react.amap3d;

import android.telecom.Call;
import android.util.Log;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by xinzhongzhu on 2017/8/30.
 */

public class AMapUtilsModule extends ReactContextBaseJavaModule implements OnGeocodeSearchListener {

    private ReactApplicationContext context = null;
    private GeocodeSearch geocoderSearch;
    private Callback geoMessageCallback;

    public AMapUtilsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    @Override
    public String getName() {
        return "LocationManager";
    }

    @ReactMethod
    public void geoMessage(float latitude, float longitude, final Callback successCallback) {
        geoMessageCallback = successCallback;
        geocoderSearch = new GeocodeSearch(context);
        geocoderSearch.setOnGeocodeSearchListener(this);
        geocoderSearch.getFromLocationAsyn(
                new RegeocodeQuery(new LatLonPoint(latitude, longitude), 200, GeocodeSearch.AMAP)
        );
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        //解析result获取地址描述信息
        if (rCode == 1000) {
            geoMessageCallback.invoke(false, result.getRegeocodeAddress().getFormatAddress());
        } else {
            geoMessageCallback.invoke(true);
        }
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        //解析result获取地址描述信息
    }

    @ReactMethod
    public void whereami(final Promise promise) {

        AMapLocationListener locationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation loc) {
                if (null != loc) {
                    WritableMap params = Arguments.createMap();
                    params.putInt("status", 0);
                    params.putDouble("latitude", loc.getLatitude());
                    params.putDouble("longitude", loc.getLongitude());
                    params.putDouble("accuracy", loc.getAccuracy());
                    params.putDouble("speed", loc.getSpeed());
                    //解析定位结果
                    promise.resolve(params);

                } else {
                    WritableMap params = Arguments.createMap();
                    params.putInt("status", 1);
                    promise.resolve(params);
                }
            }
        };

        AMapLocationClient locationClient = new AMapLocationClient(context);
        locationClient.setLocationListener(locationListener);
        locationClient.setLocationOption(getDefaultOption());
        locationClient.startLocation();
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(false);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(true);//可选，设置是否使用传感器。默认是false
        return mOption;
    }

    @ReactMethod
    public void distance(ReadableMap fromPoint, ReadableMap toPoint, Promise promise) {
        WritableMap params = Arguments.createMap();
        params.putDouble("result",
                AMapUtils.calculateLineDistance(
                        new LatLng(fromPoint.getDouble("latitude"), fromPoint.getDouble("longitude")),
                        new LatLng(toPoint.getDouble("latitude"), toPoint.getDouble("longitude"))
                ));
        promise.resolve(params);
    }
}
