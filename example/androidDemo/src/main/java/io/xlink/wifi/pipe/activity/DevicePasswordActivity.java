package io.xlink.wifi.pipe.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.R;
import io.xlink.wifi.pipe.bean.Device;
import io.xlink.wifi.pipe.manage.DeviceManage;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.pipe.view.CustomDialog;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.SetDeviceAccessKeyListener;
import io.xlink.wifi.sdk.util.MyLog;

public class DevicePasswordActivity extends BaseActivity implements
        OnClickListener {

    public Device device;
    private EditText et_device_password;
    private Button b_device_password_next;
    private TextView tv_device_password_title;
    private TextView tv_device_password_content;
    public static int resultCode=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xlink_device_password_fragment);
        initWidget();
    }

    private Dialog progressDialog;

    @Override
    public void initWidget() {
        et_device_password = (EditText) findViewById(R.id.et_device_password);
        b_device_password_next = (Button) findViewById(R.id.b_device_password_next);
        tv_device_password_title = (TextView) findViewById(R.id.tv_device_password_title);
        tv_device_password_content = (TextView) findViewById(R.id.tv_device_password_content);

        String mac = getIntent().getStringExtra(Constant.DEVICE_MAC);
        if (mac == null || mac.equals("")) {
            tv_device_password_title.setText("参数错误");
            return;
        }
        device = DeviceManage.getInstance().getDevice(mac);
        if (device == null) {
            tv_device_password_title.setText("参数错误");
            return;
        }
        // 是否初始化
        if (device.getAccessKey() > 0) {
            tv_device_password_title.setText("请输入密码");
            tv_device_password_content.setText("设备已有授权码,请输入设备的授权码");

        } else {
            tv_device_password_title.setText("请输入设备初始化授权码");
            tv_device_password_content.setText("为设备创建访问授权码，密码小于999999999，请牢记您的密码。");
        }

        b_device_password_next.setOnClickListener(this);
    }

    @Override
    public void onClickListener(View v) {
        int id = v.getId();

        if (id == R.id.b_device_password_next) {
            String password = et_device_password.getText().toString().trim();
            int accessKey = -1;
            try {
                accessKey = Integer.parseInt(password);
            } catch (NumberFormatException e) {
                XlinkUtils.shortTips("请输入整型的设备密码，密码小于999999999");
                return;
            }
            if ((accessKey > 999999999) || (accessKey <= 0)) {
                XlinkUtils.shortTips("请输入设备密码，密码大于0.小于999999999");
                return;
            }

            String title = "";
            String msg = "";
            String tips = "";
            int ret = setDevicePassword(accessKey);
            if (ret < 0) {
                tips = device.getXDevice().isInit() ? "认证设备失败" + ret : "设置初始密码失败" + ret;
                XlinkUtils.shortTips(tips);
                return;
            } else {
                if (device.getXDevice().isInit()) {
                    title = "认证设备 msgID" + ret;
                    msg = "正在认证设备授权码,请稍后...";
                } else {
                    title = "设置初始授权码msgID" + ret;
                    msg = "正在设置初始授权码,请稍后...";
                }
            }
            progressDialog = createProgressDialog(title, msg);
        }
    }

    public Dialog createProgressDialog(String title, String tips) {
        ProgressDialog progressDialog = CustomDialog.createProgressDialog(this,
                title, tips);
        return progressDialog;
    }

    public CustomDialog createTipsDialog(String title, String tips) {
        CustomDialog dialog = CustomDialog.createErrorDialog(this, title, tips,
                null);
        dialog.show();
        return dialog;
    }

    private void linkDeviceError(String msg) {
        try {
            CustomDialog dialog = createTipsDialog("设备认证", msg);
            dialog.show();
        } catch (NullPointerException e) {
            XlinkUtils.shortTips(msg);
        }

    }

    private int setDevicePassword(final int accessKey) {
        device.setAccessKey(accessKey);
        DeviceManage.getInstance().addDevice(device);
        int ret = XlinkAgent.getInstance().setDeviceAccessKey(device.getXDevice(), accessKey,
                new SetDeviceAccessKeyListener() {
                    @Override
                    public void onSetLocalDeviceAccessKey(XDevice xdevice, int code, int msgId) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        switch (code) {
                            case XlinkCode.SUCCEED:
                                SUCCEED(xdevice, accessKey);
                                break;
                            default:
                                fail(code);
                                break;
                        }
                        Log("设置默认密码:" + code);
                    }
                });

        return ret;
    }

    private void Log(String msg) {
        Intent intent = new Intent(Constant.BROADCAST_LOG_ACTION);
        intent.putExtra(Constant.DATA, msg);
        sendBroadcast(intent);
        MyLog.e("DevicePassword", msg);
    }

    private void SUCCEED(XDevice xd, int accesssKey) {
        XlinkUtils.shortTips("认证设备成功");
        Intent intent = new Intent();
        intent.putExtra(Constant.DATA, device);
        setResult(resultCode, intent);
        finish();
    }

    private void fail(int code) {
        String tips = "";
        String strCode;
        switch (code) {
            case XlinkCode.SERVER_CODE_INVALID_KEY:
                strCode = "密码错误";
                break;
            case XlinkCode.SERVER_DEVICE_OFFLIEN:
                strCode = "设备不在线";
                break;
            default:
                strCode = "错误码：" + code;
                break;
        }
        if (device.getXDevice().isInit()) {
            tips = "设备认证失败," + strCode;

        } else {
            tips = "设置初始密码失败,：" + strCode;
        }

        linkDeviceError(tips);
    }


}
