package io.xlink.wifi.pipe;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import io.xlink.wifi.pipe.bean.Device;
import io.xlink.wifi.pipe.http.HttpManage;
import io.xlink.wifi.pipe.manage.DeviceManage;
import io.xlink.wifi.pipe.util.CrashHandler;
import io.xlink.wifi.pipe.util.SharedPreferencesUtil;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.bean.DataPoint;
import io.xlink.wifi.sdk.bean.EventNotify;
import io.xlink.wifi.sdk.listener.XlinkNetListener;
import io.xlink.wifi.sdk.util.MyLog;

public class MyApp extends Application implements XlinkNetListener {

    private static final String TAG = "MyApp";
    private static MyApp application;

    /**
     * 首选项设置
     */
    public static SharedPreferences sharedPreferences;
    // 判断程序是否正常启动
    public boolean auth;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        auth = false;
        Log.e(TAG, "onCreate()");
        // bug收集
        CrashHandler.init(this);
        // 初始化sdk
        XlinkAgent.init(this);
//        XlinkAgent.setCMServer("cm.iotbull.com", 23778);
//        XlinkAgent.setCMServer("42.121.122.23", 23778);
//        XlinkAgent.setCMServer("114.55.119.222",23778);
        XlinkAgent.setCMServer("cm2.xlink.cn", 23778);
        XlinkAgent.getInstance().addXlinkListener(this);
        //优先内网连接(谨慎使用,如果优先内网,则外网会在内网连接成功或者失败,或者超时后再进行连接,可能会比较慢)
//        XlinkAgent.getInstance().setPreInnerServiceMode(true);
        // 首选项 用于存储用户
        sharedPreferences = getSharedPreferences("XlinkOfficiaDemo", Context.MODE_PRIVATE);
        appid = SharedPreferencesUtil.queryIntValue(Constant.SAVE_appId);
        authKey = SharedPreferencesUtil.queryValue(Constant.SAVE_authKey, "");
        String prodctid = SharedPreferencesUtil.queryValue(Constant.SAVE_PRODUCTID);
        String compayId = SharedPreferencesUtil.queryValue(Constant.SAVE_COMPANY_ID);

        if (!TextUtils.isEmpty(prodctid)) {
            Constant.PRODUCTID = prodctid.replace(" ", "");
        }
        if (!TextUtils.isEmpty(compayId)) {
            HttpManage.COMPANY_ID = compayId.replace(" ", "");
        }
        // if (prodctid.equals("")) {
        // SharedPreferencesUtil.keepShared("pid", Constant.PRODUCTID);
        // } else if (prodctid.length() > 30) {
        // Constant.PRODUCTID = prodctid;
        // }
        // Constant.PRODUCTID= Constant.PRODUCTID.trim();
        // Constant.PRODUCTID=Constant.PRODUCTID.replace(" ", "");
        initHandler();
        for (Device device : DeviceManage.getInstance().getDevices()) {// 向sdk初始化设备
            MyLog.e(TAG, "init device:" + device.getMacAddress());
            XlinkAgent.getInstance().initDevice(device.getXDevice());
        }

