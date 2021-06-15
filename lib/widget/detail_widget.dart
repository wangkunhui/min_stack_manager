import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:mix_stack_manager/manager/manager.dart';
import 'package:mix_stack_manager/manager/router_mapper.dart';

class DetailWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    var index = Get.arguments;
    print("index = $index");
    if (index == null) {
      index = 1;
    }

    return Scaffold(
      appBar: AppBar(
        title: Text("DetailWidget"),
      ),
      body: Center(
        child: Column(
          children: [
            SizedBox(
              height: 50,
            ),
            Text("这是第$index个DetailWidget界面"),
            ElevatedButton(
                onPressed: () {
                  Get.toNamed(FlutterMapper.ROUTER_DETAIL,
                      arguments: index + 1, preventDuplicates: false);
                },
                child: Text("我可以打开我自己")),
            ElevatedButton(
                onPressed: () {
                  RouterHelper.openNative(route: NativeMapper.NATIVE_VIEW);
                },
                child: Text("也可以打开Native的Activity")),
          ],
        ),
      ),
    );
  }
}
