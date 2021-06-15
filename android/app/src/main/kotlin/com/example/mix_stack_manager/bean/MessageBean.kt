package com.example.mix_stack_manager.bean

/**
 * native向flutter发送的消息模型
 */
data class SendMessage(val route: String, val param: HashMap<String, Any>? = null)

class ReceivedMessage {
    /// 发送消息的类型，
    /// 0——打开Native页面
    /// 1——Flutter内部的跳转
    var operateType: Int = 0;

    /// Flutter内部跳转类型
    /// 0——Flutter内Route入栈
    /// 1——Flutter内Route出栈
    /// 2——Flutter内Route被replace
    /// 3——Flutter内Route被移除
    var flutterType: Int = 0;

    /// 跳转到Native层的router
    var nativeRoute: String? = null

    /// 跳转到Native层需要传递的参数
    var nativeParams: HashMap<String, Any>? = null

    /// Flutter内部当前router信息
    var flutterCurrentRoute: String? = null

    ///Flutter内部上一个Route信息
    var flutterPreRoute: String? = null
}
