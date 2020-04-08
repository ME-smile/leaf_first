import 'dart:async';
import 'dart:convert';
import 'package:flutter/services.dart';

class LfAppVersion {
  String version_name;
  int version_code;

  LfAppVersion.fromJson(Map<String, dynamic> json) {
    this.version_name = json['version_name'];
    this.version_code = json['version_code'];
  }
}

class LeafFirst {
  static const MethodChannel _channel =
      const MethodChannel('leaf_first');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<LfAppVersion> appVersion() async {
    final String result = await _channel.invokeMethod('appVersion');
    Map<String,dynamic> map = jsonDecode(result);
    return LfAppVersion.fromJson(map);
  }
}
