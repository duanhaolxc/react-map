package cn.qiuxiang.react.amap3d.maps

import android.content.Context
import android.graphics.Color
import android.widget.FrameLayout
import android.widget.ImageView
import cn.qiuxiang.react.amap3d.utils.LatLongData
import cn.qiuxiang.react.amap3d.utils.PathSmoothTool
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.*
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
                    LatLongData(it.getDouble("latitude"), it.getDouble("longitude"), if (it.hasKey("speed")) it.getDouble("speed").toFloat() else 0.0f)
                })
        fixPolyLines()
    }

    fun setColors(colors: ReadableArray) {
        this.colors = ArrayList((0 until colors.size()).map { colors.getInt(it) })
        fixPolyLines()
    }

    fun addToMap(map: AMap) {
        polyline = map.addPolyline(PolylineOptions().addAll(polylineOptions)
                .color(color)
                .colorValues(PolyLineColors)
                .width(width)
                .useGradient(gradient)
                .geodesic(geodesic)
                .setDottedLine(dashed)
                .zIndex(zIndex))
        val b = LatLngBounds.builder()
        if (polylineOptions != null) {
            polylineOptions!!.indices
                    .map { polylineOptions!![it] }
                    .forEach { b.include(it) }
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 50))
        }
    }

    private fun fixPolyLines() {
        if (this.coordinates.size > 0) {
            polylineOptions = ArrayList()
            for (i in this.coordinates.indices) {
                if (i == 0) {
                    addStartMarker(LatLng(this.coordinates[i].latitude, this.coordinates[i].longitude))
                }
                if (i == coordinates.size - 1) {
                    addEndMarker(LatLng(this.coordinates[i].latitude, this.coordinates[i].longitude))
                }
                polylineOptions!!.add(LatLng(this.coordinates[i].latitude, this.coordinates[i].longitude))
                if (this.colors.size > 0) {
                    var speed = this.coordinates[i].speed
                    if (0 <= speed && speed < 2) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[0])
                    } else if (2 <= speed && speed < 5) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[1])
                    } else if (5 <= speed && speed < 7) {
                        PolyLineColors.add(PolyLineColors.size, this.colors[2])
                    } else {
                        PolyLineColors.add(PolyLineColors.size, this.colors[2])
                    }

                }

            }
            val mpathSmoothTool = PathSmoothTool()
            mpathSmoothTool.intensity = 3
            polyline?.points = mpathSmoothTool.pathOptimize(polylineOptions!!)
        }
    }

    private var mMarkStart: Marker? = null
    private var mMarkend: Marker? = null
    private fun addStartMarker(start: LatLng) {
        if (mMarkStart != null) {
            mMarkStart!!.destroy()
            mMarkStart = null
        }
        if (mMarkStart == null) {
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
            markerOptions.position(start)
            mMarkStart = map.addMarker(markerOptions)

        } else {
            mMarkStart!!.position = start
        }
    }

    private fun addEndMarker(end: LatLng) {
        if (mMarkend != null) {
            mMarkend!!.destroy()
            mMarkend = null
        }
        if (mMarkend == null) {
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
            markerOptions.position(end)
            mMarkend = map.addMarker(markerOptions)

        } else {
            mMarkend!!.position = end
        }
    }
}
