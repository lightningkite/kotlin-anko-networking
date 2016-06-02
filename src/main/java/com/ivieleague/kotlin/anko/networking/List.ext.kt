package com.ivieleague.kotlin.anko.networking

import android.content.Context
import com.ivieleague.kotlin.files.child
import com.ivieleague.kotlin.networking.load
import com.ivieleague.kotlin.networking.save
import java.lang.reflect.Type

/**
 * Created by jivie on 3/29/16.
 */

@Suppress("NOTHING_TO_INLINE")
inline fun <E> List<E>.save(context: Context, name: String) {
    val folder = context.filesDir.child("lists")
    val file = folder.child(name + ".json")
    save(file)
}

inline fun <reified E : Any> MutableList<E>.load(context: Context, name: String) {
    val folder = context.filesDir.child("lists")
    val file = folder.child(name + ".json")
    load(file)
}

@Suppress("NOTHING_TO_INLINE")
inline fun <E : Any> MutableList<E>.load(context: Context, name: String, type: Type) {
    val folder = context.filesDir.child("lists")
    val file = folder.child(name + ".json")
    load(file, type)
}