import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:get/get.dart';
import 'dart:convert';

import 'package:mix_stack_manager/manager/router_mapper.dart';

import 'message_bean.dart';

class RouterHelper {
  //注册消息通道
  static final _routerMessageChannel =
      BasicMessageChannel<String?>("mix_stack_message_channel", StringCodec());

  static registerApp() {
    //防止下面空异常
    WidgetsFlutterBinding.ensureInitialized();
    //注册消息监听
    _routerMessageChannel.setMessageHandler((String? message) async {
      if (message != null) {
        print("received message -> $message");
        var messageBean = ReceivedMessage.formJson(json.decode(message));
        // Get.toNamed(FlutterMapper.noAnimRoute("${messageBean.route}"),
        //     preventDuplicates: false);
        Get.toNamed("${messageBean.route}", preventDuplicates: false);
      }
    });
  }

  static sendInitFinsihMessage() {
    print("发送初始化成功的消息");

    ///发送初始化成功的消息
    _routerMessageChannel.send("flutter_engine_init_finish");
  }

  static sendMessage(SendMessage bean) async {
    var result = await _routerMessageChannel.send(json.encode(bean.toJson()));
    print("操作结果返回 -> $result");
  }

  static openNative(
      {@required String? route, HashMap<String, Object>? params}) {
    if (route == null) {
      print("route should not be null!");
    } else {
      var msgBean = SendMessage.routeNativeMsg(route, params);
      sendMessage(msgBean);
    }
  }
}

class RouteNavigatorObserver extends NavigatorObserver {
  @override
  void didPush(Route<dynamic> route, Route<dynamic>? previousRoute) {
    var currentRoute = route.settings.name;

    ///如果HostWidget加载成功，则告知Native FlutterEngine初始化成功
    if (currentRoute == FlutterMapper.ROUTER_HOST) {
      RouterHelper.sendInitFinsihMessage();
    }

    ///与native同步入栈情况
    else {
      _sendMessage(0, route, previousRoute);
    }
  }

  @override
  void didPop(Route<dynamic> route, Route<dynamic>? previousRoute) {
    /// 与native同步出栈情况
    _sendMessage(1, route, previousRoute);
  }

  @override
  void didReplace({Route<dynamic>? newRoute, Route<dynamic>? oldRoute}) {
    ///与native同步栈被replace
    _sendMessage(2, newRoute, oldRoute);
  }

  @override
  void didRemove(Route<dynamic> route, Route<dynamic>? previousRoute) {
    /// 暂时没用到
    print("告知Native当前出栈的信息");
  }

  void _sendMessage(
      int type, Route<dynamic>? route, Route<dynamic>? previousRoute) {
    String? currentRoute = route?.settings.name;
    String? preRoute = previousRoute?.settings.name;

    if (currentRoute == null) {
      throw Exception("入栈的路由为空？这应该是不可能的");
    }
    var msgBean =
        SendMessage.flutterRouteChangeMsg(type, currentRoute, preRoute);

    RouterHelper.sendMessage(msgBean);
  }
}
