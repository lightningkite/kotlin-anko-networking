package com.lightningkite.kotlin.anko.networking.image

import android.content.Context
import android.graphics.Bitmap
import com.lightningkite.kotlin.Disposable
import com.lightningkite.kotlin.async.invokeAsync
import com.lightningkite.kotlin.invokeAll
import com.lightningkite.kotlin.networking.TypedResponse
import okhttp3.Request
import java.util.*

/**
 *
 * Created by joseph on 8/3/17.
 */
class ImageLoader(val baseRequest: Request.Builder, var imageMaxWidth: Int, var imageMaxHeight: Int, var maxCount: Int = 10) : Disposable {
    val requests = HashSet<String>()
    val callbacks = HashMap<String, ArrayList<(TypedResponse<Bitmap>) -> Unit>>()
    val results = HashMap<String, Bitmap>()
    val lastRequest = HashMap<String, Long>()

    fun getImage(context: Context, input: String, callback: (TypedResponse<Bitmap>) -> Unit) {
        lastRequest[input] = System.currentTimeMillis()
        if (results.containsKey(input)) {
            callback.invoke(TypedResponse(200, results[input]!!))
        } else {
            callbacks.getOrPut(input) { ArrayList() }.add(callback)
            if (!requests.contains(input)) {
                requests.add(input)
                baseRequest.url(input).lambdaBitmapExif(context, imageMaxWidth, imageMaxHeight).invokeAsync {
                    if (it.isSuccessful()) {
                        results[input] = it.result!!
                        checkMem()
                    }
                    callbacks[input]?.invokeAll(it)
                    callbacks.remove(input)
                    requests.remove(input)
                }
            }
        }
    }

    fun checkMem() {
        while (results.size > 10) {
            val toRemove = lastRequest.minBy { it.value }!!
            lastRequest.remove(toRemove.key)
            results.remove(toRemove.key)
        }
    }

    override fun dispose() {
        for ((key, image) in results) {
            image.recycle()
        }
        results.clear()
    }
}