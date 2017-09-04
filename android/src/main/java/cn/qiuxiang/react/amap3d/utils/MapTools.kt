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
        val b = LatLngBounds.builder()
        for (i in pointList.indices) {
            val p = pointList[i]
            b.include(p)
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(b.build(), 200))
    }
}
