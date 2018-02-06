package cn.qiuxiang.react.amap3d.maps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import cn.qiuxiang.react.amap3d.R
import cn.qiuxiang.react.amap3d.location.db.DBConfig
import cn.qiuxiang.react.amap3d.location.db.DataBaseOpenHelper
import cn.qiuxiang.react.amap3d.location.db.DataBaseOperateToken
import cn.qiuxiang.react.amap3d.location.db.IQueryCallback
import cn.qiuxiang.react.amap3d.location.utils.DateUtil
import cn.qiuxiang.react.amap3d.location.utils.FileUtils
import cn.qiuxiang.react.amap3d.utils.LatLongBean
import cn.qiuxiang.react.amap3d.utils.PathSmooth
import cn.qiuxiang.react.amap3d.utils.WalkUtil
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.TextureMapView
import com.amap.api.maps.model.*
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.events.RCTEventEmitter
import java.util.ArrayList
import kotlin.collections.HashMap

class AMapView(context: Context) : TextureMapView(context), LocationSource, AMapLocationListener {
    private val eventEmitter: RCTEventEmitter = (context as ThemedReactContext).getJSModule(RCTEventEmitter::class.java)
    private val markers = HashMap<String, AMapMarker>()
    private val polyline = HashMap<String, AMapPolyline>()
    private val polygons = HashMap<String, AMapPolygon>()
    private val circles = HashMap<String, AMapCircle>()
    private val heatTitles = HashMap<String, AMapHeatTitle>()
    private var mLocationListener: LocationSource.OnLocationChangedListener? = null
    private var locationClient: AMapLocationClient? = null
    private var locationOption: AMapLocationClientOption? = null
    private val mLocationList = ArrayList<LatLongBean>()
    private var mIsFirstLocation = true
    private var isTracking: Boolean = false
    private var mMarkMyLocation: Marker? = null
    private var mMarkStartLocation: Marker? = null
    private var locationReceiver: BroadcastReceiver? = null
    private val locationStyle by lazy {
        val locationStyle = MyLocationStyle()
        //		 自定义系统定位小蓝点
        val iv = ImageView(context)
        val fmIv = FrameLayout.LayoutParams(1, 1)
        iv.setImageResource(R.drawable.location_icon)
        iv.layoutParams = fmIv
        val markerIcon = BitmapDescriptorFactory.fromView(iv)
        locationStyle.myLocationIcon(markerIcon)// 设置小蓝点的图标
        locationStyle.strokeColor(Color.argb(0, 0, 0, 0))// 设置圆形的边框颜色
        locationStyle.radiusFillColor(Color.argb(0, 0, 0, 0))// 设置圆形的填充颜色
        locationStyle.strokeWidth(0f)// 设置圆形的边框粗细
        locationStyle.anchor(0.5f, 0.9f)
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        locationStyle
    }

    private fun initLocation() {
        map.setLocationSource(this)// 设置定位监听
        map.isMyLocationEnabled = true
        locationClient = AMapLocationClient(context)
        locationOption = AMapLocationClientOption()
        // 设置定位模式为高精度模式
        locationOption!!.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置是否返回地址信息（默认返回地址信息）
        locationOption!!.isNeedAddress = true
        // 设置定位监听
        locationClient!!.setLocationListener(this)
        locationOption!!.isOnceLocation = true
        // 设置定位参数
        locationClient!!.setLocationOption(locationOption)
        // 启动定位
        locationClient!!.startLocation()
        map.myLocationStyle = locationStyle

    }