        // 获取当前软件包版本号和版本名称
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            versionCode = pinfo.versionCode;
            versionName = pinfo.versionName;
            packageName = pinfo.packageName;

        } catch (NameNotFoundException e) {
        }
    }

    public String versionName;
    public int versionCode;
    public String packageName;
    private static Handler mainHandler = null;
    private String accessToken;

    public static void initHandler() {
        mainHandler = new Handler();
    }

    /**
     * 执行在主线程任务
     *
     * @param runnable
     */
    public static void postToMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public static MyApp getApp() {
        return application;
    }

    // 全局登录的 appId 和auth
    public int appid;
    public String authKey;

    public void setAppid(int id) {
        appid = id;
    }

    public void setAuth(String auth) {
        this.authKey = auth;
    }

    public int getAppid() {
        return appid;
    }


    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }


    public String getAuth() {
        return authKey;
    }

    // 当前的activity
    private Activity currentActivity;

    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }

    // xlink 回调的onStart
    @Override
    public void onStart(int code) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onStart code" + code);
        sendBroad(Constant.BROADCAST_ON_START, code);
    }

    // 回调登录xlink状态
    @Override
    public void onLogin(int code) {
        // TODO Auto-generated method stub
        Log.e(TAG, "login code" + code);
        sendBroad(Constant.BROADCAST_ON_LOGIN, code);
        if (code == XlinkCode.SUCCEED) {
            XlinkUtils.shortTips("云端网络已可用");
        } else if (code == XlinkCode.CLOUD_CONNECT_NO_NETWORK
                || XlinkUtils.isConnected()) {
            // XlinkUtils.shortTips("网络不可用，请检查网络连接");

        } else {
            XlinkUtils.shortTips("连接到服务器失败，请检查网络连接");
        }
    }

    @Override
    public void onLocalDisconnect(int code) {
        if (code == XlinkCode.LOCAL_SERVICE_KILL) {
            // 这里是xlink服务被异常终结了（第三方清理软件，或者进入应用管理被强制停止应用/服务）
            // 永不结束的service
            // 除非调用 XlinkAgent.getInstance().stop（）;
            XlinkAgent.getInstance().start();
        }
        XlinkUtils.shortTips("本地网络已经断开");
    }


    @Override
    public void onDisconnect(int code) {
        if (code == XlinkCode.CLOUD_SERVICE_KILL) {
            // 这里是服务被异常终结了（第三方清理软件，或者进入应用管理被强制停止服务）
            if (appid != 0 && !TextUtils.isEmpty(authKey)) {
                XlinkAgent.getInstance().login(appid, authKey);
            }
        }
        XlinkUtils.shortTips("正在修复云端连接");
    }

    /**
     * 收到 局域网设备推送的pipe数据
     */
    @Override
    public void onRecvPipeData(short messageId,XDevice xdevice,  byte[] data) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onRecvPipeData::device:" + xdevice.toString() + "data:"
                + data);
        Device device = DeviceManage.getInstance().getDevice(
                xdevice.getMacAddress());
        if (device != null) {
            // 发送广播，那个activity需要该数据可以监听广播，并获取数据，然后进行响应的处理
            sendPipeBroad(Constant.BROADCAST_RECVPIPE, device, data);
            // TimerManage.getInstance().parseByte(device,data);
        }
    }

    /**
     * 收到设备通过云端服务器推送的pipe数据
     */
    @Override
    public void onRecvPipeSyncData(short messageId,XDevice xdevice,  byte[] data) {
        // TODO Auto-generated method stub
        Log.e(TAG, "onRecvPipeSyncData::device:" + xdevice.toString() + "data:"
                + data);
        Device device = DeviceManage.getInstance().getDevice(
                xdevice.getMacAddress());
        if (device != null) {
            // 发送广播，那个activity需要该数据可以监听广播，并获取数据，然后进行响应的处理
            // TimerManage.getInstance().parseByte(device,data);
            sendPipeBroad(Constant.BROADCAST_RECVPIPE_SYNC, device, data);
        }
    }

    /**
     */
    public void sendBroad(String action, int code) {
        Intent intent = new Intent(action);
        intent.putExtra(Constant.STATUS, code);
        MyApp.this.sendBroadcast(intent);
    }

    /**
     */
    public void sendPipeBroad(String action, Device device, byte[] data) {
        Intent intent = new Intent(action);
        intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
        if (data != null) {
            intent.putExtra(Constant.DATA, data);
        }
        MyApp.this.sendBroadcast(intent);
    }

    /**
     * 设备状态改变：掉线/重连/在线
     */
    @Override
    public void onDeviceStateChanged(XDevice xdevice, int state) {
        // TODO Auto-generated method stub

        MyLog.e(TAG, "onDeviceStateChanged::" + xdevice.getMacAddress()
                + " state:" + state);
        Device device = DeviceManage.getInstance().getDevice(
                xdevice.getMacAddress());
        if (device != null) {
            device.setxDevice(xdevice);
            Intent intent = new Intent(Constant.BROADCAST_DEVICE_CHANGED);
            intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
            intent.putExtra(Constant.STATUS, state);
            MyApp.getApp().sendBroadcast(intent);
        }

    }

    @Override
    public void onDataPointUpdate(XDevice xDevice, List<DataPoint> dataPionts, int channel) {
        MyLog.e(TAG,"onDataPointUpdate:"+dataPionts.toString());

        Device device = DeviceManage.getInstance().getDevice(xDevice.getMacAddress());
        if (device != null) {
            Intent intent = new Intent(Constant.BROADCAST_DATAPOINT_RECV);
            intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
            if (dataPionts != null) {
                intent.putExtra(Constant.DATA, (Serializable) dataPionts);
            }
            MyApp.this.sendBroadcast(intent);
        }
    }

    @Override
    public void onEventNotify(EventNotify eventNotify) {
        String str = "EventNotify{" +
                "notyfyFlags=" + eventNotify.notyfyFlags +
                ", formId=" + eventNotify.formId +
                ", messageId=" + eventNotify.messageId +
                ", messageType=" + eventNotify.messageType +
                ", notifyData=" +new String(eventNotify.notifyData) +
                '}';

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MyApp.getApp());

        mBuilder.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                .setDefaults(Notification.DEFAULT_SOUND)
                .setOngoing(false)//不是正在进行的   true为正在进行  效果和.flag一样
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentText(str);
        Notification notify = mBuilder.build();

        notify.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager mNotificationManager = (NotificationManager) MyApp.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(new Random().nextInt(), notify);
        XlinkUtils.longTips(str);
    }
}
