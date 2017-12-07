package cn.qiuxiang.react.amap3d.utils

import com.vividsolutions.jts.geom.Coordinate
import com.vividsolutions.jts.geom.Geometry
import com.vividsolutions.jts.io.ParseException
import com.vividsolutions.jts.io.WKTReader
import java.util.*

/**
 * 1）对曲线的首末点虚连一条直线，求曲线上所有点与直线的距离，并找出最大距离值dmax，用dmax与事先给定的阈值D相比：
 * 2）若dmax<D></D>，则将这条曲线上的中间点全部舍去；则该直线段作为曲线的近似，该段曲线处理完毕。
 * 　 若dmax≥D，保留dmax对应的坐标点，并以该点为界，把曲线分为两部分，对这两部分重复使用该方法，即重复1），2）步，直到所有dmax均<D></D>，即完成对曲线的抽稀。
 * 显然，本算法的抽稀精度也与阈值相关，阈值越大，简化程度越大，点减少的越多，反之，化简程度越低，点保留的越多，形状也越趋于原曲线。
 */
class Douglas {

    /**
     * 存储采样点数据的链表
     */
    var points: MutableList<Point>? = ArrayList()

    private var reader: WKTReader? = null

    /**
     * 构造Geometry
     *
     * @param str
     * @return
     */
    fun buildGeo(str: String): Geometry {
        try {
            if (reader == null) {
                reader = WKTReader()
            }
            return reader!!.read(str)
        } catch (e: ParseException) {
            throw RuntimeException("buildGeometry Error", e)
        }

    }

    /**
     * 读取采样点
     */
    private var p: Point? = null
    private var g: Geometry? = null
    private var coords: Array<Coordinate>? = null

    fun readPoint(stringBuilder: StringBuilder) {
        g = buildGeo(stringBuilder.toString())
        coords = g!!.coordinates

        if (null != points) {
            points!!.clear()
        }

        for (i in coords!!.indices) {
            p = Point(coords!![i].x, coords!![i].y, i,0.0)
            points!!.add(p!!)
        }
    }


    /**
     * 压缩算法的开关量
     */
    internal var switchvalue = false

    /**
     * 对矢量曲线进行压缩
     *
     * @param from 曲线的起始点
     * @param to   曲线的终止点
     */
    fun compress(from: Point?, to: Point?) {
        /**
         * 由起始点和终止点构成的直线方程一般式的系数
         */
        println(from!!.y)
        println(to!!.y)
        val A = (from.y - to.y) / Math.sqrt(Math.pow(from.y - to.y, 2.0) + Math.pow(from.x - to.x, 2.0))

        /**
         * 由起始点和终止点构成的直线方程一般式的系数
         */
        val B = (to.x - from.x) / Math.sqrt(Math.pow(from.y - to.y, 2.0) + Math.pow(from.x - to.x, 2.0))

        /**
         * 由起始点和终止点构成的直线方程一般式的系数
         */
        val C = (from.x * to.y - to.x * from.y) / Math.sqrt(Math.pow(from.y - to.y, 2.0) + Math.pow(from.x - to.x, 2.0))

        var d: Double
        var dmax: Double
        val m = points!!.indexOf(from)
        val n = points!!.indexOf(to)
        if (n == m + 1)
            return
        var middle: Point? = null
        val distance = ArrayList<Double>()
        for (i in m + 1 until n) {
            d = Math.abs(A * points!![i].x + B * points!![i].y + C) / Math.sqrt(Math.pow(A, 2.0) + Math.pow(B, 2.0))
            distance.add(d)
        }
        dmax = distance[0]
        for (j in 1 until distance.size) {
            if (distance[j] > dmax)
                dmax = distance[j]
        }
        switchvalue = dmax > D
        if (!switchvalue) {
            // 删除Points(m,n)内的坐标
            for (i in m + 1 until n) {
                points!![i].index = -1
            }

        } else {
            (m + 1 until n)
                    .filter { Math.abs(A * points!![it].x + B * points!![it].y + C) / Math.sqrt(Math.pow(A, 2.0) + Math.pow(B, 2.0)) == dmax }
                    .forEach { middle = points!![it] }
            compress(from, middle)
            compress(middle, to)
        }
    }

    companion object {

        /**
         * 控制数据压缩精度的极差
         */
        private val D = 5.0
    }
}
