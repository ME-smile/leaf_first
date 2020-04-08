package com.leaf.flutterplugin.leaf_first

import android.app.Activity
import com.google.gson.Gson
import com.leaf.flutterplugin.leaf_first.util.CommonUtils
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class LeafFirstPlugin(var mActivity: Activity?) : MethodCallHandler {


  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "leaf_first")
      channel.setMethodCallHandler(LeafFirstPlugin(registrar.activity()))
    }
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
      return
    }

    when(call.method)
    {
      "appVersion" -> {
        val model = hashMapOf<String,Any>()
        model.put("version_name", CommonUtils.getAppVersionName(mActivity))
        model.put("version_code", CommonUtils.getAppVersionCode(mActivity))
        result.success(Gson().toJson(model))
      }
    }
  }
}
