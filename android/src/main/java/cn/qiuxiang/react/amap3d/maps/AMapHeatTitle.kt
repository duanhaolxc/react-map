package cn.qiuxiang.react.amap3d.maps

import android.content.Context
import com.amap.api.maps.AMap
import com.amap.api.maps.model.HeatmapTileProvider
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.TileOverlay
import com.amap.api.maps.model.TileOverlayOptions
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.views.view.ReactViewGroup
import java.util.*


class AMapHeatTitle(context: Context) : ReactViewGroup(context) {
    var tileOverlay: TileOverlay? = null
        private set

    private var coordinates: Collection<WeightedLatLng> = ArrayList()

    fun setCoordinates(coordinates: ReadableArray) {
        this.coordinates = ArrayList((0 until coordinates.size())
                .map { coordinates.getMap(it) }
                .map { WeightedLatLng(LatLng(it.getDouble("latitude"), it.getDouble("longitude")), it.getDouble("weight")) })
        Log.e("AMapHeatTitle", this.coordinates.toString())
    }

    fun addToMap(map: AMap) {
        // 构建热力图 HeatmapTileProvider
        val builder = HeatmapTileProvider.Builder()
        builder.weightedData(coordinates)
        val heatmapTileProvider = builder.build()
        val tileOverlayOptions = TileOverlayOptions()
        tileOverlayOptions.tileProvider(heatmapTileProvider) // 设置瓦片图层的提供者
        tileOverlay = map.addTileOverlay(tileOverlayOptions)
    }
}
