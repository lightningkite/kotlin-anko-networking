package com.ivieleague.kotlin.anko.networking

import android.content.ContentResolver
import android.net.Uri
import com.ivieleague.kotlin.anko.files.fileSize
import com.ivieleague.kotlin.networking.NetBody
import com.ivieleague.kotlin.networking.NetContentType

/**
 * Created by jivie on 6/2/16.
 */

fun Uri.toNetBody(resolver: ContentResolver, uri: Uri): NetBody.StreamBody {
    val type = resolver.getType(uri) ?: throw IllegalArgumentException()
    println(type)
    val size = resolver.fileSize(uri) ?: throw IllegalArgumentException()
    println("type: $type, size: $size")
    return NetBody.StreamBody(
            NetContentType(type),
            size,
            resolver.openInputStream(uri)
    )
}