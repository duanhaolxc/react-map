package cn.qiuxiang.react.amap3d.utils

import com.amap.api.maps.model.LatLng

class LatLngPoint(
        /**
         * 用于记录每一个点的序号
         */
        var id: Int,
        /**
         * 每一个点的经纬度
         */
        var latLng: LatLng, var speed: Float) : Comparable<LatLngPoint> {

    override fun compareTo(o: LatLngPoint): Int {
        if (this.id < o.id) {
            return -1
        } else if (this.id > o.id)
            return 1
        return 0
    }

}