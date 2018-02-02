package cn.qiuxiang.react.amap3d.location.service

import cn.qiuxiang.react.amap3d.location.CommonLocation
import com.orhanobut.logger.Logger
import java.util.*
import kotlin.collections.HashMap

class LocCache {

    companion object {
        var cache: LocCache = LocCache()
            private set

        var head = 0

        var tail = 0

        var indexQueue: Queue<CommonLocation> = LinkedList()

        //var locations: MutableMap<Long, CommonLocation> = Hashtable()
    }

    private constructor() {
    }


    fun addElement(commonLocation: CommonLocation) {
        synchronized(this) {
            tail += 1
            //locations[commonLocation.time] = commonLocation
            indexQueue.add(commonLocation)
        }

    }

    fun pollElement(): CommonLocation? {
        synchronized(this) {
            if (tail - head > 0) {
                //val index = indexQueue.element()
                return indexQueue.element()
            }
            return null
        }
    }

    fun removeElement(): CommonLocation? {
        synchronized(this) {
            if (tail - head > 0) {
                val tempIndex = indexQueue.poll()
                head += 1
                /*val tempLocation = locations[tempIndex]
                locations.remove(tempIndex)*/
                return tempIndex
            }
            return null
        }
    }


    fun isEmpty() = indexQueue.size == 0

    fun length() = indexQueue.size

}