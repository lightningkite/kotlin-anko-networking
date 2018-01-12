package com.lightningkite.kotlin.anko.networking.image

import android.content.Context
import android.graphics.Bitmap
import com.lightningkite.kotlin.lambda.invokeAll
import com.lightningkite.kotlin.networking.TypedResponse
import okhttp3.Request

/**
 *
 * Created by joseph on 8/3/17.
 */
class ImageLoader(val baseRequest: Request.Builder, val imageMaxWidth: Int, val imageMaxHeight: Int) : Disposable {
    val requests = HashSet<String>()
    val callbacks = HashMap<String, ArrayList<(TypedResponse<Bitmap>) -> Unit>>()
    val results = HashMap<String, Bitmap>()

    fun getImage(context: Context, input: String, callback: (TypedResponse<Bitmap>) -> Unit) {
        if (results.containsKey(input)) {
            callback.invoke(TypedResponse(200, results[input]!!))
        } else {
            callbacks.getOrPut(input) { ArrayList() }.add(callback)
            if (!requests.contains(input)) {
                requests.add(input)
                baseRequest.url(input).lambdaBitmapExif(context, imageMaxWidth, imageMaxHeight).invokeAsync {
                    if (it.isSuccessful())
                        results[input] = it.result!!
                    callbacks[input]?.invokeAll(it)
                    callbacks.remove(input)
                    requests.remove(input)
                }
            }
        }
    }

    override fun dispose() {
        for ((key, image) in results) {
            image.recycle()
        }
        results.clear()
    }
}