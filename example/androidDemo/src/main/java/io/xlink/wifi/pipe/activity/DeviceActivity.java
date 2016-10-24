package io.xlink.wifi.pipe.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.Header;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.MyApp;
import io.xlink.wifi.pipe.R;
import io.xlink.wifi.pipe.bean.Device;
import io.xlink.wifi.pipe.fragment.CmdActivity;
import io.xlink.wifi.pipe.fragment.DataPointFragment;
import io.xlink.wifi.pipe.fragment.LogFragment;
import io.xlink.wifi.pipe.http.HttpManage;
import io.xlink.wifi.pipe.manage.DeviceManage;
import io.xlink.wifi.pipe.util.SharedPreferencesUtil;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.bean.DataPoint;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.GetSubscribeKeyListener;
import io.xlink.wifi.sdk.listener.SendPipeListener;
import io.xlink.wifi.sdk.listener.SubscribeDeviceListener;
import io.xlink.wifi.sdk.util.MyLog;

/**
 * 设备界面
 *
 * @author Liuxy
 * @2015年4月25日下午5:14:07 </br>
 * @explain
 */
public class DeviceActivity extends FragmentActivity implements OnClickListener {
    // fgm
    private final String TAG = "DeviceActivity";

    // 当前操作的设备
    public Device device;
    private TextView headertitle;
    // 显示设备状态
    private ImageView device_state;
    // 界面是否可见
    private boolean isRun;
    private FragmentTabHost mTabHost;

    public List<DataPoint> dataPoints = new ArrayList<>();

    private Class fragmentArray[] = {LogFragment.class, DataPointFragment.class};
    private String mTextviewArray[] = {"透传模式", "数据端点"};
    private LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        // Log("onCreate");
        if (!MyApp.getApp().auth) {
            Log("非法启动");
            MyApp.getApp().auth = true;
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.device_main);

