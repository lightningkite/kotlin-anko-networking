package com.lightningkite.kotlin.anko.networking.image

/**
 * Created by jivie on 6/30/16.
 */
object ImageUtils {
    fun calculateInSampleSize(length: Long, minBytes: Long): Int {
        return Math.ceil(length.toDouble() / minBytes).toInt()
    }

}