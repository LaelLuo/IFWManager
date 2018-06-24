package io.github.laelluo.ifwmanager

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.io.DataOutputStream

fun log(title: String, message: String) = Log.e(title, message)
fun Context.toast(title: String, message: String, time: Int = Toast.LENGTH_SHORT) = Toast.makeText(this.applicationContext, " \n$title : $message", time).show()
fun command(cmd: String = ""): Boolean {
    var process: Process? = null
    var os: DataOutputStream? = null
    try {
        process = Runtime.getRuntime().exec("su") //切换到root帐号
        os = DataOutputStream(process!!.outputStream)
        os.writeBytes(cmd + "\n")
        os.writeBytes("exit\n")
        os.flush()
        process.waitFor()
    } catch (e: Exception) {
        log("RootError",e.message!!)
        return false
    } finally {
        try {
            os?.close()
            process?.destroy()
        } catch (e: Exception) { }
    }
    return true
}