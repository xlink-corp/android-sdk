package io.xlink.wifi.pipe;

public class Constant {

    // 透传产品ID
//    public static String PRODUCTID = "5249b7cd4b694726a7d953bd60ef3fc8";
//    public static String PRODUCTID = "160fa2ad399e9200160fa2ad399e9201";
    public static String PRODUCTID = "";

    // ------------启动监听
    public static final String PACKAGE_NAME = MyApp.getApp().getPackageName();
    public static final String BROADCAST_ON_START = PACKAGE_NAME + ".onStart"; //
    public static final String BROADCAST_ON_LOGIN = PACKAGE_NAME
	    + ".xlinkonLogin";

    public static final String BROADCAST_CLOUD_DISCONNECT = PACKAGE_NAME
	    + ".clouddisconnect";
    public static final String BROADCAST_LOCAL_DISCONNECT = PACKAGE_NAME
	    + ".localdisconnect";
    public static final String BROADCAST_RECVPIPE = PACKAGE_NAME + ".recv-pipe";
    public static final String BROADCAST_RECVPIPE_SYNC = PACKAGE_NAME + ".recv-pipe-sync";
    public static final String BROADCAST_DEVICE_CHANGED = PACKAGE_NAME + ".device-changed";
    public static final String BROADCAST_EVENT_NOTIFY = PACKAGE_NAME + ".event-notify";
    public static final String BROADCAST_LOG_ACTION = PACKAGE_NAME + ".Log_action";
    public static final String BROADCAST_SEND_DATA = PACKAGE_NAME + ".send_data";
    public static final String BROADCAST_DEVICE_DATAPOINT_RECV = PACKAGE_NAME + ".device_recv_datapoint";
    public static final String BROADCAST_DATAPOINT_RECV = PACKAGE_NAME + ".recv_datapoint";

    public static final String BROADCAST_DEVICE_SYNC = PACKAGE_NAME
	    + ".device-sync";
    public static final String BROADCAST_EXIT = PACKAGE_NAME + ".exit";
    public static final String BROADCAST_TIMER_UPDATE = PACKAGE_NAME
	    + "timer-update";
    public static final String BROADCAST_SOCKET_STATUS = PACKAGE_NAME
	    + "socket-status";
    // http 注册，获取appid回调
    public static final int HTTP_NETWORK_ERR = 1;

    // 数据包超时时间
    public static final int TIMEOUT = 10;// 设置请求超时时间

    public static final String DATA = "data";
    // public static final String DEVICE = "device";
    public static final String DEVICE_MAC = "device-mac";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String KEY = "key";

    public static final String SAVE_PRODUCTID = "pid";
    public static final String SAVE_COMPANY_ID= "COMPANY_ID";
    public static final String SAVE_EMAIL_ID= "EMAIL_ID";
    public static final String SAVE_PASSWORD_ID= "PASSWD_ID";
    public static final String SAVE_appId = "appId";
    public static final String SAVE_authKey = "authKey";
    public static final String IS_INIT = "isinit";
    public static final int TIMER_OFF = 0;
    public static final int TIMER_ON = 1;
    public static final int TIMER_BUFF_SIZE = 6;
    public static final int TIMER_MAX = 19;
}
