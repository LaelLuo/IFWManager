package io.github.laelluo.ifwmanager
import android.content.Context
import io.github.laelluo.ifwmanager.bean.AppBean

object Data{
    val apps:ArrayList<AppBean> = ArrayList()
    val sharedPreferences by lazy{MyApplication.getContext().getSharedPreferences("data",Context.MODE_PRIVATE)}
    var isOpen: Boolean
        get() = sharedPreferences.getBoolean("open_state",false)
        set(value) = sharedPreferences.edit().putBoolean("open_state",value).apply()

}

