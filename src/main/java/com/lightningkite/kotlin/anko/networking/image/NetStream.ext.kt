package com.lightningkite.kotlin.anko.networking.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.lightningkite.kotlin.anko.image.ImageUtils
import com.lightningkite.kotlin.anko.image.getBitmapFromUri
import com.lightningkite.kotlin.networking.NetStream
import java.io.File

/**
 * Represents a response from the network.
 * It can only be read from once, as it really just holds a stream.  Don't screw it up.
 * Created by jivie on 9/23/15.
 */

fun NetStream.bitmap(options: BitmapFactory.Options = BitmapFactory.Options()): Bitmap? {
    val opts = BitmapFactory.Options()
    try {
        return readStream {
            BitmapFactory.decodeStream(it, null, opts)
        }
    } catch(e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun NetStream.bitmapSized(minBytes: Long): Bitmap? {
    val opts = BitmapFactory.Options().apply {
        inSampleSize = ImageUtils.calculateInSampleSize(length, minBytes)
    }
    try {
        return readStream {
            BitmapFactory.decodeStream(it, null, opts)
        }
    } catch(e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun NetStream.bitmapExif(context: Context, minBytes: Long): BitmapHolder? {
    try {
        val tempFile = File.createTempFile("image", "jpg", context.cacheDir)
        download(tempFile)
        val bitmap = context.getBitmapFromUri(Uri.fromFile(tempFile), minBytes)
        return if (bitmap != null) BitmapHolder(tempFile, bitmap) else null
    } catch(e: Exception) {
        e.printStackTrace()
        return null
    }
}

fun NetStream.bitmapExif(context: Context, maxWidth: Int, maxHeight: Int): BitmapHolder? {
    try {
        val tempFile = File.createTempFile("image", "jpg", context.cacheDir)
        download(tempFile)
        val bitmap = context.getBitmapFromUri(Uri.fromFile(tempFile), maxWidth, maxHeight)
        return if (bitmap != null) BitmapHolder(tempFile, bitmap) else null
    } catch(e: Exception) {
        e.printStackTrace()
        return null
    }
}