        initWidget();
    }

    // 连接设备对话框
    private ProgressDialog dialog;

    private void showConnectedDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = ProgressDialog.show(DeviceActivity.this, "连接设备", "正在连接设备...",
                true, true);
        dialog.setCanceledOnTouchOutside(false);
    }

    /**
     * 连接设备
     */

    public void connectDevice() {
        if (isOnline) {
            return;
        }
        showConnectedDialog();

        //V3版本获取SUBKEY
        if (device.getXDevice().getVersion() >= 3 && device.getXDevice().getSubKey() <= 0) {
            Log("get subkey:" + device.getXDevice().getMacAddress() + " " + device.getXDevice().getSubKey());
            XlinkAgent.getInstance().getInstance().getDeviceSubscribeKey(device.getXDevice(), device.getXDevice().getAccessKey(), new GetSubscribeKeyListener() {
                @Override
                public void onGetSubscribekey(XDevice xdevice, int code, int subKey) {
                    device.getXDevice().setSubKey(subKey);
                    DeviceManage.getInstance().updateDevice(device);
                }
            });
        }

        //订阅设备,V3版本设备开始使用subKey订阅设备。
        if (!device.isSubscribe()) {
            XlinkAgent.getInstance().subscribeDevice(device.getXDevice(), device.getXDevice().getSubKey(), new SubscribeDeviceListener() {
                @Override
                public void onSubscribeDevice(XDevice xdevice, int code) {
                    if (code == XlinkCode.SUCCEED) {
                        device.setSubscribe(true);
                    }
                }
            });
        }

//        int ret = XlinkAgent.getInstance().connectDevice(device.getXDevice(), device.getXDevice().getAccessKey(), connectDeviceListener);
        int ret = XlinkAgent.getInstance().connectDevice(device.getXDevice(), device.getXDevice().getAccessKey(), device.getXDevice().getSubKey(), connectDeviceListener);
        if (ret < 0) {// 调用设备失败
            if (dialog != null) {
                dialog.dismiss();
            }
            setDeviceStatus(false);
            switch (ret) {
                case XlinkCode.INVALID_DEVICE_ID:
                    XlinkUtils.shortTips("无效的设备ID，请先联网激活设备");
                    break;
                case XlinkCode.NO_CONNECT_SERVER:
                    XlinkUtils.shortTips("连接设备失败，手机未连接服务器");
                    if (XlinkUtils.isConnected()) {
                        int appid = SharedPreferencesUtil.queryIntValue("appId");
                        String authKey = SharedPreferencesUtil.queryValue("authKey", "");
                        XlinkAgent.getInstance().start();
                        XlinkAgent.getInstance().login(appid, authKey);
                    }
                    break;
                case XlinkCode.NETWORD_UNAVAILABLE:
                    XlinkUtils.shortTips("当前网络不可用,无法连接设备");
                    break;
                case XlinkCode.NO_DEVICE:
                    XlinkUtils.shortTips("未找到设备");
                    XlinkAgent.getInstance().initDevice(device.getXDevice());
                    break;
                // 重复调用了连接设备接口
                case XlinkCode.ALREADY_EXIST:
                    XlinkUtils.shortTips("重复调用");
                    break;
                default:
                    XlinkUtils.shortTips("连接设备" + device.getMacAddress() + "失败:" + ret);
                    break;
            }

        }
    }

    /**
     * 连接设备回调。该回调在主程序，可直接更改ui
     */
    private ConnectDeviceListener connectDeviceListener = new ConnectDeviceListener() {

        @Override
        public void onConnectDevice(XDevice xDevice, int result) {
            dialog.dismiss();
            // TODO: handle exception

            String tips;
            switch (result) {
                // 连接设备成功 设备处于内网
                case XlinkCode.DEVICE_STATE_LOCAL_LINK:
                    // 连接设备成功，成功后
                    setDeviceStatus(true);
                    DeviceManage.getInstance().updateDevice(xDevice);
                    tips = "正在局域网控制设备(" + xDevice.getMacAddress() + ")";
                    XlinkUtils.shortTips(tips);
                    Log(tips);
                    device_status_text.setText("内网");
                    XlinkAgent.getInstance().sendProbe(xDevice);
                    break;
                // 连接设备成功 设备处于云端
                case XlinkCode.DEVICE_STATE_OUTER_LINK:
                    setDeviceStatus(true);
                    device_status_text.setText("云端");
                    DeviceManage.getInstance().updateDevice(xDevice);
                    tips = "正在通过云端控制设备(" + xDevice.getMacAddress() + ")";
                    XlinkUtils.shortTips(tips);
                    DeviceManage.getInstance().addDevice(xDevice);
                    Log(tips);
                    break;
                // 设备授权码错误
                case XlinkCode.CONNECT_DEVICE_INVALID_KEY:
                    setDeviceStatus(false);
                    openDevicePassword();
                    Log.e(TAG, "Device:" + xDevice.getMacAddress() + "设备认证失败");
                    XlinkUtils.shortTips("设备认证失败");
                    break;
                // 设备不在线
                case XlinkCode.CONNECT_DEVICE_OFFLINE:
                    setDeviceStatus(false);
                    // Log.e(TAG, "Device:" + xDevice.getMacAddress() + "设备不在线");
                    XlinkUtils.shortTips("设备不在线");
                    Log("设备不在线");
                    break;

                // 连接设备超时了，（设备未应答，或者服务器未应答）
                case XlinkCode.CONNECT_DEVICE_TIMEOUT:
                    setDeviceStatus(false);
                    // Log.e(TAG, "Device:" + xDevice.getMacAddress() + "连接设备超时");
                    XlinkUtils.shortTips("连接设备超时");
                    break;

                case XlinkCode.CONNECT_DEVICE_SERVER_ERROR:
                    setDeviceStatus(false);
                    XlinkUtils.shortTips("连接设备失败，服务器内部错误");

                    break;
                case XlinkCode.CONNECT_DEVICE_OFFLINE_NO_LOGIN:
                    setDeviceStatus(false);
                    XlinkUtils.shortTips("连接设备失败，设备未在局域网内，且当前手机只有局域网环境");

                    break;
                default:
                    setDeviceStatus(false);
                    XlinkUtils.shortTips("连接设备失败，其他错误码:" + result);
                    break;
            }

        }
    };

    /**
     * 设置设备状态
     *
     * @param iso
     */
    private void setDeviceStatus(boolean iso) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        this.isOnline = iso;
        if (isOnline) {
            if (device.getXDevice().isLAN()) {
                device_status_text.setText("内网");
            } else {
                device_status_text.setText("云端");
            }
            device_state.setImageResource(R.drawable.icon_green);
        } else {
            device_status_text.setText("离线");
            device_state.setImageResource(R.drawable.icon_gray);
        }

    }


    public void openCmdFg() {
        startActivity(new Intent(this, CmdActivity.class));
    }

    private boolean isOnline;
    private View device_status_layout, title_save;
    private TextView device_status_text;


    private boolean isRegisterBroadcast = false;
    byte[] pipeData;
    /**
     * 监听的广播
     */
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            String mac = intent.getStringExtra(Constant.DEVICE_MAC);

            if (action.equals(Constant.BROADCAST_SEND_DATA)) {
                byte[] data = intent.getByteArrayExtra(Constant.DATA);
                sendData(data, null);
                return;
            }

            if (mac == null
                    || !mac.equals(DeviceActivity.this.device.getMacAddress())) {
                return;
            }
            // 收到pipe包
            if (action.equals(Constant.BROADCAST_RECVPIPE)) {
                byte[] data = intent.getByteArrayExtra(Constant.DATA);

                Log("收到数据：" + XlinkUtils.getHexBinString(data));
                pipeData = data;
                if (!isRun) { // 当界面不可见，把pipeData存起来，然后等onResume再更新界面
                    return;
                }

            } else if (action.equals(Constant.BROADCAST_RECVPIPE_SYNC)) {
                byte[] data = intent.getByteArrayExtra(Constant.DATA);
                Log("收到SYNC数据：" + XlinkUtils.getHexBinString(data));
                pipeData = data;
            } else if (action.equals(Constant.BROADCAST_DEVICE_CHANGED)) {
                int status = intent.getIntExtra(Constant.STATUS, -1);
                if (status == XlinkCode.DEVICE_CHANGED_CONNECTING) {
                    Log("正在重连设备...");
                    // if (dialog != null && !dialog.isShowing()) {
                    // showConnectedDialog();
                    // }
                } else if (status == XlinkCode.DEVICE_CHANGED_CONNECT_SUCCEED) {
                    setDeviceStatus(true);
                    Log("连接设备成功");
                    XlinkUtils.shortTips("连接设备成功");
                    dialog.dismiss();
                } else if (status == XlinkCode.DEVICE_CHANGED_OFFLINE) {
                    setDeviceStatus(false);
                    dialog.dismiss();
                    XlinkUtils.shortTips("连接设备失败");
                    Log("连接设备失败");
                }

            } // sync
            else if (action.equals(Constant.BROADCAST_DEVICE_SYNC)) {

            } else if (action.equals(Constant.BROADCAST_EXIT)) {
                finish();
            } else if (action.equals(Constant.BROADCAST_DATAPOINT_RECV)) {
                List<DataPoint> dps = (List<DataPoint>) intent.getSerializableExtra(Constant.DATA);
                for (DataPoint dp : dps) {
                    if (dataPoints.contains(dp)) {
                        int index = dataPoints.indexOf(dp);
                        DataPoint dataPoint = dataPoints.get(dataPoints.indexOf(dp));
                        if (dataPoint != null) {
                            dataPoint.setValue(dp.getValue());
                            dataPoints.remove(index);
                            dataPoints.add(index, dataPoint);
                        }
                    }
                }
                intent = new Intent(Constant.BROADCAST_DEVICE_DATAPOINT_RECV);
                intent.putExtra(Constant.DATA, (Serializable) dataPoints);
                sendBroadcast(intent);
            }
        }
    };
    public int ledcode = -1;
    public boolean switch_;
    public int wind = -1;
    public int temp = -1;
    public int humidity = -1;

