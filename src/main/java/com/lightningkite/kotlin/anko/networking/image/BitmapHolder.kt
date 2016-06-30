package com.lightningkite.kotlin.anko.networking.image

import android.graphics.Bitmap
import com.lightningkite.kotlin.Disposable
import java.io.File

/**
 * Created by jivie on 6/30/16.
 */
class BitmapHolder(val file: File, val bitmap: Bitmap) : Disposable {
    override fun dispose() {
        file.delete()
        bitmap.recycle()
    }
}