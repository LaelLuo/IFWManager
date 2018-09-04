package io.github.laelluo.ifwmanager

import android.util.Log
import android.widget.Toast

var isDebug = true
fun log(message: String, title: String = "LOG") {
    if (isDebug){
        Log.e(title, message)
    }
}

fun toast(title: String, message: String, time: Int = Toast.LENGTH_SHORT) = Toast.makeText(MyApplication.getContext(), "$title : $message", time).show()