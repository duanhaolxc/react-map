package cn.qiuxiang.react.amap3d.utils

class Point
/**
 * 点数据的构造方法
 *
 * @param x
 * 点的X坐标
 * @param y
 * 点的Y坐标
 * @param index 点所属的曲线的索引
 */
(x: Double, y: Double, index: Int,speed:Double) {
    /**
     * 点的X坐标
     */
    var x = 0.0

    /**
     * 点的Y坐标
     */
    var y = 0.0

    /**
     * 点所属的曲线的索引
     */
    var index = 0
    var speed=0.0

    init {
        this.x = x
        this.y = y
        this.index = index
        this.speed=speed
    }

    override fun toString(): String {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", index=" + index +
                '}'
    }
}  