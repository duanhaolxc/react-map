package cn.qiuxiang.react.amap3d.utils

import com.amap.api.maps.AMapUtils

object LineUtils {


    /**
     * 使用三角形面积（使用海伦公式求得）相等方法计算点pX到点pA和pB所确定的直线的距离
     * @param start  起始经纬度
     * @param end    结束经纬度
     * @param center 前两个点之间的中心点
     * @return 中心点到 start和end所在直线的距离
     */
    fun distToSegment(start: LatLngPoint, end: LatLngPoint, center: LatLngPoint): Double {
        val a = Math.abs(AMapUtils.calculateLineDistance(start.latLng, end.latLng)).toDouble()
        val b = Math.abs(AMapUtils.calculateLineDistance(start.latLng, center.latLng)).toDouble()
        val c = Math.abs(AMapUtils.calculateLineDistance(end.latLng, center.latLng)).toDouble()
        val p = (a + b + c) / 2.0
        val s = Math.sqrt(Math.abs(p * (p - a) * (p - b) * (p - c)))
        return s * 2.0 / a
    }


}