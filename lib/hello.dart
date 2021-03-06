import 'dart:async';

import 'package:flutter/services.dart';

class Hello {
  static const int WXSceneSession = 0;
  static const int WXSceneTimeline = 1;
  static const int WXSceneFavorite = 2;
  static const int WXSceneSpecifiedContact = 3;
  static const MethodChannel _channel =
      const MethodChannel('hello');

  static const EventChannel eventChannel =
      EventChannel('eventhello');

  static void listen(Function _onEvent,Function _onError){
    eventChannel.receiveBroadcastStream().listen(_onEvent, onError: _onError);
  }

  static Future<String> registerApp(String appId) async {
    String result = await _channel.invokeMethod('registerApp',{"appId":appId});
    return result;
  }
  static Future<void> shareText(String text,{int scene=WXSceneTimeline}) async{
    await _channel.invokeMethod("shareText",{"text":text,"scene":scene});
  }

  static Future<void> sendWebUrl() async{
    await _channel.invokeMethod("sendWebUrl");
  }
  static Future<void> sendImage() async{
    await _channel.invokeMethod("sendImage");
  }
  static Future<void> login(String state) async{
    await _channel.invokeMethod("login",{"scope":"snsapi_userinfo","state":state ?? ""});
  }

  static Future<void> pay({String partnerId,String prepayId,String nonceStr,String timeStamp,String sign,String packageValue}) async{
    await _channel.invokeMethod("pay",{
                    "partnerId": partnerId,
                    "prepayId": prepayId,
                    "nonceStr": nonceStr,
                    "timeStamp": timeStamp,
                    "sign": sign,
                    "packageValue": packageValue
    });
  }
}
