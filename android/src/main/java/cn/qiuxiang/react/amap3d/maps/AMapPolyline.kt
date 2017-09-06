package cn.qiuxiang.react.amap3d.maps

import android.content.Context
import android.graphics.Color
import android.util.Log
import cn.qiuxiang.react.amap3d.utils.Douglas
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
    private var polyLines: ArrayList<LatLng> = ArrayList()
    private var colors: ArrayList<Int> = ArrayList()
    private var PolyLineColors: ArrayList<Int> = ArrayList()
    /**
     * 实时定位展示运动轨迹
     */
    internal var builder = StringBuilder()
    internal var d = Douglas()
    private var polylineRunningOptions: PolylineOptions? = null
    fun setCoordinates(coordinates: ReadableArray) {
        this.coordinates = ArrayList((0 until coordinates.size())
                .map { coordinates.getMap(it) }
                .map {
                    LatLongData(it.getDouble("latitude"), it.getDouble("longitude"), if (it.hasKey("speer")) it.getDouble("speer") else 3.0)
                })
        fixPolyLines()
    }

    fun setColors(colors: ReadableArray) {
        this.colors = ArrayList((0..colors.size() - 1).map { colors.getInt(it) })
        fixPolyLines()
    }

    fun addToMap(map: AMap) {
        polyline = map.addPolyline(polylineRunningOptions!!
                .color(color)
                .colorValues(PolyLineColors)
                .width(width)
                .useGradient(gradient)
                .geodesic(geodesic)
                .setDottedLine(dashed)
                .zIndex(zIndex))
        MapTools().getLatLngBounds(polylineRunningOptions!!.points, map)

    }

    private fun fixPolyLines() {
        if (this.coordinates.size>0)
        for (i in this.coordinates.indices) {
            if (i == 0) {//拆分坐标
                builder.append("LINESTRING(" + this.coordinates[i].lattitude + " " + this.coordinates[i].longitude + " " + this.coordinates[i].speer)
            } else {
                builder.append("," + this.coordinates[i].lattitude + " " + this.coordinates[i].longitude + " " + this.coordinates[i].speer)
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
                var speer = d.points!![i].speer
                if (this.colors.size > 0) {
                    if (0 < speer && speer < 2)
                        PolyLineColors.add(PolyLineColors.size, this.colors[0])
                    else if (2 < speer && speer < 4) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[1])
                    } else if (4 < speer && speer < 6) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[2])
                    } else if (6 < speer && speer < 7) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[3])
                    } else if (7 < speer && speer < 10) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[4])
                    } else {
                        PolyLineColors.add(PolyLineColors.size, this.colors[5])
                    }
                }
            }
        }
        polyline?.options = polylineRunningOptions
        builder.setLength(0)
    }
}
