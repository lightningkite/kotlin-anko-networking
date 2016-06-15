package com.lightningkite.kotlin.anko.networking.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.lightningkite.kotlin.networking.NetResponse

/**
 * Represents a response from the network.
 * It can only be read from once, as it really just holds a stream.  Don't screw it up.
 * Created by jivie on 9/23/15.
 */


fun NetResponse.bitmap(options: BitmapFactory.Options = BitmapFactory.Options()): Bitmap? {
    try {
        return BitmapFactory.decodeByteArray(raw, 0, raw.size)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}