//    public Dialog createProgressDialog(String title, String tips) {
//        ProgressDialog progressDialog = CustomDialog.createProgressDialog(this,
//                title, tips);
//        return progressDialog;
//    }
//
//    public CustomDialog createTipsDialog(String title, String tips) {
//        CustomDialog dialog = CustomDialog.createErrorDialog(this, title, tips,
//                null);
//        dialog.show();
//        return dialog;
//    }

    private void back() {
        DeviceListActivity.isAuthConnctDevice = false;
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                back();

                // startActivity(new Intent(this, DeviceListActivity.class));
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    static String logtext_save;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisterBroadcast) {
            unregisterReceiver(mBroadcastReceiver);
        }
        // mPopupWindow = null;
        Log("onDestroy");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Log("-----onNewIntent()");
    }

    public boolean sendData(final byte[] bs, String name) {
        if (!isOnline) {
            connectDevice();
            return false;
        }

        int ret = XlinkAgent.getInstance().sendPipeData(device.getXDevice(), bs, pipeListener);
        if (ret < 0) {
            setDeviceStatus(false);
            switch (ret) {
                case XlinkCode.NO_CONNECT_SERVER:
                    XlinkUtils.shortTips("发送数据失败，手机未连接服务器");
                    Log("发送数据失败，手机未连接服务器");
                    break;
                case XlinkCode.NETWORD_UNAVAILABLE:
                    XlinkUtils.shortTips("当前网络不可用,发送数据失败");
                    Log("当前网络不可用,发送数据失败");
                    break;
                case XlinkCode.NO_DEVICE:
                    XlinkUtils.shortTips("未找到设备");
                    Log("未找到设备");
                    XlinkAgent.getInstance().initDevice(device.getXDevice());
                    break;
                default:
                    XlinkUtils.shortTips("发送数据失败，错误码：" + ret);
                    Log("发送数据失败，错误码：" + ret);
                    break;
            }

            return false;
        } else {
            if (name != null) {
                Log("发送数据,msgId:" + ret + " data:(" + name + ")"
                        + XlinkUtils.getHexBinString(bs));
            } else {
                Log("发送数据,msgId:" + ret + " data:"
                        + XlinkUtils.getHexBinString(bs));
            }
        }
        return true;
    }

    private SendPipeListener pipeListener = new SendPipeListener() {

        @Override
        public void onSendLocalPipeData(XDevice device, int code, int messageId) {
            // setDeviceStatus(false);
            switch (code) {
                case XlinkCode.SUCCEED:
                    if (!isOnline) {
                        setDeviceStatus(true);
                    }

                    Log("发送数据,msgId:" + messageId + "成功");
                    break;
                case XlinkCode.TIMEOUT:
                    // 重新调用connect
                    Log("发送数据,msgId:" + messageId + "超时");
                    // XlinkUtils.shortTips("发送数据超时："
                    // + );

                    break;
                case XlinkCode.SERVER_CODE_UNAUTHORIZED:
                    XlinkUtils.shortTips("控制设备失败,当前帐号未订阅此设备，请重新订阅");
                    connectDevice();
                    break;
                case XlinkCode.SERVER_DEVICE_OFFLIEN:
                    Log("设备不在线");
                    XlinkUtils.shortTips("设备不在线");
                    setDeviceStatus(false);
                    break;
                default:
                    XlinkUtils.shortTips("控制设备其他错误码:" + code);
                    Log("控制设备其他错误码:" + code);
                    setDeviceStatus(false);
                    break;
            }

        }
    };

    private void Log(String msg) {
        Intent intent = new Intent(Constant.BROADCAST_LOG_ACTION);
        intent.putExtra(Constant.DATA, msg);
        sendBroadcast(intent);
        MyLog.e(TAG, msg);
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // Log("onResume");
        MyApp.getApp().setCurrentActivity(this);
        if (device.getAccessKey() > 0) {
            if (!isOnline && isAuthConnect) {
                connectDevice();
            }
        }
        isRun = true;
    }

    boolean isAuthConnect = true;// 是否自动登录

    @Override
    protected void onPause() {
        super.onPause();
        isRun = false;
        // Log("onPause");
    }

    Fragment currentFr;

    /**
     * 切换fragment内容呈现
     *
     * @param fragment
     * @param tag
     */
    public void replaceFragment(Fragment fragment, String tag) {

        Log.e(TAG, "切换fragment " + tag);
        if (fragment == currentFr) {
            return;
        }
        FragmentTransaction mFragmentTransaction;
        mFragmentTransaction = getSupportFragmentManager().beginTransaction();
//        mFragmentTransaction.setCustomAnimations(
//                R.animator.fragment_slide_left_enter,
//                R.animator.fragment_slide_right_exit);
        // android.R.animator.fade_in
        if (currentFr != null) {
            mFragmentTransaction.hide(currentFr);
        }
        if (fragment.isAdded()) {
            mFragmentTransaction.show(fragment);
        } else {
            mFragmentTransaction.add(R.id.frame_content, fragment, tag);
        }
        currentFr = fragment;
        mFragmentTransaction.commitAllowingStateLoss();
    }


    public void initWidget() {
        // TODO Auto-generated method stub
        device_status_text = (TextView) findViewById(R.id.device_status_text);
        title_save = findViewById(R.id.title_save);
        device_status_layout = findViewById(R.id.device_status_layout);
        device_state = (ImageView) findViewById(R.id.device_status);
        findViewById(R.id.title_save).setOnClickListener(this);
        findViewById(R.id.header_back_ll).setOnClickListener(this);
        headertitle = (TextView) findViewById(R.id.header_title);
        Bundle b = this.getIntent().getExtras();
        // 获取从设备列表
        device = DeviceManage.getInstance().getDevice(
                b.getString(Constant.DEVICE_MAC));
        XlinkAgent.getInstance().initDevice(device.getXDevice());

        headertitle.setText(device.getMacAddress());

        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        //测试使用. 实际不需要每次都订阅
//        XlinkAgent.getInstance().subscribeDevice(device.getXDevice(), device.getAccessKey(), new SubscribeDeviceListener() {
//            @Override
//            public void onSubscribeDevice(XDevice device, int code) {
//
//            }
//        });
        /**
         * 监听广播
         */
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constant.BROADCAST_RECVPIPE);
        myIntentFilter.addAction(Constant.BROADCAST_DEVICE_CHANGED);
        myIntentFilter.addAction(Constant.BROADCAST_DEVICE_SYNC);
        myIntentFilter.addAction(Constant.BROADCAST_RECVPIPE_SYNC);
        myIntentFilter.addAction(Constant.BROADCAST_SEND_DATA);
        myIntentFilter.addAction(Constant.BROADCAST_DATAPOINT_RECV);
        isRegisterBroadcast = true;
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
        // 打开数据透传主界面
