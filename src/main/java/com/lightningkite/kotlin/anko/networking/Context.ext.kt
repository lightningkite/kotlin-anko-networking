package com.lightningkite.kotlin.anko.networking

import android.content.Context
import org.jetbrains.anko.connectivityManager

/**
 * Various extensions for context involving networking.
 * Created by jivie on 4/5/16.
 */

inline fun Context.isNetworkAvailable(): Boolean = connectivityManager.activeNetworkInfo?.isConnected ?: false