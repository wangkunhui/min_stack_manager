import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mix_stack_manager/manager/router_mapper.dart';
import 'package:mix_stack_manager/widget/detail_widget.dart';
import 'package:mix_stack_manager/widget/home_widget.dart';

import 'manager/manager.dart';

void main() {
  var getApp = GetMaterialApp(
    initialRoute: FlutterMapper.ROUTER_HOST,
    getPages: [
      GetPage(
          name: FlutterMapper.ROUTER_HOST,
          page: () => HostWidget(),
          transition: Transition.noTransition),
      GetPage(
          name: FlutterMapper.ROUTER_HOME_NO_ANIM,
          page: () => HomeWidget(),
          transition: Transition.noTransition),
      GetPage(
          name: FlutterMapper.ROUTER_HOME,
          page: () => HomeWidget(),
          transition: Transition.rightToLeft),
      GetPage(
          name: FlutterMapper.ROUTER_DETAIL_NO_ANIM,
          page: () => DetailWidget(),
          transition: Transition.noTransition),
      GetPage(
          name: FlutterMapper.ROUTER_DETAIL,
          page: () => DetailWidget(),
          transition: Transition.rightToLeft),
    ],
    navigatorObservers: [RouteNavigatorObserver()],
  );

  RouterHelper.registerApp();
  runApp(getApp);
}

class HostWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold();
  }
}