//            openLogFg();

        // 实例化布局对象
        layoutInflater = LayoutInflater.from(this);
        // 实例化TabHost对象，得到TabHost

        mTabHost.setup(this, getSupportFragmentManager(), R.id.frame_content);
        mTabHost.getTabWidget().setDividerDrawable(null);
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
                                             @Override
                                             public void onTabChanged(String tabId) {

                                             }
                                         }
        );
        // 得到fragment的个数
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);//
        }

        if (device.getAccessKey() <= 0) {// 无密码或者 未初始化
            openDevicePassword();
        }

        HttpManage.getInstance().getDatapoints(device.getXDevice().getProductId(), new HttpManage.ResultCallback<List<Map<String, Object>>>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {

            }

            /**
             *
             BYTE(0),
             SHORT(1),
             USHORT(2),
             INT(3),
             UINT(4),
             LONG(5),
             ULONG(6),
             FLOAT(7),
             DOUBLE(8),
             STRING(9);


             1 	布尔类型
             2 	单字节
             3 	16位短整型
             4 	32位整型
             5 	浮点
             6 	字符串
             * @param code
             * @param response
             */
            @Override
            public void onSuccess(int code, List<Map<String, Object>> response) {
                for (Map<String, Object> map : response) {
                    int index = ((Double) map.get("index")).intValue();
                    int type = ((Double) map.get("type")).intValue();
                    DataPoint dataPoint = new DataPoint(index, type);
                    dataPoint.setDescription((String) map.get("description"));
                    dataPoint.setName((String) map.get("name"));
                    dataPoint.setSymbol((String) map.get("symbol"));
                    dataPoints.add(dataPoint);
                }
                Intent intent = new Intent(Constant.BROADCAST_DEVICE_DATAPOINT_RECV);
                intent.putExtra(Constant.DATA, (Serializable) dataPoints);
                sendBroadcast(intent);
            }
        });
    }


    int requestCode = 1;

    private void openDevicePassword() {
        Intent intent = new Intent(this, DevicePasswordActivity.class);
        intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == DevicePasswordActivity.resultCode) {
            Device device = (Device) data.getSerializableExtra(Constant.DATA);
            DeviceActivity.this.device = device;
            if (!isOnline && isAuthConnect) {
                connectDevice();
            }
        } else {
            if (requestCode == this.requestCode) {
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            // 回退键
            case R.id.header_back_ll:
                back();
                // startActivity(new Intent(this, DeviceListActivity.class));
//                XlinkAgent.getInstance().sendProbe(device.getXDevice());
                break;
            default:
                break;
        }
    }


    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);
        return view;
    }

}
