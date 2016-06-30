package com.lightningkite.kotlin.anko.networking

import android.content.ContentResolver
import android.net.Uri
import com.lightningkite.kotlin.anko.files.fileSize
import com.lightningkite.kotlin.networking.NetBody
import com.lightningkite.kotlin.networking.NetContentType

/**
 * Created by jivie on 6/2/16.
 */

fun Uri.toNetBody(resolver: ContentResolver): NetBody.StreamBody {
    val type = resolver.getType(this) ?: throw IllegalArgumentException()
    val size = resolver.fileSize(this) ?: throw IllegalArgumentException()
    return NetBody.StreamBody(
            NetContentType(type),
            size,
            resolver.openInputStream(this)
    )
}