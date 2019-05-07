package com.update;

import android.content.Context;

import com.update.dealog.UpAppDialog;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

public class FlutterUpdatePlugin implements MethodCallHandler {
  private static Context context;
  public static void registerWith(Registrar registrar) {
    context = registrar.activity();
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_update_plugin");
    channel.setMethodCallHandler(new FlutterUpdatePlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } if(call.method.equals("getUpdate")){
      UpAppDialog upAppDialog = new UpAppDialog(context);
      upAppDialog.setCancelable(false);
      upAppDialog.show();
      upAppDialog.setTitleT("是否升级到"+call.argument("Edition")+"版本？",""+call.argument("Content"));
      upAppDialog.setUpdateUrl(""+call.argument("Url"),""+call.argument("Edition"));
//      upAppDialog.setWhetherForce(""+call.argument("Force"));
    } else {
      result.notImplemented();
    }
  }
}
