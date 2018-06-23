package io.github.laelluo.ifwmanager.bean

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

data class AppBean(val label: CharSequence,val icon: Drawable, val applicationInfo: ApplicationInfo)