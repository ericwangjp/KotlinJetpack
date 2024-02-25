package com.example.kotlinjetpack.activity.commonTest

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.kotlinjetpack.R
import com.example.kotlinjetpack.activity.MainActivity
import com.example.kotlinjetpack.databinding.ActivityNotificationDemoBinding
import com.permissionx.guolindev.PermissionX


class NotificationDemoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotificationDemoBinding
    private val TAG = "NotificationActivity"
    private val requestResultData =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.e(TAG, "onActivityResult: ${result.data}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
    }

    private fun initData() {
        binding.btnSendNotification.setOnClickListener {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                // 判断是否开启悬浮窗通知权限（这里经测试无效，部分华为无效）
//                if (!Settings.canDrawOverlays(this)) {
//                    getScreenPermission()
//                } else {
//                    sendNotification()
//                }
//            } else {
//                sendNotification()
//            }
            Log.e(TAG, "是否有通知权限: ${isEnableNotification()}")
//            checkPermission {
            //            需要开启悬浮窗通知权限
            sendNotification()
            
//            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun getScreenPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.data = Uri.parse("package:$packageName")
        requestResultData.launch(intent)
    }

    private fun isEnableNotification(): Boolean {
        return NotificationManagerCompat.from(this).areNotificationsEnabled()
    }

    private fun isOpenFloatingWindowNotifyPermission(channelId: String): Boolean {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //Android 8.0及以上
            val channel =
                mNotificationManager.getNotificationChannel(channelId) //CHANNEL_ID是自己定义的渠道ID
            if (channel.importance == NotificationManager.IMPORTANCE_DEFAULT) { //未开启
                Log.e(TAG, "未开启浮窗通知权限")
                // 跳转到设置页面
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.id)
                startActivity(intent)
            }
            return channel.importance > NotificationManager.IMPORTANCE_DEFAULT
        }
        return true
    }


    /**
     * 通知栏（兼容android 8.0以上）
     */
    private fun sendNotification() {
        //是否震动
        val isVibrate = true

        //1.获取消息服务
        val manager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //默认通道是default
        var channelId = "default"
        //2.如果是android8.0以上的系统，则新建一个消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = "chat-1"
            /**
             * 通道优先级别：
             * IMPORTANCE_NONE 关闭通知
             * IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
             * IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
             * IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
             * IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
             */
            val channel =
                NotificationChannel(channelId, "消息提醒-1", NotificationManager.IMPORTANCE_HIGH)
            //设置该通道的描述（可以不写）
            channel.description = "重要消息，请不要关闭这个通知。"
            //是否绕过勿打扰模式
            channel.setBypassDnd(true)
            //是否允许呼吸灯闪烁
            channel.enableLights(true)
            //闪关灯的灯光颜色
            channel.lightColor = Color.RED
            //桌面launcher的消息角标
            channel.setShowBadge(true)
            Log.e(TAG, "sendNotification: ${channel.sound}" )
//            var uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            val audioAttributesUsage: Int = AudioAttributes.USAGE_NOTIFICATION
//            val audioAttributes: AudioAttributes = AudioAttributes.Builder().setUsage(audioAttributesUsage).build()
//            var mediaPlayer = MediaPlayer()
//            mediaPlayer.setDataSource(this,uri)
//            mediaPlayer.prepareAsync()
//            mediaPlayer.setOnPreparedListener {
//                it.start()
//            }
//            Log.e(TAG, "sendNotification: $uri")
//            channel.setSound(uri,audioAttributes)
            //设置是否应在锁定屏幕上显示此频道的通知
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            if (isVibrate) {
                //是否允许震动
                channel.enableVibration(true)
                //先震动1秒，然后停止0.5秒，再震动2秒则可设置数组为：new long[]{1000, 500, 2000}
                channel.vibrationPattern = longArrayOf(1000, 500, 2000)
            } else {
                channel.enableVibration(false)
                channel.vibrationPattern = longArrayOf(0)
            }
//            manager.deleteNotificationChannel(channelId)
            //创建消息通道
            manager.createNotificationChannel(channel)
        }
        //3.实例化通知
        val nc: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId)
        //通知默认的声音 震动 呼吸灯
        nc.setDefaults(NotificationCompat.DEFAULT_ALL)
        //通知标题
        nc.setContentTitle("消息标题")
        //通知内容
        nc.setContentText("这里是消息内容")
        //设置通知的小图标
        nc.setSmallIcon(R.drawable.icon_pic_default)
        //设置通知的大图标
        nc.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon_add))
        //设定通知显示的时间
        nc.setWhen(System.currentTimeMillis())
        nc.setTicker("悬浮通知")
        //设置通知的优先级
        nc.priority = NotificationCompat.PRIORITY_MAX
        // Android 使用一些预定义的系统范围类别来确定在用户启用勿扰模式后是否发出指定通知来干扰客户
        nc.setCategory(NotificationCompat.CATEGORY_MESSAGE)
        //设置点击通知之后通知是否消失
        nc.setAutoCancel(true)
//        小米手机通知悬浮显示
//        nc.extras.putBoolean("miui.enableFloat",true)
        // 自定义横幅样式显示的布局，安卓12以上不用设置setCustomHeadsUpContentView
        // 用setCustomContentView 和 setCustomBigContentView 单独设置折叠和展开就可以了
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
//            nc.setCustomHeadsUpContentView(RemoteViews(packageName, R.layout.layout_head_banner))
//        }
        //点击通知打开应用
        val application: Context = applicationContext
        val resultIntent = Intent(application, MainActivity::class.java)
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        resultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            application, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        nc.setContentIntent(pendingIntent)
//        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
//            nc.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//            nc.setFullScreenIntent(pendingIntent,true)
//        }
        //4.创建通知，得到build
        val notification: Notification = nc.build()
//        // 设置横幅通知显示的内容
//        notification.headsUpContentView = getRemoteViews()
        //        修改小米手机角标
//        try {
//            val field: Field = notification.javaClass.getDeclaredField("extraNotification")
//            field.isAccessible = true
//            val extraNotification: Any = field.get(notification)
//            val method: Method = extraNotification.javaClass.getDeclaredMethod(
//                "setMessageCount",
//                Int::class.javaPrimitiveType
//            )
//            method.invoke(extraNotification, 1000)
//        } catch (e: Exception) {
//            Log.e(TAG, "小米桌面图标出错: ${e.message}", )
//        }
        //5.发送通知
        manager.notify(0x1001, notification)

    }


    private fun checkPermission(block: () -> Unit) {
        PermissionX.init(this).permissions(
            Manifest.permission.SYSTEM_ALERT_WINDOW
        ).explainReasonBeforeRequest().onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(
                deniedList, "没有权限无法显示通知", "确定", "取消"
            )
        }.onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(
                deniedList, "需要您手动在设置中打开对应的权限", "确定", "取消"
            )
        }.request { allGranted, grantedList, deniedList ->
            if (allGranted) {
//                Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG).show()
                block.invoke()
            } else {
                Toast.makeText(
                    this, "These permissions are denied: $deniedList", Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}