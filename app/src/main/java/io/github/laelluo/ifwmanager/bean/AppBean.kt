package io.github.laelluo.ifwmanager.bean

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

data class AppBean(
        val label: String,
        val packageName: String,
        val version: String,
        val icon: Drawable,
        val services: List<String>,
        val receivers: List<String>,
        val activities: List<String>,
        val providers: List<String>
)