package com.lightningkite.kotlin.anko.networking

import android.net.Uri
import android.widget.ImageView
import com.lightningkite.kotlin.anko.image.getBitmapFromUri
import com.lightningkite.kotlin.anko.networking.image.lambdaBitmapExif
import com.lightningkite.kotlin.async.invokeAsync
import okhttp3.Request
import org.jetbrains.anko.imageBitmap
import org.jetbrains.anko.imageResource

/**
 *
 * Created by joseph on 11/11/16.
 */

fun ImageView.imageAnyUri(
        uri: String?,
        noImageResource: Int? = null,
        brokenImageResource: Int? = null,
        imageMinBytes: Long,
        requestBuilder: Request.Builder = Request.Builder(),
        onComplete: (Boolean) -> Unit
) {
    if (uri == null || uri.isEmpty()) {
        //set to default image
        if (noImageResource != null) {
            imageResource = noImageResource
        }
        onComplete(false)
    } else {
        val uriObj = Uri.parse(uri)
        if (uriObj.scheme.contains("http")) {
            requestBuilder.url(uri).lambdaBitmapExif(context, imageMinBytes).invokeAsync {
                if (it.result == null) {
                    //set to default image or broken image
                    if (brokenImageResource != null) {
                        imageResource = brokenImageResource
                    }
                    onComplete(false)
                } else {
                    imageBitmap = it.result
                    onComplete(true)
                }
            }
        } else {
            try {
                imageBitmap = context.getBitmapFromUri(Uri.parse(uri), 2048, 2048)!!
                onComplete(true)
            } catch(e: Exception) {
                if (brokenImageResource != null) {
                    imageResource = brokenImageResource
                }
                onComplete(false)
            }
        }
    }
}