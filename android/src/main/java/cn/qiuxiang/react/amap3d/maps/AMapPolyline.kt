package cn.qiuxiang.react.amap3d.maps

import android.content.Context
import android.graphics.Color
import android.util.Log
import cn.qiuxiang.react.amap3d.utils.Douglas
import com.amap.api.maps.AMap
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Polyline
import com.amap.api.maps.model.PolylineOptions
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.views.view.ReactViewGroup

class AMapPolyline(context: Context) : ReactViewGroup(context) {
    var polyline: Polyline? = null
        private set

    var width: Float = 1f
        set(value) {
            field = value
            polyline?.width = value
        }

    var color: Int = Color.BLACK
        set(value) {
            field = value
            polyline?.color = value
        }

    var zIndex: Float = 0f
        set(value) {
            field = value
            polyline?.zIndex = value
        }

    var geodesic: Boolean = false
        set(value) {
            field = value
            polyline?.isGeodesic = value
        }

    var dashed: Boolean = false
        set(value) {
            field = value
            polyline?.isDottedLine = value
        }

    var gradient: Boolean = false

    private var coordinates: ArrayList<LatLng> = ArrayList()
    private var colors: ArrayList<Int> = ArrayList()
    /**
     * 实时定位展示运动轨迹
     */
    internal var builder = StringBuilder()
    internal var d = Douglas()
    private var polylineRunningOptions: PolylineOptions? = null
    fun setCoordinates(coordinates: ReadableArray) {
        this.coordinates = ArrayList((0 until coordinates.size())
                .map { coordinates.getMap(it) }
                .map { LatLng(it.getDouble("latitude"), it.getDouble("longitude")) })

        for (i in this.coordinates.indices) {
            if (i == 0) {//拆分坐标
                builder.append("LINESTRING(" + this.coordinates[i].latitude + " " + this.coordinates[i].longitude)
            } else {
                builder.append("," + this.coordinates[i].latitude + " " + this.coordinates[i].longitude)
            }

            if (i == this.coordinates.size - 1) {

                builder.append(")")
            }
        }
        d.readPoint(builder)
        d.compress(d.points!![0], d.points!![d.points!!.size - 1])
        for (i in d.points!!.indices) {
            if (d.points!![i].index > -1) {

                if (polylineRunningOptions == null) {
                    polylineRunningOptions = PolylineOptions()
                }
                polylineRunningOptions!!.add(LatLng(d.points!![i].x, d.points!![i].y))
            }
        }
        polyline?.options = polylineRunningOptions
        builder.setLength(0)
    }

    fun setColors(colors: ReadableArray) {
        this.colors = ArrayList((0..colors.size() - 1).map { colors.getInt(it) })
    }

    fun addToMap(map: AMap) {
        polyline = map.addPolyline(polylineRunningOptions!!
                .color(color)
                .colorValues(colors)
                .width(width)
                .useGradient(gradient)
                .geodesic(geodesic)
                .setDottedLine(dashed)
                .zIndex(zIndex))

    }
}
