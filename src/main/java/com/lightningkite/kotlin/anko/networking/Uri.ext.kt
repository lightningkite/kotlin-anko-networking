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
    val type = resolver.getType(this) ?: pathSegments.lastOrNull()?.split('.')?.lastOrNull() ?: throw IllegalArgumentException()
    val size = resolver.fileSize(this) ?: throw IllegalArgumentException()
    return NetBody.StreamBody(
            NetContentType.fromString(type),
            size,
            resolver.openInputStream(this)
    )
}

fun NetBody.Companion.fromUri(uri: Uri, resolver: ContentResolver): NetBody {
    val type = resolver.getType(uri) ?: uri.pathSegments.lastOrNull()?.split('.')?.lastOrNull() ?: throw IllegalArgumentException()
    val size = resolver.fileSize(uri) ?: throw IllegalArgumentException()
    return NetBody.StreamBody(
            NetContentType.fromString(type),
            size,
            resolver.openInputStream(uri)
    )
}