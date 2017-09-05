package cn.qiuxiang.react.amap3d.utils

import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.LatLngBounds

/**
 * Created by duanzhenwei on 2017/9/1.
 */

class MapTools {
    /**
     * 根据自定义内容获取缩放bounds
     */
    fun getLatLngBounds(pointList: List<LatLng>, aMap: AMap) {
        aMap.myLocation
        val b = LatLngBounds.builder()
        pointList.indices
                .map { pointList[it] }
                .forEach { b.include(it) }
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 50))
    }

    //根据中心点和自定义内容获取缩放bounds
    fun getLatLngBounds(centerpoint: LatLng?, pointList: List<LatLng>): LatLngBounds {
        val b = LatLngBounds.builder()
        if (centerpoint != null) {
            for (i in pointList.indices) {
                val p = pointList[i]
                val p1 = LatLng(centerpoint.latitude * 2 - p.latitude, centerpoint.longitude * 2 - p.longitude)
                b.include(p)
                b.include(p1)
            }
        }
        return b.build()
    }
}
