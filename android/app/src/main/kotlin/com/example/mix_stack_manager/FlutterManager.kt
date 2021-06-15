package com.example.mix_stack_manager

import android.content.Context
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.mix_stack_manager.bean.ReceivedMessage
import com.example.mix_stack_manager.bean.SendMessage
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugin.common.StringCodec
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.measureTimeMillis

class FlutterManager {

    companion object {
        //FlutterEngine缓存的key
        const val FLUTTER_ENGINE = "flutter_engine"

        //BasicMessageChannel的key
        const val FLUTTER_MIX_STACK_CHANNEL = "mix_stack_message_channel"

        //flutter初始化成功的消息
        private const val FLUTTER_ENGINE_INIT_FINISH = "flutter_engine_init_finish"

        val instance: FlutterManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            FlutterManager()
        }
    }

    private var messageChannel: BasicMessageChannel<String>? = null

    // 初始化状态回调
    private var initCallback: ((status: Int) -> Unit)? = null

    private val routeCallbackStack: Stack<RouteCallback> by lazy {
        Stack()
    }

    // basicMessageChannel消息处理器
    private val messageHandler =
        BasicMessageChannel.MessageHandler<String> { message, reply ->
            LogUtils.i("FlutterManager", "received message -> $message")
            if (FLUTTER_ENGINE_INIT_FINISH == message) {
                initCallback?.run {
                    this(2)
                }
            }
            //处理其他交互信息
            else {
                try {
                    var received = GsonUtils.fromJson(message, ReceivedMessage::class.java)
                    parseMessage(received)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    /**
     * 初始化FlutterEngine
     * @param context 上下文
     * @param block 初始化状态回调 0 不需要初始化 1 开始初始化 2 初始化完毕
     */
    @Synchronized
    fun initFlutterEngine(context: Context, block: (status: Int) -> Unit) {
        if (!FlutterEngineCache.getInstance().contains(FLUTTER_ENGINE)) {

            block(1)
            var engine = FlutterEngine(context.applicationContext)
            messageChannel = BasicMessageChannel(
                engine.dartExecutor,
                FLUTTER_MIX_STACK_CHANNEL,
                StringCodec.INSTANCE
            )
            messageChannel?.setMessageHandler(messageHandler)
            this.initCallback = block

            //开始运行dart代码
            engine.dartExecutor.executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            )

            FlutterEngineCache.getInstance().put(FLUTTER_ENGINE, engine)
        } else {
            block(0)
        }
    }

    fun isInitFinish(): Boolean {
        return FlutterEngineCache.getInstance().get(FLUTTER_ENGINE) != null
    }

    fun release() {
        FlutterEngineCache.getInstance().get(FLUTTER_ENGINE)?.run {
            destroy()
        }
        FlutterEngineCache.getInstance().remove(FLUTTER_ENGINE)
        messageChannel = null
        routeCallbackStack.clear()
    }

    fun sendMessage(message: SendMessage) {
        messageChannel?.run {
            send(GsonUtils.toJson(message))
        }
    }

    fun getFlutterEngine(): FlutterEngine {
        val engine = FlutterEngineCache.getInstance().get(FLUTTER_ENGINE)
            ?: throw Exception("请先初始化FlutterEngine！")
        return engine
    }

    //添加route监听
    fun addRouteCallback(callback: RouteCallback) {
        routeCallbackStack.push(callback)
        //onDestroy时移除监听
        callback.getLifecycleOwner().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                routeCallbackStack.pop()
            }
        })
    }

    private inline fun parseMessage(message: ReceivedMessage) {
        if (routeCallbackStack.isEmpty()) {
            return
        }

        val peek = routeCallbackStack.peek()
        when (message.operateType) {
            //打开native
            0 -> {
                val nativeRoute = message.nativeRoute!!
                peek.routeNative(nativeRoute, message.nativeParams)
            }
            //Flutter栈更新
            1 -> {
                when (message.flutterType) {
                    //入栈
                    0 -> {
                        peek.onPush(message.flutterCurrentRoute!!)
                    }
                    //出栈
                    1 -> {
                        peek.onPop(message.flutterCurrentRoute!!)
                    }
                    //替换
                    2 -> {
                        peek.onReplace(message.flutterCurrentRoute!!, message.flutterPreRoute!!)
                    }
                    //移除
                    3 -> {

                    }
                }
            }
        }
    }

    /**
     * flutter路由变化回调
     */
    interface RouteCallback {

        fun onPush(route: String)

        fun onPop(route: String)

        fun onReplace(newRoute: String, oldRoute: String)

        fun onRemove(route: String)

        fun routeNative(nativeRoute: String, params: HashMap<String, Any>? = null)

        fun getLifecycleOwner(): LifecycleOwner
    }
}