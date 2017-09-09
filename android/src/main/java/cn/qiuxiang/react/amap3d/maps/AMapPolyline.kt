package cn.qiuxiang.react.amap3d.maps

import android.content.Context
import android.graphics.Color
import android.util.Log
import cn.qiuxiang.react.amap3d.utils.LatLongData
import cn.qiuxiang.react.amap3d.utils.MapTools
import cn.qiuxiang.react.amap3d.utils.PathSmoothTool
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

    private var coordinates: ArrayList<LatLongData> = ArrayList()
    private var colors: ArrayList<Int> = ArrayList()
    private var PolyLineColors: ArrayList<Int> = ArrayList()
    private lateinit var map: AMap
    /**
     * 实时定位展示运动轨迹
     */
    private var polylineOptions: ArrayList<LatLng>? = null

    fun setCoordinates(coordinates: ReadableArray) {
        this.coordinates = ArrayList((0 until coordinates.size())
                .map { coordinates.getMap(it) }
                .map {
                    LatLongData(it.getDouble("latitude"), it.getDouble("longitude"), if (it.hasKey("speed")) it.getDouble("speed") else 0.0)
                })
        fixPolyLines()
    }

    fun setColors(colors: ReadableArray) {
        this.colors = ArrayList((0 until colors.size()).map { colors.getInt(it) })
        fixPolyLines()
    }

    fun addToMap(map: AMap) {
        this.map=map
        polyline = map.addPolyline(PolylineOptions().addAll(polylineOptions)
                .color(color)
                .colorValues(PolyLineColors)
                .width(width)
                .useGradient(gradient)
                .geodesic(geodesic)
                .setDottedLine(dashed)
                .zIndex(zIndex))
        MapTools().getLatLngBounds(polylineOptions!!, map)

    }

    private fun fixPolyLines() {
        if (this.coordinates.size > 0 && this.colors.size > 0) {
            polylineOptions = ArrayList()
            for (i in this.coordinates.indices) {
                polylineOptions!!.add(LatLng(this.coordinates[i].latitude, this.coordinates[i].longitude))
                var speed = this.coordinates[i].speed
                if (this.colors.size > 0) {
                    if (0 < speed && speed < 2)
                        PolyLineColors.add(PolyLineColors.size, this.colors[0])
                    else if (2 < speed && speed < 4) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[1])
                    } else if (4 < speed && speed < 6) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[2])
                    } else if (6 < speed && speed < 7) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[3])
                    } else if (7 < speed && speed < 10) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[4])
                    } else if (speed > 10) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[4])
                    } else {
                        PolyLineColors.add(PolyLineColors.size, this.colors[0])
                    }
                }
            }
             val mpathSmoothTool = PathSmoothTool()
             mpathSmoothTool.intensity = 3
             polyline?.points = mpathSmoothTool.pathOptimize(polylineOptions!!)
        }

    }
}
