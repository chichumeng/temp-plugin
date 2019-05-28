package com.example.hello;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMusicObject;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXVideoObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** HelloPlugin */
public class HelloPlugin implements MethodCallHandler, EventChannel.StreamHandler {
  private static Context context;
  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    context = registrar.activeContext();
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "hello");
    channel.setMethodCallHandler(new HelloPlugin());

    final EventChannel event = new EventChannel(registrar.messenger(), "eventhello");
    event.setStreamHandler(new HelloPlugin());

  }

  private Result regResult;
  @Override
  public void onMethodCall(MethodCall call, Result result) {
    //注册到微信 registerApp
    //登录 login
    //分享类型
    //        public static final int WXSceneSession = 0;
    //        public static final int WXSceneTimeline = 1;
    //        public static final int WXSceneFavorite = 2;
    //        public static final int WXSceneSpecifiedContact = 3;
    //分享文字 shareText
    //分享图片 shareImage
    //分享网页 shareWebpage
    //分享视频 shareVideo
    //分享音乐 shareMusic
    //分享文件 shareFile
    //分享小程序 shareMiniProgram
    // 微信支付 pay
    // 微信登录 login

    switch (call.method){
      case "registerApp": //注册到微信
        registerApp(call,result);
        break;
      case "login":
        login(call,result);
        break;
      case "pay":
        pay(call,result);
        break;
      case "shareText":
        shareText(call,result);
        break;
      case "shareImage":
        shareImage(call,result);
        break;
      case "shareWebpage":
        shareWebpage(call,result);
        break;
      case "shareVideo":
        shareVideo(call,result);
        break;
      case "shareMusic":
        shareMusic(call,result);
        break;
      case "shareFile":
        shareFile(call,result);
        break;
      case "shareMiniProgram":
        shareMiniProgram(call,result);
        break;

      default:
        result.notImplemented();
    }
  }

  private void shareWebpage(MethodCall call, Result result) {
    final String url = call.argument("url");
    final String title = call.argument("title");
    final String thumb = call.argument("thumb");
    final int scene = call.argument("scene");

    new Thread() {
      public void run() {
        //初始化一个WXWebpageObject，填写url
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;

        //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;

        Bitmap bmp = getLocalOrNetBitmap(thumb);

        msg.setThumbImage(bmp);
        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;

        api.sendReq(req);
      }
    }.start();
  }

  private void shareVideo(MethodCall call, Result result) {
    final String videoUrl = call.argument("videoUrl");
    final String title = call.argument("title");
    final String description = call.argument("description");
    final String thumb = call.argument("thumb");
    new Thread(){
      //初始化一个WXVideoObject，填写url
      WXVideoObject video = new WXVideoObject();
    }.start();
  }

  private void shareMiniProgram(MethodCall call, Result result) {
  }

  private void shareFile(MethodCall call, Result result) {
  }

  private void shareMusic(MethodCall call, Result result) {
    final String musicUrl = call.argument("musicUrl");
    final String title = call.argument("title");
    final String description = call.argument("description");
    final String thumb = call.argument("thumb");
    final int scene = call.argument("scene");
    new Thread(){
      public void run() {
        //初始化一个WXMusicObject，填写url
        WXMusicObject music = new WXMusicObject();
        music.musicUrl=musicUrl;

        //用 WXMusicObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = music;
        msg.title = title;
        msg.description = description;
        Bitmap thumbBmp = getLocalOrNetBitmap(thumb);
        //设置音乐缩略图
        msg.setThumbImage(thumbBmp);
        thumbBmp.recycle();

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;

        //调用api接口，发送数据到微信
        api.sendReq(req);
      }
    }.start();
  }

  private void shareImage(MethodCall call, Result result) {
    final String image = call.argument("image");
    final int scene = call.argument("scene");
    new Thread(){
      public void run(){
        Bitmap bmp = getLocalOrNetBitmap(image);

        //初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        msg.setThumbImage(bmp);
        bmp.recycle();

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;

        //调用api接口，发送数据到微信
        api.sendReq(req);
      }
    }.start();
  }

  private void shareText(MethodCall call, Result result) {
    final String text = call.argument("text");
    final int scene = SendMessageToWX.Req.WXSceneTimeline;

    new Thread(){
      public void run(){
        //初始化一个 WXTextObject 对象，填写分享的文本内容
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());  //transaction字段用与唯一标示一个请求
        req.message = msg;
        req.scene = scene;

        //调用api接口，发送数据到微信
        api.sendReq(req);
      }
    }.start();

  }

  // IWXAPI 是第三方app和微信通信的openApi接口
  private IWXAPI api;
  private String appId;

  private void pay(MethodCall call, Result result){
    final String partnerId = call.argument("partnerId");
    final String prepayId = call.argument("prepayId");
    final String nonceStr = call.argument("nonceStr");
    final String timeStamp = call.argument("timeStamp");
    final String sign = call.argument("sign");
    final String packageValue = call.argument("packageValue");
    new Thread(){
      public void run(){
        PayReq payReq = new PayReq();
        payReq.partnerId = partnerId;
        payReq.prepayId = prepayId;
        payReq.nonceStr = nonceStr;
        payReq.timeStamp = timeStamp;
        payReq.sign = sign;
        payReq.packageValue = packageValue;
        payReq.appId = appId;
        api.sendReq(payReq);
      }
    }.start();

  }

  private void login(MethodCall call, Result result){
    final String scope = call.argument("scope");
    final String state = call.argument("state");
    new Thread(){
      public void run(){
        SendAuth.Req sendReq = new SendAuth.Req();
        sendReq.scope = scope;
        sendReq.state = state;
        api.sendReq(sendReq);
      }
    }.start();

  }

  private void registerApp(MethodCall call, Result result){
    this.appId = call.argument("appId");
    // 通过WXAPIFactory工厂，获取IWXAPI的实例
    api = WXAPIFactory.createWXAPI(context, appId, true);

    // 将应用的appId注册到微信
    api.registerApp(appId);

    //建议动态监听微信启动广播进行注册到微信
    context.registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        // 将该app注册到微信
        api.registerApp(appId);
      }
    }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
    result.success(true);
  }



  // APP_ID 替换为你的应用从官方网站申请到的合法appID
  //private static final String APP_ID = "wx850d21e8fc2f523f";

  private void regToWx() {
    // 通过WXAPIFactory工厂，获取IWXAPI的实例
    api = WXAPIFactory.createWXAPI(context, appId, true);

    // 将应用的appId注册到微信
    //api.registerApp(APP_ID);

    //建议动态监听微信启动广播进行注册到微信
    context.registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {

        // 将该app注册到微信
        api.registerApp(appId);
      }
    }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));

  }




  private void sendText(String text){
    //初始化一个 WXTextObject 对象，填写分享的文本内容
    WXTextObject textObj = new WXTextObject();
    textObj.text = text;

    //用 WXTextObject 对象初始化一个 WXMediaMessage 对象
    WXMediaMessage msg = new WXMediaMessage();
    msg.mediaObject = textObj;
    msg.description = text;

    SendMessageToWX.Req req = new SendMessageToWX.Req();
    req.transaction = String.valueOf(System.currentTimeMillis());  //transaction字段用与唯一标示一个请求
    req.message = msg;
    req.scene = SendMessageToWX.Req.WXSceneTimeline;

    //调用api接口，发送数据到微信
    api.sendReq(req);
  }

  private void sendImage(){
    new Thread(){
      public void run(){
        Bitmap bmp= null;
        bmp = getLocalOrNetBitmap("https://via.placeholder.com/500x500");

        //初始化 WXImageObject 和 WXMediaMessage 对象
        WXImageObject imgObj = new WXImageObject(bmp);
        //imgObj.imageData;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        //设置缩略图
        //Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        msg.setThumbImage(bmp);
        bmp.recycle();
        //msg.thumbData = Util.bmpToByteArray(thumbBmp, true);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        //req.userOpenId = getOpenId();
        //调用api接口，发送数据到微信
        api.sendReq(req);
      }
    }.start();
  }

  private void sendWebUrl(){
        new Thread() {
          public void run() {
            //初始化一个WXWebpageObject，填写url
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl ="http://www.wwwjcsc.com/";

            //用 WXWebpageObject 对象初始化一个 WXMediaMessage 对象
            WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title ="这是一个标题";
            msg.description ="这是简单的描述";

            Bitmap netThumb= null;
            netThumb = getLocalOrNetBitmap("https://via.placeholder.com/100x100");
            if(netThumb == null){
              //Toast.makeText(context,"netThumb is null",Toast.LENGTH_SHORT).show();
              return;
            }
            //Bitmap thumbBmp=Bitmap.createScaledBitmap(netThumb,100,100,true);
            //netThumb.recycle();
            //Toast.makeText(context,"100x100",Toast.LENGTH_SHORT).show();
            //Bitmap thumbBmp = netPicToBmp("http://oss.wwwjcsc.com/upload/20181201/15c01fd29a690a.jpg");
            //msg.thumbData =WXUtil.bmpToByteArray(netThumb, true);
            //Bitmap bmp = Bitmap.createScaledBitmap(netThumb, 80, 80, true);
            //bmp.recycle();
            msg.setThumbImage(netThumb);
            //构造一个Req
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = String.valueOf(System.currentTimeMillis());
            req.message =msg;
            req.scene =SendMessageToWX.Req.WXSceneTimeline;

            //调用api接口，发送数据到微信
            api.sendReq(req);
          }
        }.start();
  }

  public Bitmap getLocalOrNetBitmap(String url) {
    Bitmap bitmap = null;
    InputStream in = null;
    BufferedOutputStream out = null;
    try {
      in = new BufferedInputStream(new URL(url).openStream(), 1024);
      final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
      out = new BufferedOutputStream(dataStream, 1024);
      copy(in, out);
      out.flush();
      byte[] data = dataStream.toByteArray();
      bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
      return bitmap;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private static void copy(InputStream in, OutputStream out)
          throws IOException {
    byte[] b = new byte[1024];
    int read;
    while ((read = in.read(b)) != -1) {
      out.write(b, 0, read);
    }
  }


  public Bitmap getBitmap(String url) {
    Bitmap bm = null;
    try {
      URL iconUrl = new URL(url);
      URLConnection conn = iconUrl.openConnection();
      HttpURLConnection http = (HttpURLConnection) conn;

      int length = http.getContentLength();

      conn.connect();
      // 获得图像的字符流
      InputStream is = conn.getInputStream();
      BufferedInputStream bis = new BufferedInputStream(is, length);
      bm = BitmapFactory.decodeStream(bis);
      bis.close();
      is.close();// 关闭流
      Toast.makeText(context,"bitmap 正常",Toast.LENGTH_SHORT).show();
    } catch (Exception e) {
      //e.printStackTrace();
      Toast.makeText(context,e.getMessage()+"这里有些情况",Toast.LENGTH_SHORT).show();
    }
    return bm;
  }
  public static Bitmap netPicToBmp(String src) {
    try {
      URL url = new URL(src);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.connect();
      InputStream input = connection.getInputStream();
      return BitmapFactory.decodeStream(input);
    } catch (IOException e) {
      // Log exception
      return null;
    }
  }

  private BroadcastReceiver chargingStateChangeReceiver;
  private BroadcastReceiver authCodeSateChangeReceiver;
  @Override
  public void onListen(Object o, EventChannel.EventSink events) {

    //begin 注册电池情况广播
    chargingStateChangeReceiver = createChargingStateChangeReceiver(events);
    context.registerReceiver(chargingStateChangeReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    //end

    //注册一个AUTH code 广播
    authCodeSateChangeReceiver = createAuthCodeSateChangeReceiver(events);
    context.registerReceiver(authCodeSateChangeReceiver,new IntentFilter("CCM_WECHAT_ACTION"));
  }

  @Override
  public void onCancel(Object o) {
    //
    context.unregisterReceiver(chargingStateChangeReceiver);
    chargingStateChangeReceiver = null;

    context.unregisterReceiver(authCodeSateChangeReceiver);
    authCodeSateChangeReceiver = null;
  }

  private BroadcastReceiver createAuthCodeSateChangeReceiver(final EventChannel.EventSink events) {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        events.success(intent.getStringExtra("code"));
      }
    };
  }

  private BroadcastReceiver createChargingStateChangeReceiver(final EventChannel.EventSink events) {
    return new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        if (status == BatteryManager.BATTERY_STATUS_UNKNOWN) {
          //events.error("UNAVAILABLE", "Charging status unavailable", null);
        } else {
          boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                  status == BatteryManager.BATTERY_STATUS_FULL;
          //events.success(isCharging ? "charging" : "discharging");
        }
      }
    };
  }

}
