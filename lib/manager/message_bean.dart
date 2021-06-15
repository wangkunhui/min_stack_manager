import 'dart:collection';

///从native接受的消息
class ReceivedMessage {
  ///要跳转的路由
  String? route;

  ///传过来的参数
  Map<String, Object>? params;

  Map<String, dynamic> toJson() {
    return {"route": route, "params": params};
  }

  ReceivedMessage.formJson(Map<String, dynamic> json) {
    route = json["route"];
    params = json["params"];
  }
}

///flutter和原生通信的bean类
class SendMessage {
  /// 发送消息的类型，
  /// 0——打开Native页面
  /// 1——Flutter内部的跳转
  int operateType = 0;

  /// Flutter内部跳转类型
  /// 0——Flutter内Route入栈
  /// 1——Flutter内Route出栈
  /// 2——Flutter内Route被replace
  /// 3——Flutter内Route被移除
  int flutterType = 0;

  /// 跳转到Native层的router
  String? nativeRoute;

  /// 跳转到Native层需要传递的参数
  HashMap<String, Object>? nativeParams;

  /// Flutter内部当前router信息
  String? flutterCurrentRoute;

  ///Flutter内部上一个Route信息
  String? flutterPreRoute;

  Map<String, dynamic> toJson() {
    return {
      "operateType": operateType,
      "flutterType": flutterType,
      "nativeRoute": nativeRoute,
      "nativeParams": nativeParams,
      "flutterCurrentRoute": flutterCurrentRoute,
      "flutterPreRoute": flutterPreRoute,
    };
  }

  ///生成Flutter内部路由变化消息
  static SendMessage flutterRouteChangeMsg(
      int flutterType, String? flutterCurrentRoute, String? flutterPreRoute) {
    var msg = SendMessage();
    msg.operateType = 1;
    msg.flutterType = flutterType;
    msg.flutterCurrentRoute = flutterCurrentRoute;
    msg.flutterPreRoute = flutterPreRoute;
    return msg;
  }

  ///生成Flutter跳转Native消息
  static SendMessage routeNativeMsg(
      String nativeRoute, HashMap<String, Object>? params) {
    var msg = SendMessage();
    msg.operateType = 0;
    msg.nativeRoute = nativeRoute;
    msg.nativeParams = params;
    return msg;
  }
}