    init {
        super.onCreate(null)
        initLocation()
        map.setOnMapClickListener { latLng ->
            for (marker in markers.values) {
                marker.active = false
            }
            val event = Arguments.createMap()
            event.putDouble("latitude", latLng.latitude)
            event.putDouble("longitude", latLng.longitude)
            emit(id, "onPress", event)
        }
        map.setLocationSource(this)
        map.setOnMapLongClickListener { latLng ->
            val event = Arguments.createMap()
            event.putDouble("latitude", latLng.latitude)
            event.putDouble("longitude", latLng.longitude)
            emit(id, "onLongPress", event)
        }

        map.setOnMyLocationChangeListener { location ->
            val event = Arguments.createMap()
            event.putDouble("latitude", location.latitude)
            event.putDouble("longitude", location.longitude)
            event.putDouble("accuracy", location.accuracy.toDouble())
            emit(id, "onLocation", event)
        }
        map.setOnMarkerClickListener { marker ->
            emit(markers[marker.id]?.id, "onPress")
            true
        }

        map.setOnMarkerDragListener(object : AMap.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
                emit(markers[marker.id]?.id, "onDragStart")
            }

            override fun onMarkerDrag(marker: Marker) {
                emit(markers[marker.id]?.id, "onDrag")
            }

            override fun onMarkerDragEnd(marker: Marker) {
                val position = marker.position
                val data = Arguments.createMap()
                data.putDouble("latitude", position.latitude)
                data.putDouble("longitude", position.longitude)
                emit(markers[marker.id]?.id, "onDragEnd", data)
            }
        })

        map.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {
            override fun onCameraChangeFinish(position: CameraPosition?) {
                emitCameraChangeEvent("onStatusChangeComplete", position)
            }

            override fun onCameraChange(position: CameraPosition?) {
                emitCameraChangeEvent("onStatusChange", position)
            }
        })

        map.setOnInfoWindowClickListener { marker ->
            emit(markers[marker.id]?.id, "onInfoWindowPress")
        }

        map.setOnPolylineClickListener { polyline ->
            emit(this.polyline[polyline.id]?.id, "onPress")
        }
        map.setInfoWindowAdapter(AMapInfoWindowAdapter(context, markers))
    }

    fun emitCameraChangeEvent(event: String, position: CameraPosition?) {
        position?.let {
            val data = Arguments.createMap()
            data.putDouble("zoomLevel", it.zoom.toDouble())
            data.putDouble("tilt", it.tilt.toDouble())
            data.putDouble("rotation", it.bearing.toDouble())
            data.putDouble("latitude", it.target.latitude)
            data.putDouble("longitude", it.target.longitude)
            if (event == "onStatusChangeComplete") {
                val southwest = map.projection.visibleRegion.latLngBounds.southwest
                val northeast = map.projection.visibleRegion.latLngBounds.northeast
                data.putDouble("latitudeDelta", Math.abs(southwest.latitude - northeast.latitude))
                data.putDouble("longitudeDelta", Math.abs(southwest.longitude - northeast.longitude))
            }
            emit(id, event, data)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        //注册receiver，接收Activity发送的广播，停止线程，停止service
        val filter = IntentFilter()
        filter.addAction("location_in_background")
        locationReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val location = intent.getParcelableExtra<AMapLocation>("result")
                onMyLocationChanged(location)
            }
        }
        context.registerReceiver(locationReceiver, filter)
    }

    fun addMarker(marker: AMapMarker) {
        marker.addToMap(map)
        markers.put(marker.marker?.id!!, marker)
    }

    fun addPolyline(polyline: AMapPolyline) {
        polyline.addToMap(map)
        this.polyline.put(polyline.polyline?.id!!, polyline)
    }

    fun addHeatTitle(heatTitle: AMapHeatTitle) {
        heatTitle.addToMap(map)
        this.heatTitles.put(heatTitle.tileOverlay?.id!!, heatTitle)
    }

    fun addPolygon(polygon: AMapPolygon) {
        polygon.addToMap(map)
        polygons.put(polygon.polygon?.id!!, polygon)
    }

    fun addCircle(circle: AMapCircle) {
        circle.addToMap(map)
        circles.put(circle.circle?.id!!, circle)
    }

    fun emit(id: Int?, name: String, data: WritableMap = Arguments.createMap()) {
        id?.let { eventEmitter.receiveEvent(it, name, data) }
    }

    fun remove(child: View) {
        when (child) {
            is AMapMarker -> {
                markers.remove(child.marker?.id)
                child.marker?.destroy()
            }
            is AMapHeatTitle -> {
                child.tileOverlay?.remove()
            }
            is AMapPolyline -> {
                polyline.remove(child.polyline?.id)
                child.polyline?.remove()
            }
            is AMapPolygon -> {
                polygons.remove(child.polygon?.id)
                child.polygon?.remove()
            }
            is AMapCircle -> {
                polygons.remove(child.circle?.id)
                child.circle?.remove()
            }
        }
    }

    private val animateCallback = object : AMap.CancelableCallback {
        override fun onCancel() {
            emit(id, "onAnimateCancel")
        }

        override fun onFinish() {
            emit(id, "onAnimateFinish")
        }
    }

    fun animateTo(args: ReadableArray?) {
        val currentCameraPosition = map.cameraPosition
        val target = args?.getMap(0)!!
        val duration = args.getInt(1)

        var coordinate = currentCameraPosition.target
        var zoomLevel = currentCameraPosition.zoom
        var tilt = currentCameraPosition.tilt
        var rotation = currentCameraPosition.bearing

        if (target.hasKey("coordinate")) {
            val json = target.getMap("coordinate")
            coordinate = LatLng(json.getDouble("latitude"), json.getDouble("longitude"))
        }
        if (target.hasKey("zoomLevel")) {
            zoomLevel = target.getDouble("zoomLevel").toFloat()
        }

        if (target.hasKey("tilt")) {
            tilt = target.getDouble("tilt").toFloat()
        }

        if (target.hasKey("rotation")) {
            rotation = target.getDouble("rotation").toFloat()
        }

        val cameraUpdate = CameraUpdateFactory.newCameraPosition(
                CameraPosition(coordinate, zoomLevel, tilt, rotation))
        map.animateCamera(cameraUpdate, duration.toLong(), animateCallback)
    }

    fun setLimitRegion(limitRegion: ReadableMap) {
        val latitude = limitRegion.getDouble("latitude")
        val longitude = limitRegion.getDouble("longitude")
        val latitudeDelta = limitRegion.getDouble("latitudeDelta")
        val longitudeDelta = limitRegion.getDouble("longitudeDelta")
        map.setMapStatusLimits(LatLngBounds(
                LatLng(latitude - latitudeDelta / 2, longitude - longitudeDelta / 2),
                LatLng(latitude + latitudeDelta / 2, longitude + longitudeDelta / 2)
        ))
    }

    fun setCoordinates(coordinates: ReadableArray) {
        mLocationList.addAll(ArrayList((0 until coordinates.size())
                .map { coordinates.getMap(it) }
                .map {
                    LatLongBean(LatLng(it.getDouble("latitude"), it.getDouble("longitude")), if (it.hasKey("speed")) it.getDouble("speed").toFloat() else 0.0f)
                }))

    }

    override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener) {
        mLocationListener = onLocationChangedListener
    }

    override fun deactivate() {
        mLocationListener = null
    }

    override fun onLocationChanged(aMapLocation: AMapLocation?) {
        if (aMapLocation != null && aMapLocation.errorCode == 0) {
            if (mLocationListener != null) {
                mLocationListener!!.onLocationChanged(aMapLocation)// 显示系统小蓝点
            }
            mLocatinLat = aMapLocation.latitude
            mLocationLon = aMapLocation.longitude
            if (mIsFirstLocation) {
                mIsFirstLocation = false
                if (mLocationList.size > 0) {
                    setMyStopLoca(mLocationList[mLocationList.size - 1].latLong)
                } else {
                    setMyStopLoca(LatLng(mLocatinLat, mLocationLon))
                }


            }
        }
    }

    fun onMyLocationChanged(aMapLocation: AMapLocation?) {
        if (aMapLocation != null && aMapLocation.errorCode == 0) {
            if (mLocationListener != null) {
                mLocationListener!!.onLocationChanged(aMapLocation)// 显示系统小蓝点
            }
            mLocatinLat = aMapLocation.latitude
            mLocationLon = aMapLocation.longitude
            if (mIsFirstLocation) {
                mIsFirstLocation = false
                if (mLocationList.size > 0) {
                    setMyStopLoca(mLocationList[mLocationList.size - 1].latLong)
                } else {
                    setMyStopLoca(LatLng(mLocatinLat, mLocationLon))
                }
                mLocationList.add(LatLongBean(LatLng(mLocatinLat, mLocationLon), aMapLocation.speed))
            } else {
                if (mLastLatLng == null) {
                    mLastLatLng = LatLng(mLocatinLat, mLocationLon)
                } else {
                    findBest(aMapLocation.speed)
                }
            }
        }
    }

    private var mLocatinLat: Double = 0.toDouble()
    private var mLocationLon: Double = 0.toDouble()
    private var mBestLat: Double = 0.toDouble()
    private var mBestLon: Double = 0.toDouble()
    //当前经纬度
    private var mCurrentLatLng: LatLng? = null
    //上次经纬度
    private var mLastLatLng: LatLng? = null

    private fun findBest(speed: Float) {
        mBestLat = mLocatinLat
        mBestLon = mLocationLon
        mCurrentLatLng = LatLng(mBestLat, mBestLon)
        mLocationList.add(LatLongBean(mCurrentLatLng!!, speed))
        mMarkMyLocation!!.position = mCurrentLatLng
        drawRideTraceTotal()


    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        context.unregisterReceiver(locationReceiver)
    }

    private var totalLine: Polyline? = null
    /**
     * 实时定位展示运动轨迹
     */

    private fun drawRideTraceTotal() {
        if (isTracking) {
            if (totalLine != null) {
                totalLine!!.remove()
                totalLine = null
            }
            setMystartLoca(LatLng(mLocationList[0].latLong.latitude, mLocationList[0].latLong.longitude))
            val polylineOptions = PolylineOptions()
            val pathSmoothList = ArrayList<LatLng>()
            val colorList = ArrayList<Int>()
            val locationList = ArrayList<LatLongBean>()
            locationList.addAll(PathSmooth().pathOptimize(mLocationList)!!)
            for (i in locationList.indices) {
                pathSmoothList.add(locationList[i].latLong)
                colorList.add(colorList.size, WalkUtil.getColorList(context)[0])
            }
            polylineOptions.addAll(pathSmoothList)
            polylineOptions.visible(true).width(15f).zIndex(10f)
            //        加入对应的颜色,使用colorValues 即表示使用多颜色，使用color表示使用单色线
            polylineOptions.colorValues(colorList)
            //加上这个属性，表示使用渐变线
            polylineOptions.useGradient(true)
            totalLine = map.addPolyline(polylineOptions)
            map.moveCamera(CameraUpdateFactory.changeLatLng(LatLng(mLocatinLat, mLocationLon)))

        }

    }


    private fun setMyStopLoca(latlng: LatLng) {
        if (mMarkMyLocation != null) {
            mMarkMyLocation!!.destroy()
            mMarkMyLocation = null
        }
        if (mMarkMyLocation == null) {
            val markerOptions = MarkerOptions()
            markerOptions.isFlat = false
            markerOptions.anchor(0.5f, 0.7f)
            markerOptions.zIndex(25f)
            markerOptions.zIndex(90f)
            val iv = ImageView(context)
            val fmIv = FrameLayout.LayoutParams(70, 70)
            iv.setImageResource(R.drawable.location_icon)
            iv.layoutParams = fmIv
            val markerIcon = BitmapDescriptorFactory.fromView(iv)
            markerOptions.icon(markerIcon)
            markerOptions.position(latlng)
            mMarkMyLocation = map.addMarker(markerOptions)

        } else {
            mMarkMyLocation!!.position = latlng
        }
    }

    private fun setMystartLoca(latlng: LatLng) {
        if (mMarkStartLocation != null) {
            return
        }
        if (mMarkStartLocation == null) {
            val markerOptions = MarkerOptions()
            markerOptions.isFlat = false
            markerOptions.anchor(0.5f, 0.7f)
            markerOptions.zIndex(25f)
            markerOptions.zIndex(90f)
            val iv = ImageView(context)
            val fmIv = FrameLayout.LayoutParams(70, 70)
            iv.setImageResource(R.drawable.location_start_icon)
            iv.layoutParams = fmIv
            val markerIcon = BitmapDescriptorFactory.fromView(iv)
            markerOptions.icon(markerIcon)
            markerOptions.position(latlng)
            mMarkStartLocation = map.addMarker(markerOptions)

        } else {
            mMarkStartLocation!!.position = latlng
        }
    }

    fun setTraceEnabled(enabled: Boolean) {
        isTracking = enabled
        if (isTracking) {
            locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
        }
        getTraceList()
    }


    fun setLocationEnabled(enabled: Boolean) {
        locationStyle.showMyLocation(true)
        map.myLocationStyle = locationStyle
        map.isMyLocationEnabled = enabled

    }


    private fun getTraceList() {
        val todayZero = DateUtil.getTodayZero()
        val userId = FileUtils.getSharedPreferences(context, "uid", "00000") as String
        DataBaseOpenHelper.getInstance().queryValues(DataBaseOperateToken.TOKEN_QUERY_TABLE, false, DBConfig.TABLE_NAME, null, "uid = ? and locTime < ?", arrayOf(userId, todayZero.toString()), null, null, null, null, object : IQueryCallback {
            override fun onQueryComplete(token: Int, cursor: Cursor) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        mLocationList.add(LatLongBean(LatLng(cursor.getDouble(cursor.getColumnIndex(DBConfig.location.locLatitude))
                                , cursor.getDouble(cursor.getColumnIndex(DBConfig.location.locLongitude))), cursor.getFloat(cursor.getColumnIndex(DBConfig.location.speed))))
                    }
                    cursor.close()
                }
                if (mLocationList.size > 0) {
                    drawRideTraceTotal()
                }
            }

            override fun onAsyncOperateFailed() {}
        })

    }


}

