package cn.qiuxiang.react.amap3d.utils

import android.content.Context

import com.amap.api.maps.model.LatLng

import java.math.RoundingMode
import java.text.NumberFormat
import java.util.ArrayList
import java.util.HashMap

import cn.qiuxiang.react.amap3d.R

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


    fun getLineColor(mContext: Context): List<Int> {

        val mPolylinesColor = ArrayList<Int>()


        mPolylinesColor.add(R.color.track_line_1)
        mPolylinesColor.add(R.color.track_line_2)
        mPolylinesColor.add(R.color.track_line_3)
        mPolylinesColor.add(R.color.track_line_4)
        mPolylinesColor.add(R.color.track_line_5)
        mPolylinesColor.add(R.color.track_line_6)
        mPolylinesColor.add(R.color.track_line_7)
        mPolylinesColor.add(R.color.track_line_8)
        mPolylinesColor.add(R.color.track_line_9)
        mPolylinesColor.add(R.color.track_line_10)

        mPolylinesColor.add(R.color.track_line_11)
        mPolylinesColor.add(R.color.track_line_12)
        mPolylinesColor.add(R.color.track_line_13)
        mPolylinesColor.add(R.color.track_line_14)
        mPolylinesColor.add(R.color.track_line_15)
        mPolylinesColor.add(R.color.track_line_16)
        mPolylinesColor.add(R.color.track_line_17)
        mPolylinesColor.add(R.color.track_line_18)
        mPolylinesColor.add(R.color.track_line_19)
        mPolylinesColor.add(R.color.track_line_20)

        mPolylinesColor.add(R.color.track_line_21)
        mPolylinesColor.add(R.color.track_line_22)
        mPolylinesColor.add(R.color.track_line_23)
        mPolylinesColor.add(R.color.track_line_24)
        mPolylinesColor.add(R.color.track_line_25)
        mPolylinesColor.add(R.color.track_line_26)
        mPolylinesColor.add(R.color.track_line_27)
        mPolylinesColor.add(R.color.track_line_28)
        mPolylinesColor.add(R.color.track_line_29)
        mPolylinesColor.add(R.color.track_line_30)

        mPolylinesColor.add(R.color.track_line_31)
        mPolylinesColor.add(R.color.track_line_32)
        mPolylinesColor.add(R.color.track_line_33)
        mPolylinesColor.add(R.color.track_line_34)
        mPolylinesColor.add(R.color.track_line_35)
        mPolylinesColor.add(R.color.track_line_36)
        mPolylinesColor.add(R.color.track_line_37)
        mPolylinesColor.add(R.color.track_line_38)
        mPolylinesColor.add(R.color.track_line_39)
        mPolylinesColor.add(R.color.track_line_40)

        mPolylinesColor.add(R.color.track_line_41)
        mPolylinesColor.add(R.color.track_line_42)
        mPolylinesColor.add(R.color.track_line_43)
        mPolylinesColor.add(R.color.track_line_44)
        mPolylinesColor.add(R.color.track_line_45)
        mPolylinesColor.add(R.color.track_line_46)
        mPolylinesColor.add(R.color.track_line_47)
        mPolylinesColor.add(R.color.track_line_48)
        mPolylinesColor.add(R.color.track_line_49)
        mPolylinesColor.add(R.color.track_line_50)

        mPolylinesColor.add(R.color.track_line_51)
        mPolylinesColor.add(R.color.track_line_52)
        mPolylinesColor.add(R.color.track_line_53)
        mPolylinesColor.add(R.color.track_line_54)
        mPolylinesColor.add(R.color.track_line_55)
        mPolylinesColor.add(R.color.track_line_56)
        mPolylinesColor.add(R.color.track_line_57)
        mPolylinesColor.add(R.color.track_line_58)
        mPolylinesColor.add(R.color.track_line_59)
        mPolylinesColor.add(R.color.track_line_60)

        mPolylinesColor.add(R.color.track_line_61)
        mPolylinesColor.add(R.color.track_line_62)
        mPolylinesColor.add(R.color.track_line_63)
        mPolylinesColor.add(R.color.track_line_64)
        mPolylinesColor.add(R.color.track_line_65)
        mPolylinesColor.add(R.color.track_line_66)
        mPolylinesColor.add(R.color.track_line_67)
        mPolylinesColor.add(R.color.track_line_68)
        mPolylinesColor.add(R.color.track_line_69)
        mPolylinesColor.add(R.color.track_line_70)

        mPolylinesColor.add(R.color.track_line_71)
        mPolylinesColor.add(R.color.track_line_72)
        mPolylinesColor.add(R.color.track_line_73)

        return mPolylinesColor
    }


    fun getColorList(cnt: Int, mContext: Context): List<Int> {
        val colorList = ArrayList<Int>()


        for (i in 0 until cnt) {
            colorList.add(mContext.resources.getColor(R.color.track_line_1))
            colorList.add(mContext.resources.getColor(R.color.track_line_1))
            colorList.add(mContext.resources.getColor(R.color.track_line_2))
            colorList.add(mContext.resources.getColor(R.color.track_line_2))
            colorList.add(mContext.resources.getColor(R.color.track_line_3))
            colorList.add(mContext.resources.getColor(R.color.track_line_3))
            colorList.add(mContext.resources.getColor(R.color.track_line_4))
            colorList.add(mContext.resources.getColor(R.color.track_line_4))
            colorList.add(mContext.resources.getColor(R.color.track_line_5))
            colorList.add(mContext.resources.getColor(R.color.track_line_5))
            colorList.add(mContext.resources.getColor(R.color.track_line_6))
            colorList.add(mContext.resources.getColor(R.color.track_line_6))
            colorList.add(mContext.resources.getColor(R.color.track_line_7))
            colorList.add(mContext.resources.getColor(R.color.track_line_7))
            colorList.add(mContext.resources.getColor(R.color.track_line_8))
            colorList.add(mContext.resources.getColor(R.color.track_line_8))
            colorList.add(mContext.resources.getColor(R.color.track_line_9))
            colorList.add(mContext.resources.getColor(R.color.track_line_9))
            colorList.add(mContext.resources.getColor(R.color.track_line_10))
            colorList.add(mContext.resources.getColor(R.color.track_line_10))

            colorList.add(mContext.resources.getColor(R.color.track_line_11))
            colorList.add(mContext.resources.getColor(R.color.track_line_11))
            colorList.add(mContext.resources.getColor(R.color.track_line_12))
            colorList.add(mContext.resources.getColor(R.color.track_line_12))
            colorList.add(mContext.resources.getColor(R.color.track_line_13))
            colorList.add(mContext.resources.getColor(R.color.track_line_13))
            colorList.add(mContext.resources.getColor(R.color.track_line_14))
            colorList.add(mContext.resources.getColor(R.color.track_line_14))
            colorList.add(mContext.resources.getColor(R.color.track_line_15))
            colorList.add(mContext.resources.getColor(R.color.track_line_15))
            colorList.add(mContext.resources.getColor(R.color.track_line_16))
            colorList.add(mContext.resources.getColor(R.color.track_line_16))
            colorList.add(mContext.resources.getColor(R.color.track_line_17))
            colorList.add(mContext.resources.getColor(R.color.track_line_17))
            colorList.add(mContext.resources.getColor(R.color.track_line_18))
            colorList.add(mContext.resources.getColor(R.color.track_line_18))
            colorList.add(mContext.resources.getColor(R.color.track_line_19))
            colorList.add(mContext.resources.getColor(R.color.track_line_19))
            colorList.add(mContext.resources.getColor(R.color.track_line_20))
            colorList.add(mContext.resources.getColor(R.color.track_line_20))

            colorList.add(mContext.resources.getColor(R.color.track_line_21))
            colorList.add(mContext.resources.getColor(R.color.track_line_21))
            colorList.add(mContext.resources.getColor(R.color.track_line_22))
            colorList.add(mContext.resources.getColor(R.color.track_line_22))
            colorList.add(mContext.resources.getColor(R.color.track_line_23))
            colorList.add(mContext.resources.getColor(R.color.track_line_23))
            colorList.add(mContext.resources.getColor(R.color.track_line_24))
            colorList.add(mContext.resources.getColor(R.color.track_line_24))
            colorList.add(mContext.resources.getColor(R.color.track_line_25))
            colorList.add(mContext.resources.getColor(R.color.track_line_25))
            colorList.add(mContext.resources.getColor(R.color.track_line_26))
            colorList.add(mContext.resources.getColor(R.color.track_line_26))
            colorList.add(mContext.resources.getColor(R.color.track_line_27))
            colorList.add(mContext.resources.getColor(R.color.track_line_27))
            colorList.add(mContext.resources.getColor(R.color.track_line_28))
            colorList.add(mContext.resources.getColor(R.color.track_line_28))
            colorList.add(mContext.resources.getColor(R.color.track_line_29))
            colorList.add(mContext.resources.getColor(R.color.track_line_29))
            colorList.add(mContext.resources.getColor(R.color.track_line_30))
            colorList.add(mContext.resources.getColor(R.color.track_line_30))

            colorList.add(mContext.resources.getColor(R.color.track_line_31))
            colorList.add(mContext.resources.getColor(R.color.track_line_31))
            colorList.add(mContext.resources.getColor(R.color.track_line_32))
            colorList.add(mContext.resources.getColor(R.color.track_line_32))
            colorList.add(mContext.resources.getColor(R.color.track_line_33))
            colorList.add(mContext.resources.getColor(R.color.track_line_33))
            colorList.add(mContext.resources.getColor(R.color.track_line_34))
            colorList.add(mContext.resources.getColor(R.color.track_line_34))
            colorList.add(mContext.resources.getColor(R.color.track_line_35))
            colorList.add(mContext.resources.getColor(R.color.track_line_35))
            colorList.add(mContext.resources.getColor(R.color.track_line_36))
            colorList.add(mContext.resources.getColor(R.color.track_line_36))
            colorList.add(mContext.resources.getColor(R.color.track_line_37))
            colorList.add(mContext.resources.getColor(R.color.track_line_37))
            colorList.add(mContext.resources.getColor(R.color.track_line_38))
            colorList.add(mContext.resources.getColor(R.color.track_line_38))
            colorList.add(mContext.resources.getColor(R.color.track_line_39))
            colorList.add(mContext.resources.getColor(R.color.track_line_39))
            colorList.add(mContext.resources.getColor(R.color.track_line_40))
            colorList.add(mContext.resources.getColor(R.color.track_line_40))

            colorList.add(mContext.resources.getColor(R.color.track_line_41))
            colorList.add(mContext.resources.getColor(R.color.track_line_41))
            colorList.add(mContext.resources.getColor(R.color.track_line_42))
            colorList.add(mContext.resources.getColor(R.color.track_line_42))
            colorList.add(mContext.resources.getColor(R.color.track_line_43))
            colorList.add(mContext.resources.getColor(R.color.track_line_43))
            colorList.add(mContext.resources.getColor(R.color.track_line_44))
            colorList.add(mContext.resources.getColor(R.color.track_line_44))
            colorList.add(mContext.resources.getColor(R.color.track_line_45))
            colorList.add(mContext.resources.getColor(R.color.track_line_45))
            colorList.add(mContext.resources.getColor(R.color.track_line_46))
            colorList.add(mContext.resources.getColor(R.color.track_line_46))
            colorList.add(mContext.resources.getColor(R.color.track_line_47))
            colorList.add(mContext.resources.getColor(R.color.track_line_47))
            colorList.add(mContext.resources.getColor(R.color.track_line_48))
            colorList.add(mContext.resources.getColor(R.color.track_line_48))
            colorList.add(mContext.resources.getColor(R.color.track_line_49))
            colorList.add(mContext.resources.getColor(R.color.track_line_49))
            colorList.add(mContext.resources.getColor(R.color.track_line_50))
            colorList.add(mContext.resources.getColor(R.color.track_line_50))

            colorList.add(mContext.resources.getColor(R.color.track_line_51))
            colorList.add(mContext.resources.getColor(R.color.track_line_51))
            colorList.add(mContext.resources.getColor(R.color.track_line_52))
            colorList.add(mContext.resources.getColor(R.color.track_line_52))
            colorList.add(mContext.resources.getColor(R.color.track_line_53))
            colorList.add(mContext.resources.getColor(R.color.track_line_53))
            colorList.add(mContext.resources.getColor(R.color.track_line_54))
            colorList.add(mContext.resources.getColor(R.color.track_line_54))
            colorList.add(mContext.resources.getColor(R.color.track_line_55))
            colorList.add(mContext.resources.getColor(R.color.track_line_55))
            colorList.add(mContext.resources.getColor(R.color.track_line_56))
            colorList.add(mContext.resources.getColor(R.color.track_line_56))
            colorList.add(mContext.resources.getColor(R.color.track_line_57))
            colorList.add(mContext.resources.getColor(R.color.track_line_57))
            colorList.add(mContext.resources.getColor(R.color.track_line_58))
            colorList.add(mContext.resources.getColor(R.color.track_line_58))
            colorList.add(mContext.resources.getColor(R.color.track_line_59))
            colorList.add(mContext.resources.getColor(R.color.track_line_59))
            colorList.add(mContext.resources.getColor(R.color.track_line_60))
            colorList.add(mContext.resources.getColor(R.color.track_line_60))

            colorList.add(mContext.resources.getColor(R.color.track_line_61))
            colorList.add(mContext.resources.getColor(R.color.track_line_61))
            colorList.add(mContext.resources.getColor(R.color.track_line_62))
            colorList.add(mContext.resources.getColor(R.color.track_line_62))
            colorList.add(mContext.resources.getColor(R.color.track_line_63))
            colorList.add(mContext.resources.getColor(R.color.track_line_63))
            colorList.add(mContext.resources.getColor(R.color.track_line_64))
            colorList.add(mContext.resources.getColor(R.color.track_line_64))
            colorList.add(mContext.resources.getColor(R.color.track_line_65))
            colorList.add(mContext.resources.getColor(R.color.track_line_65))
            colorList.add(mContext.resources.getColor(R.color.track_line_66))
            colorList.add(mContext.resources.getColor(R.color.track_line_66))
            colorList.add(mContext.resources.getColor(R.color.track_line_67))
            colorList.add(mContext.resources.getColor(R.color.track_line_67))
            colorList.add(mContext.resources.getColor(R.color.track_line_68))
            colorList.add(mContext.resources.getColor(R.color.track_line_68))
            colorList.add(mContext.resources.getColor(R.color.track_line_69))
            colorList.add(mContext.resources.getColor(R.color.track_line_69))
            colorList.add(mContext.resources.getColor(R.color.track_line_70))
            colorList.add(mContext.resources.getColor(R.color.track_line_70))

            colorList.add(mContext.resources.getColor(R.color.track_line_71))
            colorList.add(mContext.resources.getColor(R.color.track_line_71))
            colorList.add(mContext.resources.getColor(R.color.track_line_72))
            colorList.add(mContext.resources.getColor(R.color.track_line_72))
            colorList.add(mContext.resources.getColor(R.color.track_line_73))//如果第四个颜色不添加，那么最后一段将显示上一段的颜色
            colorList.add(mContext.resources.getColor(R.color.track_line_73))//如果第四个颜色不添加，那么最后一段将显示上一段的颜色

            colorList.add(mContext.resources.getColor(R.color.track_line_73))
            colorList.add(mContext.resources.getColor(R.color.track_line_73))
            colorList.add(mContext.resources.getColor(R.color.track_line_72))
            colorList.add(mContext.resources.getColor(R.color.track_line_72))
            colorList.add(mContext.resources.getColor(R.color.track_line_71))
            colorList.add(mContext.resources.getColor(R.color.track_line_71))
            colorList.add(mContext.resources.getColor(R.color.track_line_70))
            colorList.add(mContext.resources.getColor(R.color.track_line_70))
            colorList.add(mContext.resources.getColor(R.color.track_line_69))
            colorList.add(mContext.resources.getColor(R.color.track_line_69))
            colorList.add(mContext.resources.getColor(R.color.track_line_68))
            colorList.add(mContext.resources.getColor(R.color.track_line_68))
            colorList.add(mContext.resources.getColor(R.color.track_line_67))
            colorList.add(mContext.resources.getColor(R.color.track_line_67))
            colorList.add(mContext.resources.getColor(R.color.track_line_66))
            colorList.add(mContext.resources.getColor(R.color.track_line_66))
            colorList.add(mContext.resources.getColor(R.color.track_line_65))
            colorList.add(mContext.resources.getColor(R.color.track_line_65))
            colorList.add(mContext.resources.getColor(R.color.track_line_64))
            colorList.add(mContext.resources.getColor(R.color.track_line_64))

            colorList.add(mContext.resources.getColor(R.color.track_line_63))
            colorList.add(mContext.resources.getColor(R.color.track_line_63))
            colorList.add(mContext.resources.getColor(R.color.track_line_62))
            colorList.add(mContext.resources.getColor(R.color.track_line_62))
            colorList.add(mContext.resources.getColor(R.color.track_line_61))
            colorList.add(mContext.resources.getColor(R.color.track_line_61))
            colorList.add(mContext.resources.getColor(R.color.track_line_60))
            colorList.add(mContext.resources.getColor(R.color.track_line_60))
            colorList.add(mContext.resources.getColor(R.color.track_line_59))
            colorList.add(mContext.resources.getColor(R.color.track_line_59))
            colorList.add(mContext.resources.getColor(R.color.track_line_58))
            colorList.add(mContext.resources.getColor(R.color.track_line_58))
            colorList.add(mContext.resources.getColor(R.color.track_line_57))
            colorList.add(mContext.resources.getColor(R.color.track_line_57))
            colorList.add(mContext.resources.getColor(R.color.track_line_56))
            colorList.add(mContext.resources.getColor(R.color.track_line_56))
            colorList.add(mContext.resources.getColor(R.color.track_line_55))
            colorList.add(mContext.resources.getColor(R.color.track_line_55))
            colorList.add(mContext.resources.getColor(R.color.track_line_54))
            colorList.add(mContext.resources.getColor(R.color.track_line_54))

            colorList.add(mContext.resources.getColor(R.color.track_line_53))
            colorList.add(mContext.resources.getColor(R.color.track_line_53))
            colorList.add(mContext.resources.getColor(R.color.track_line_52))
            colorList.add(mContext.resources.getColor(R.color.track_line_52))
            colorList.add(mContext.resources.getColor(R.color.track_line_51))
            colorList.add(mContext.resources.getColor(R.color.track_line_51))
            colorList.add(mContext.resources.getColor(R.color.track_line_50))
            colorList.add(mContext.resources.getColor(R.color.track_line_50))
            colorList.add(mContext.resources.getColor(R.color.track_line_49))
            colorList.add(mContext.resources.getColor(R.color.track_line_49))
            colorList.add(mContext.resources.getColor(R.color.track_line_48))
            colorList.add(mContext.resources.getColor(R.color.track_line_48))
            colorList.add(mContext.resources.getColor(R.color.track_line_47))
            colorList.add(mContext.resources.getColor(R.color.track_line_47))
            colorList.add(mContext.resources.getColor(R.color.track_line_46))
            colorList.add(mContext.resources.getColor(R.color.track_line_46))
            colorList.add(mContext.resources.getColor(R.color.track_line_45))
            colorList.add(mContext.resources.getColor(R.color.track_line_45))
            colorList.add(mContext.resources.getColor(R.color.track_line_44))
            colorList.add(mContext.resources.getColor(R.color.track_line_44))

            colorList.add(mContext.resources.getColor(R.color.track_line_43))
            colorList.add(mContext.resources.getColor(R.color.track_line_43))
            colorList.add(mContext.resources.getColor(R.color.track_line_42))
            colorList.add(mContext.resources.getColor(R.color.track_line_42))
            colorList.add(mContext.resources.getColor(R.color.track_line_41))
            colorList.add(mContext.resources.getColor(R.color.track_line_41))
            colorList.add(mContext.resources.getColor(R.color.track_line_40))
            colorList.add(mContext.resources.getColor(R.color.track_line_40))
            colorList.add(mContext.resources.getColor(R.color.track_line_39))
            colorList.add(mContext.resources.getColor(R.color.track_line_39))
            colorList.add(mContext.resources.getColor(R.color.track_line_38))
            colorList.add(mContext.resources.getColor(R.color.track_line_38))
            colorList.add(mContext.resources.getColor(R.color.track_line_37))
            colorList.add(mContext.resources.getColor(R.color.track_line_37))
            colorList.add(mContext.resources.getColor(R.color.track_line_36))
            colorList.add(mContext.resources.getColor(R.color.track_line_36))
            colorList.add(mContext.resources.getColor(R.color.track_line_35))
            colorList.add(mContext.resources.getColor(R.color.track_line_35))
            colorList.add(mContext.resources.getColor(R.color.track_line_34))
            colorList.add(mContext.resources.getColor(R.color.track_line_34))

            colorList.add(mContext.resources.getColor(R.color.track_line_33))
            colorList.add(mContext.resources.getColor(R.color.track_line_33))
            colorList.add(mContext.resources.getColor(R.color.track_line_32))
            colorList.add(mContext.resources.getColor(R.color.track_line_32))
            colorList.add(mContext.resources.getColor(R.color.track_line_31))
            colorList.add(mContext.resources.getColor(R.color.track_line_31))
            colorList.add(mContext.resources.getColor(R.color.track_line_30))
            colorList.add(mContext.resources.getColor(R.color.track_line_30))
            colorList.add(mContext.resources.getColor(R.color.track_line_29))
            colorList.add(mContext.resources.getColor(R.color.track_line_29))
            colorList.add(mContext.resources.getColor(R.color.track_line_28))
            colorList.add(mContext.resources.getColor(R.color.track_line_28))
            colorList.add(mContext.resources.getColor(R.color.track_line_27))
            colorList.add(mContext.resources.getColor(R.color.track_line_27))
            colorList.add(mContext.resources.getColor(R.color.track_line_26))
            colorList.add(mContext.resources.getColor(R.color.track_line_26))
            colorList.add(mContext.resources.getColor(R.color.track_line_25))
            colorList.add(mContext.resources.getColor(R.color.track_line_25))
            colorList.add(mContext.resources.getColor(R.color.track_line_24))
            colorList.add(mContext.resources.getColor(R.color.track_line_24))

            colorList.add(mContext.resources.getColor(R.color.track_line_23))
            colorList.add(mContext.resources.getColor(R.color.track_line_23))
            colorList.add(mContext.resources.getColor(R.color.track_line_22))
            colorList.add(mContext.resources.getColor(R.color.track_line_22))
            colorList.add(mContext.resources.getColor(R.color.track_line_21))
            colorList.add(mContext.resources.getColor(R.color.track_line_21))
            colorList.add(mContext.resources.getColor(R.color.track_line_20))
            colorList.add(mContext.resources.getColor(R.color.track_line_20))
            colorList.add(mContext.resources.getColor(R.color.track_line_19))
            colorList.add(mContext.resources.getColor(R.color.track_line_19))
            colorList.add(mContext.resources.getColor(R.color.track_line_18))
            colorList.add(mContext.resources.getColor(R.color.track_line_18))
            colorList.add(mContext.resources.getColor(R.color.track_line_17))
            colorList.add(mContext.resources.getColor(R.color.track_line_17))
            colorList.add(mContext.resources.getColor(R.color.track_line_16))
            colorList.add(mContext.resources.getColor(R.color.track_line_16))
            colorList.add(mContext.resources.getColor(R.color.track_line_15))
            colorList.add(mContext.resources.getColor(R.color.track_line_15))
            colorList.add(mContext.resources.getColor(R.color.track_line_14))
            colorList.add(mContext.resources.getColor(R.color.track_line_14))

            colorList.add(mContext.resources.getColor(R.color.track_line_13))
            colorList.add(mContext.resources.getColor(R.color.track_line_13))
            colorList.add(mContext.resources.getColor(R.color.track_line_12))
            colorList.add(mContext.resources.getColor(R.color.track_line_12))
            colorList.add(mContext.resources.getColor(R.color.track_line_11))
            colorList.add(mContext.resources.getColor(R.color.track_line_11))
            colorList.add(mContext.resources.getColor(R.color.track_line_10))
            colorList.add(mContext.resources.getColor(R.color.track_line_10))
            colorList.add(mContext.resources.getColor(R.color.track_line_9))
            colorList.add(mContext.resources.getColor(R.color.track_line_9))
            colorList.add(mContext.resources.getColor(R.color.track_line_8))
            colorList.add(mContext.resources.getColor(R.color.track_line_8))
            colorList.add(mContext.resources.getColor(R.color.track_line_7))
            colorList.add(mContext.resources.getColor(R.color.track_line_7))
            colorList.add(mContext.resources.getColor(R.color.track_line_6))
            colorList.add(mContext.resources.getColor(R.color.track_line_6))
            colorList.add(mContext.resources.getColor(R.color.track_line_5))
            colorList.add(mContext.resources.getColor(R.color.track_line_5))
            colorList.add(mContext.resources.getColor(R.color.track_line_4))
            colorList.add(mContext.resources.getColor(R.color.track_line_4))

            colorList.add(mContext.resources.getColor(R.color.track_line_3))
            colorList.add(mContext.resources.getColor(R.color.track_line_3))
            colorList.add(mContext.resources.getColor(R.color.track_line_2))
            colorList.add(mContext.resources.getColor(R.color.track_line_2))
            colorList.add(mContext.resources.getColor(R.color.track_line_1))//如果第四个颜色不添加，那么最后一段将显示上一段的颜色
            colorList.add(mContext.resources.getColor(R.color.track_line_1))//如果第四个颜色不添加，那么最后一段将显示上一段的颜色
        }

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
