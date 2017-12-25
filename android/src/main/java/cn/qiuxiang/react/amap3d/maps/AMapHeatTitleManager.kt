package cn.qiuxiang.react.amap3d.maps

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp

@Suppress("unused")
internal class AMapHeatTitleManager : ViewGroupManager<AMapHeatTitle>() {
    override fun getName(): String {
        return "AMapHeatTitle"
    }

    override fun createViewInstance(reactContext: ThemedReactContext): AMapHeatTitle {
        return AMapHeatTitle(reactContext)
    }

    @ReactProp(name = "coordinates")
    fun setCoordinate(polygon: AMapHeatTitle, coordinates: ReadableArray) {
        polygon.setCoordinates(coordinates)
    }
}