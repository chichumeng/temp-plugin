import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:hello/hello.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String eventStr = "无";
  @override
  void initState() {
    super.initState();
    initPlatformState();
    Hello.listen(_onEvent, _onError);
  }

  void _onEvent(Object event) {
    setState(() {
      eventStr = event;
    });
  }

  void _onError(Object error) {
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      await Hello.registerApp("wx850d21e8fc2f523f");
      //platformVersion += await Hello.regToWx;
    } on PlatformException {
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('demo'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              FlatButton(child: Text("login"),onPressed: () async {
                await Hello.login("temp");
              },),
              FlatButton(child: Text("shareText"),onPressed: () async {
                await Hello.shareText("share text 分享的内容",scene:Hello.WXSceneTimeline);
              },),
              Text("$eventStr"),
              FlatButton(child:Text("pay"),
                onPressed: () async{
                  await Hello.pay(
                    partnerId: "1400574002",
                    prepayId: "wx281627019215342a016be1bd9810434800",
                    nonceStr: "5cecf0d5a6da7",
                    timeStamp: "1559032021",
                    sign: "EB3FF2DA4CB287D28E2528763952FE04",
                    packageValue: "Sign=WXPay"
                  );
                },
              ),
            ],
          ),
        ),
      ),
    );
  }
}