package com.lightningkite.kotlin.anko.networking.image

import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.lightningkite.kotlin.Disposable
import com.lightningkite.kotlin.anko.isAttachedToWindowCompat
import com.lightningkite.kotlin.async.doAsync
import com.lightningkite.kotlin.networking.NetMethod
import com.lightningkite.kotlin.networking.NetRequest
import com.lightningkite.kotlin.networking.NetStream
import com.lightningkite.kotlin.networking.Networking
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.imageResource
import java.util.*

/**
 *
 * Created by josep on 3/11/2016.
 *
 */


val ImageView_previousBitmap: MutableMap<ImageView, Bitmap> = HashMap()
val ImageView_previousListener: MutableMap<ImageView, View.OnAttachStateChangeListener> = HashMap()

@Deprecated("Use imageStreamSized instead.")
inline fun ImageView.imageStream(url: String, minBytes: Long = Long.MAX_VALUE, crossinline onResult: (Boolean) -> Unit)
        = imageStreamSized(NetRequest(NetMethod.GET, url), minBytes, onResult = { onResult(it != null) })

@Deprecated("Use imageStreamSized instead.")
inline fun ImageView.imageStream(request: NetRequest, minBytes: Long = Long.MAX_VALUE, crossinline onResult: (Boolean) -> Unit)
        = imageStreamSized(request, minBytes, onResult = { onResult(it != null) })

inline fun ImageView.imageStreamSized(request: NetRequest, minBytes: Long = Long.MAX_VALUE, brokenImageResource: Int? = null)
        = imageStreamSized(request, minBytes, brokenImageResource, {})
inline fun ImageView.imageStreamSized(request: NetRequest, minBytes: Long = Long.MAX_VALUE, brokenImageResource: Int? = null, crossinline onResult: (Disposable?) -> Unit) {
    imageStreamCustom(request, howToStream = {
        val bitmap = bitmapSized(minBytes)
        if (bitmap == null) null else bitmap to object : Disposable {
            override fun dispose() {
                bitmap.recycle()
            }
        }
    }, brokenImageResource = brokenImageResource, onResult = onResult)
}

fun ImageView.imageStreamExif(context: Context, request: NetRequest, minBytes: Long, brokenImageResource: Int? = null, onResult: (Disposable?) -> Unit) {
    imageStreamCustom(request, howToStream = {
        val holder = bitmapExif(context, minBytes)
        if (holder == null) null else holder.bitmap to holder
    }, brokenImageResource = brokenImageResource, onResult = onResult)
}

fun ImageView.imageStreamExif(context: Context, request: NetRequest, maxWidth: Int = 2048, maxHeight: Int = 2048, brokenImageResource: Int? = null, onResult: (Disposable?) -> Unit) {
    imageStreamCustom(request, howToStream = {
        val holder = bitmapExif(context, maxWidth, maxHeight)
        if (holder == null) null else holder.bitmap to holder
    }, brokenImageResource = brokenImageResource, onResult = onResult)
}

val ImageView_previousDisposable: MutableMap<ImageView, Disposable> = HashMap()
inline fun ImageView.imageStreamCustom(request: NetRequest, crossinline howToStream: NetStream.() -> Pair<Bitmap, Disposable>?, brokenImageResource: Int? = null, crossinline onResult: (Disposable?) -> Unit) {
    doAsync({
        Networking.stream(request).ifSuccessful?.howToStream()
    }, {
        if (it == null) {
            //It failed, so use the broken image.
            if (brokenImageResource != null) {
                imageResource = brokenImageResource
            }
            onResult(null)
        } else {
            //cancel if this isn't attached.
            if (!isAttachedToWindowCompat()) {
                it.second.dispose()
                return@doAsync
            }

            //setup a listener with disposal for recycling the image and removing itself
            val newListener = object : View.OnAttachStateChangeListener, Disposable {
                var disposed = false
                override fun onViewDetachedFromWindow(v: View?) {
                    dispose()
                }

                override fun onViewAttachedToWindow(v: View?) {
                }

                override fun dispose() {
                    if (disposed) return else disposed = true
                    setImageDrawable(null)
                    it.second.dispose()
                    ImageView_previousDisposable.remove(this@imageStreamCustom)
                    removeOnAttachStateChangeListener(this)
                }
            }

            //Remove old listener of this kind
            ImageView_previousDisposable[this]?.dispose()

            //set the image
            imageBitmap = it.first

            //add new listener
            ImageView_previousDisposable[this] = newListener
            addOnAttachStateChangeListener(newListener)

            //Return the listener as a disposable to dispose the image on demand
            onResult(newListener)
        }
    })
}

enum class ImageLoadState {
    LOADING,
    NEW_IMAGE_LOADED,
    EXISTING_LOADED
}