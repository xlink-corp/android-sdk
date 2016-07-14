package io.xlink.wifi.pipe.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.NumberKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Map;

import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.MyApp;
import io.xlink.wifi.pipe.R;
import io.xlink.wifi.pipe.adapter.DeviceListAdapter;
import io.xlink.wifi.pipe.bean.Device;
import io.xlink.wifi.pipe.http.HttpManage;
import io.xlink.wifi.pipe.manage.DeviceManage;
import io.xlink.wifi.pipe.util.SharedPreferencesUtil;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.pipe.view.ClearableEditText;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.ScanDeviceListener;
import io.xlink.wifi.sdk.util.MyLog;

/**
 * 设备列表界面
 *
 * @author Liuxy
 * @2015年5月8日上午9:23:58 </br>
 * @explain
 */
public class DeviceListActivity extends BaseActivity implements
        OnItemClickListener {
    private ListView device_listview;
    private Button scan_btn;
    private DeviceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        MyApp.getApp().auth = true;
        if (!XlinkAgent.getInstance().isConnectedLocal()) {
            XlinkAgent.getInstance().start();
        }
        if (!XlinkAgent.getInstance().isConnectedOuterNet()) {
            XlinkAgent.getInstance().login(MyApp.getApp().getAppid(), MyApp.getApp().getAuth());
        }

        Log("---------onCreate ");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.act_devicelist);
        initWidget();
        //重新打开APP需要重新调用接口获取Token
        loginUser(SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID), SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWORD_ID));
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Log("---------onRestart ");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log("---------onResume ");
        if (MyApp.getApp().appid == 0 || MyApp.getApp().authKey.equals("")) {
            DeviceListActivity.this.finish();
            startActivity(new Intent(DeviceListActivity.this,
                    AuthActivity.class));
        }
        updataDeviceListView(DeviceManage.getInstance().getDevices());
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log("---------onPause ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log("---------onDestroy ");
    }

    private final String TAG = "DeviceListActivity";

    public void Log(String msg) {
        MyLog.e(TAG, msg);
    }

    public static boolean isAuthConnctDevice;
    boolean isF;

    @Override
    public void initWidget() {
        findViewById(R.id.header_back_ll).setOnClickListener(this);
        device_listview = (ListView) findViewById(R.id.device_list);
        scan_btn = (Button) findViewById(R.id.btn_scan);
        findViewById(R.id.more_showpop).setOnClickListener(this);
        scan_btn.setOnClickListener(this);
        updataDeviceListView(DeviceManage.getInstance().getDevices());
        device_listview.setOnItemClickListener(this);
        // if (devices.size() == 1) {
        // if (isAuthConnctDevice) {
        // toControlDeviceActivity(devices.get(0));
        // }
        // }
        device_listview
                .setOnItemLongClickListener(new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, final int position, long id) {
                        // 弹出警告框

                        AlertDialog.Builder builder = new Builder(
                                DeviceListActivity.this);
                        builder.setCancelable(false);
                        builder.setMessage("确定删除此设备吗？");
                        builder.setTitle("提示");
                        builder.setNegativeButton(
                                "确定",
                                new android.content.DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        //使用HTTP接口解除订阅关系
                                        HttpManage.getInstance().unsubscribe(MyApp.getApp().getAppid(), devices.get(position).getXDevice().getDeviceId(), new HttpManage.ResultCallback<String>() {
                                            @Override
                                            public void onError(Header[] headers, HttpManage.Error error) {
                                                //TODO 错误处理， 这里为了方便直接删除
                                                DeviceManage.getInstance().removeDevice(devices.get(position).getXDevice().getMacAddress());
                                                updataDeviceListView(DeviceManage.getInstance().getDevices());
                                                XlinkUtils.shortTips("删除设备失败："+error.getMsg());
                                            }

                                            @Override
                                            public void onSuccess(int code, String response) {
                                                DeviceManage.getInstance().removeDevice(devices.get(position).getXDevice().getMacAddress());
                                                updataDeviceListView(DeviceManage.getInstance().getDevices());
                                                XlinkUtils.shortTips("删除设备成功");
                                            }
                                        });
                                    }
                                });
                        builder.setNeutralButton("取消", null);
                        builder.create().show();

                        return true;
                    }
                });
    }

    private ArrayList<Device> devices;

    private void updataDeviceListView(ArrayList<Device> devices) {
        this.devices = devices;
        if (adapter == null) {
            adapter = new DeviceListAdapter(this, this.devices);
            device_listview.setAdapter(adapter);
        } else {
            adapter.setDevices(devices);
            adapter.notifyDataSetChanged();
        }

    }

    private ScanDeviceListener scanListener = new ScanDeviceListener() {

        @Override
        public void onGotDeviceByScan(XDevice device) {
            XlinkUtils.shortTips("扫描到设备:" + device.getMacAddress());
            final Device dev = new Device(device);
//            if (device.getAccessKey() < 0) {
//                final int key = 654321;
//                XlinkAgent.getInstance().setDeviceAccessKey(device, key, new SetDeviceAccessKeyListener() {
//                    @Override
//                    public void onSetLocalDeviceAccessKey(XDevice device, int code, int messageId) {
//                        Log("设置AccessKey:" + code);
//                        switch (code) {
//                            case XlinkCode.SUCCEED:
//                                dev.setAccessKey(key);
//                                break;
//                        }
//                    }
//                });
//            } else {
//                dev.setAccessKey(device.getAccessKey());
//            }
            if (device.getAccessKey() > 0) {
                dev.setAccessKey(device.getAccessKey());
            }
            DeviceManage.getInstance().addDevice(dev);
            // if (!device.isInit()) {
            // int ret = XlinkAgent.getInstance().setDeviceAuthorizeCode(
            // device, "0000", Constant.passwrod,
            // new SetDeviceAuthorizeListener() {
            // @Override
            // public void onSetLocalDeviceAuthorizeCode(
            // XDevice device, int code,int msgId) {
            // Log("设置设备" + device.getMacAddress() + "默认密码:"
            // + code);
            // }
            // });
            // if (ret != 0) {
            // XlinkUtils.shortTips("设置" + device.getMacAddress()
            // + "密码失败,同步错误码：" + ret);
            // }
            // }
            updataDeviceListView(DeviceManage.getInstance().getDevices());
        }
    };

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        Log("----onNewIntent()");
        MyApp.getApp().auth = true;
    }

    ;


    @Override
    public void onClickListener(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:

                int ret = XlinkAgent.getInstance().scanDeviceByProductId(
                        Constant.PRODUCTID, scanListener);
                if (ret < 0) {
                    switch (ret) {
                        case XlinkCode.NO_CONNECT_SERVER:
                            XlinkUtils.shortTips("未开启局域网服务");
                            if (XlinkUtils.isWifi()) {
                                XlinkAgent.getInstance().start();
                            }
                            break;
                        case XlinkCode.NETWORD_UNAVAILABLE:
                            XlinkUtils.shortTips("手机无网络/wifi环境");
                            break;
                        default:
                            XlinkUtils.shortTips("调用扫描失败:" + ret);
                            break;
                    }
                    return;
                } else {
                    count = 0;
                    mHandler.post(runnable);
                }
                break;
            case R.id.header_back_ll:
                showDialog("退出Demo程序吗？");
                break;
            case R.id.more_showpop:

                initPopupWindow();
                mPopupWindow.showAsDropDown(v, -300, 0);
                break;
            case R.id.set_pid:
                mPopupWindow.dismiss();
                setPid();
                break;
            case R.id.set_accesskey:
                mPopupWindow.dismiss();
                setAccess();
                break;
            case R.id.about:
                mPopupWindow.dismiss();
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.clear_devicelist:
                mPopupWindow.dismiss();
                DeviceManage.getInstance().clearAllDevice();
                adapter.setDevices(DeviceManage.getInstance().getDevices());
                adapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private void test(XDevice xDevice) {
        int ret = XlinkAgent.getInstance().connectDevice(xDevice, "8888",
                connectDeviceListener);
        XlinkUtils.shortTips("ret:" + ret);
    }

    private ConnectDeviceListener connectDeviceListener = new ConnectDeviceListener() {

        @Override
        public void onConnectDevice(XDevice xDevice, int result) {
            // TODO Auto-generated method stub

            XlinkUtils.shortTips("onConnectDevice result :" + result
                    + "xDevice:" + xDevice);

        }
    };

    void setAccess() {
        View view = LayoutInflater.from(this).inflate(R.layout.set_accessid,
                null);
        final EditText edit_company_id = (EditText) view.findViewById(R.id.edit_company_id);
        final EditText edit_email = (EditText) view.findViewById(R.id.edit_email);
        final EditText edit_passwd = (EditText) view.findViewById(R.id.edit_passwd);
        edit_company_id.setText(HttpManage.COMPANY_ID);
        edit_passwd.setText(SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWORD_ID));
        edit_email.setText(SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID));
        new AlertDialog.Builder(this).setView(view)
                .setNegativeButton("保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String compayId = edit_company_id.getText().toString().trim();
                        String mail = edit_email.getText().toString().trim();
                        String passwd = edit_passwd.getText().toString().trim();
                        HttpManage.COMPANY_ID = compayId;
                        SharedPreferencesUtil.keepShared(Constant.SAVE_COMPANY_ID, compayId);
                        SharedPreferencesUtil.keepShared(Constant.SAVE_EMAIL_ID, mail);
                        SharedPreferencesUtil.keepShared(Constant.SAVE_PASSWORD_ID, passwd);
                        XlinkUtils.shortTips("修改成功，正在重启..");
                        MyApp.getApp().appid = 0;
                        MyApp.getApp().authKey = "";

                        SharedPreferencesUtil.deleteValue(Constant.SAVE_appId);
                        SharedPreferencesUtil.deleteValue(Constant.SAVE_authKey);
                        XlinkAgent.getInstance().stop();
                        DeviceListActivity.this.finish();
                        startActivity(new Intent(DeviceListActivity.this,
                                AuthActivity.class));
                    }
                }).setNeutralButton("取消", null).show();

    }

    public static NumberKeyListener numberKeyListener = new NumberKeyListener() {

        @Override
        public int getInputType() {
            // TODO Auto-generated method stub
            return android.text.InputType.TYPE_CLASS_PHONE;
        }

        @Override
        protected char[] getAcceptedChars() {
            char[] f = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', 'a',
                    'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'i', 'm',
                    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
                    'z'};
            return f;
        }
    };

    void setPid() {
        View view = LayoutInflater.from(this).inflate(R.layout.set_pid, null);
        // edit_pid
        final ClearableEditText editText = (ClearableEditText) view.findViewById(R.id.edit_pid);
        editText.setKeyListener(numberKeyListener);
        editText.setText(Constant.PRODUCTID);
        new AlertDialog.Builder(this).setView(view)
                .setNeutralButton("取消", null)
                .setNegativeButton("保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String pid = editText.getText().toString();
                        pid = pid.trim();
                        pid = pid.replace(" ", "");
                        if (pid.length() != 32) {
                            XlinkUtils.shortTips("pid格式不正确，pid为32位");
                            setPid();
                            return;
                        }
                        SharedPreferencesUtil.keepShared(Constant.SAVE_PRODUCTID, pid);
                        Constant.PRODUCTID = pid;
                        XlinkUtils.shortTips("保存成功");
                    }
                }).show();
    }

    private PopupWindow mPopupWindow;

    private void initPopupWindow() {
        if (mPopupWindow == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View myview = layoutInflater.inflate(R.layout.config_popup, null);
            mPopupWindow = new PopupWindow(myview, myview.getWidth(),
                    myview.getHeight());
            myview.findViewById(R.id.set_pid).setOnClickListener(this);
            myview.findViewById(R.id.set_accesskey).setOnClickListener(this);
            myview.findViewById(R.id.about).setOnClickListener(this);
            myview.findViewById(R.id.clear_devicelist).setOnClickListener(this);
            // myview.findViewById(R.id.close).setVisibility(View.GONE);
            mPopupWindow.setWindowLayoutMode(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setFocusable(true);

            // 设置点击弹框外部，弹框消失
            mPopupWindow.setOutsideTouchable(true);
            // 设置一个透明的背景，不然无法实现点击弹框外，弹框消失
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
            mPopupWindow.setTouchable(true);
        }
    }

    int count = 0;

    private void scanUi(String msg) {
        scan_btn.setText("正在扫描中" + msg);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                showDialog("退出Demo程序吗？");
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showDialog(String tips) {
        Builder builder = new Builder(this);
        builder.setCancelable(false);
        builder.setMessage(tips);
        builder.setTitle("提示");
        builder.setNegativeButton("确定",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        XlinkAgent.getInstance().stop();
                        finish();
                        System.exit(0);
                    }
                });
        builder.setNeutralButton("取消", null);
        builder.create().show();
    }

    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            count++;
            if (count < 7) {
                mHandler.postDelayed(runnable, 800);
            } else {
                scan_btn.setText("扫描本地设备");

                return;
            }
            switch (count) {
                case 1:
                    scanUi(".");
                    break;
                case 2:
                    scanUi("..");
                    break;
                case 3:
                    scanUi("...");
                    break;
                case 4:
                    scanUi(".");
                    break;
                case 5:
                    scanUi("..");
                    break;
                case 6:
                    scanUi("...");
                    break;
                default:
                    break;
            }
        }
    };
    private Handler mHandler = new Handler();

    private void toControlDeviceActivity(Device device) {

        Bundle b = new Bundle();
        b.putSerializable(Constant.DEVICE_MAC, device.getMacAddress());
        Intent i = new Intent();
        i.putExtras(b);
        i.setClass(this, DeviceActivity.class);
        // i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        // finish();
    }

    // private Device currentDevice;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//	test(devices.get(position).getXDevice());
        // test(devices.get(position).getXDevice());
        // currentDevice = devices.get(position);
        toControlDeviceActivity(devices.get(position));

    }


    public void loginUser(final String user, final String pwd) {
        HttpManage.getInstance().login(user, pwd, new HttpManage.ResultCallback<Map<String, Object>>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
            }

            @Override
            public void onSuccess(int code, Map<String, Object> response) {
                String authKey = (String) response.get("authorize");
                String accessToken = (String) response.get("access_token");
                int appid = ((Double) response.get("user_id")).intValue();
                SharedPreferencesUtil.keepShared("appId", appid);
                SharedPreferencesUtil.keepShared("authKey", authKey);
                MyApp.getApp().setAccessToken(accessToken);
                MyApp.getApp().setAppid(appid);
                MyApp.getApp().setAuth(authKey);
            }
        });
    }
}
