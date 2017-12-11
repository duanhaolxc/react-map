
package cn.qiuxiang.react.amap3d.utils

import android.content.Context
import cn.qiuxiang.react.amap3d.R
import com.amap.api.maps.model.LatLng
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

/**
 * Created by Administrator on 2016/3/2.
 */
object WalkUtil {

    /**
     * 算斜率
     */
    fun getSlope(fromPoint: LatLng, toPoint: LatLng): Double {
        if (toPoint.longitude == fromPoint.longitude) {
            return java.lang.Double.MAX_VALUE
        }
        return if (toPoint.latitude == fromPoint.latitude) {
            java.lang.Double.MAX_VALUE
        } else (toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude)
    }

    /**
     * 根据点和斜率算取截距
     */
    fun getInterception(slope: Double, point: LatLng): Double {

        return point.latitude - slope * point.longitude
    }

    /**
     * 计算x方向每次移动的距离
     */
    fun getXMoveDistance(slope: Double, dis: Double): Double {
        return if (slope == java.lang.Double.MAX_VALUE) {
            dis
        } else Math.abs(dis * slope / Math.sqrt(1 + slope * slope))
    }


    fun getDifferent(list1: List<String>, list2: List<String>): List<String> {
        val map = HashMap<String, Int>(list1.size)
        val diff = ArrayList<String>()

        for (string in list1) {
            map.put(string, 1)
        }

        for (string in list2) {
            var cc: Int? = map[string]
            if (cc != null) {
                map.put(string, ++cc)
                continue
            }
        }

        for ((key, value) in map) {
            if (value == 1) {
                diff.add(key)
            }
        }
        return diff
    }


    /**
     * 使用NumberFormat,保留小数点后两位
     */
    fun formatDouble(value: Double): String {
        val nf = NumberFormat.getNumberInstance()
        nf.maximumFractionDigits = 2
        /*
         * setMinimumFractionDigits设置成2
         *
         * 如果不这么做，那么当value的值是100.00的时候返回100
         *
         * 而不是100.00
         */
        nf.minimumFractionDigits = 2
        nf.roundingMode = RoundingMode.HALF_UP
        /*
         * 如果想输出的格式用逗号隔开，可以设置成true
         */
        nf.isGroupingUsed = false
        return nf.format(value)
    }

    fun strToInt(s: String): Int {
        return Integer.valueOf(s)!!.toInt()
    }

    /**
     * 根据两点算取图标转的角度
     */
    fun getAngle(fromPoint: LatLng, toPoint: LatLng): Double {
        val slope = getSlope(fromPoint, toPoint)
        if (slope == java.lang.Double.MAX_VALUE) {
            return if (toPoint.latitude > fromPoint.latitude) {
                0.0
            } else {
                180.0
            }
        }
        var deltAngle = 0f
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180f
        }
        val radio = Math.atan(slope)
        return 180 * (radio / Math.PI) + deltAngle - 90
    }



    fun getColorList(mContext: Context): List<Int> {
        val colorList = ArrayList<Int>()
        colorList.add(mContext.resources.getColor(R.color.track_line_1))
        colorList.add(mContext.resources.getColor(R.color.track_line_2))
        colorList.add(mContext.resources.getColor(R.color.track_line_3))
        colorList.add(mContext.resources.getColor(R.color.track_line_4))
        colorList.add(mContext.resources.getColor(R.color.track_line_5))
        return colorList
    }


    fun getStatusHeight(context: Context): Int {
        var statusHeight = -1
        try {

            val clazz = Class.forName("com.android.internal.R\$dimen")
            val `object` = clazz.newInstance()
            val height = Integer.parseInt(clazz.getField("status_bar_height").get(`object`).toString())
            statusHeight = context.resources.getDimensionPixelSize(height)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return statusHeight
    }


}
