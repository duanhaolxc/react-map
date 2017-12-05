package cn.qiuxiang.react.amap3d.utils

import java.util.*

class Douglas(mLineInit: ArrayList<LatLongBean>?, private val dMax: Double) {
    private val start: Int
    private val end: Int
    private val mLineInit: ArrayList<LatLngPoint>? = null
    private val mLineFilter = ArrayList<LatLngPoint>()


    init {
        if (mLineInit == null) {
            throw IllegalArgumentException("传入的经纬度坐标list == null")
        }
        this.start = 0
        this.end = mLineInit.size - 1
        for (i in mLineInit.indices) {
            this.mLineInit!!.add(LatLngPoint(i, mLineInit[i].latLong, mLineInit[i].speed))
        }

    }

    /**
     * 压缩经纬度点
     *
     * @return
     */
    fun compress(): ArrayList<LatLongBean> {
        val size = mLineInit!!.size
        val latLngPoints = compressLine(mLineInit.toTypedArray(), mLineFilter, start, end, dMax)
        latLngPoints.add(mLineInit[0])
        latLngPoints.add(mLineInit[size - 1])
        //对抽稀之后的点进行排序
        Collections.sort(latLngPoints) { o1, o2 -> o1.compareTo(o2) }
        return latLngPoints.mapTo(ArrayList()) { LatLongBean(it.latLng, it.speed) }
    }


    /**
     * 根据最大距离限制，采用DP方法递归的对原始轨迹进行采样，得到压缩后的轨迹
     * x
     *
     * @param originalLatLngs 原始经纬度坐标点数组
     * @param endLatLngs      保持过滤后的点坐标数组
     * @param start           起始下标
     * @param end             结束下标
     * @param dMax            预先指定好的最大距离误差
     */
    private fun compressLine(originalLatLngs: Array<LatLngPoint>, endLatLngs: ArrayList<LatLngPoint>, start: Int, end: Int, dMax: Double): ArrayList<LatLngPoint> {
        if (start < end) {
            //递归进行调教筛选
            var maxDist = 0.0
            var currentIndex = 0
            for (i in start + 1 until end) {
                val currentDist = LineUtils.distToSegment(originalLatLngs[start], originalLatLngs[end], originalLatLngs[i])
                if (currentDist > maxDist) {
                    maxDist = currentDist
                    currentIndex = i
                }
            }
            //若当前最大距离大于最大距离误差
            if (maxDist >= dMax) {
                //将当前点加入到过滤数组中
                endLatLngs.add(originalLatLngs[currentIndex])
                //将原来的线段以当前点为中心拆成两段，分别进行递归处理
                compressLine(originalLatLngs, endLatLngs, start, currentIndex, dMax)
                compressLine(originalLatLngs, endLatLngs, currentIndex, end, dMax)
            }
        }
        return endLatLngs
    }


}
