package io.xlink.wifi.pipe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.xlink.wifi.pipe.R;
import io.xlink.wifi.pipe.bean.Device;
import io.xlink.wifi.pipe.manage.DeviceManage;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.RenameDeviceListener;
import io.xlink.wifi.sdk.listener.SetDeviceAccessKeyListener;
import io.xlink.wifi.sdk.listener.SubscribeDeviceListener;

public class DeviceDetailInfoActivity extends Activity implements View.OnClickListener{

    public static final String BUNDLE_DEVICE="BUNDLE_DEVICE";

    private Device mDevice;

    private TextView header_title;
    private TextView mDeviceId;
    private TextView mDeviceName;
    private TextView mDeviceVersion;
    private TextView mMcuVersion;
    private TextView mMcuSoftVersion;
    private TextView mDevicePID;
    private TextView mDeviceSession;
    private TextView mDeviceIP;
    private TextView mDevicePort;
    private TextView mDeviceAccessKey;
    private TextView mDeviceSubKey;

    private Button BtnDeviceNameUpdate;
    private Button BtnDeviceAckUpdate;
    private Button mDeviceSubscribe;
    private Button mSetHandShakeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_device_detail_info);
        mDevice=(Device)getIntent().getSerializableExtra(BUNDLE_DEVICE);
        if(mDevice==null)finish();
        initView();
    }

    private void initView(){

        header_title=(TextView)findViewById(R.id.header_title);
        mDeviceId=(TextView)findViewById(R.id.mDeviceId);
        mDeviceName=(TextView)findViewById(R.id.mDeviceName);
        mDeviceVersion=(TextView)findViewById(R.id.mDeviceVersion);
        mMcuVersion=(TextView)findViewById(R.id.mMcuVersion);
        mMcuSoftVersion=(TextView)findViewById(R.id.mMcuSoftVersion);
        mDevicePID=(TextView)findViewById(R.id.mDevicePid);
        mDeviceSession=(TextView)findViewById(R.id.mDeviceSession);
        mDeviceIP=(TextView)findViewById(R.id.mDeviceIP);
        mDevicePort=(TextView)findViewById(R.id.mDevicePort);
        mDeviceAccessKey=(TextView)findViewById(R.id.mDeviceAccessKey);
        mDeviceSubKey=(TextView)findViewById(R.id.mDeviceSubKey);
        BtnDeviceNameUpdate=(Button)findViewById(R.id.BtnDeviceNameUpdate);
        BtnDeviceAckUpdate=(Button)findViewById(R.id.BtnDeviceAckUpdate);
        mDeviceSubscribe=(Button)findViewById(R.id.mDeviceSubscribe);
        mSetHandShakeTime=(Button)findViewById(R.id.mSetHandShakeTime);

        findViewById(R.id.header_back_ll).setOnClickListener(this);
        BtnDeviceAckUpdate.setOnClickListener(this);
        BtnDeviceNameUpdate.setOnClickListener(this);
        mDeviceSubscribe.setOnClickListener(this);
        mSetHandShakeTime.setOnClickListener(this);

        initData();

    }

    private void initData(){
        header_title.setText(mDevice.getXDevice().getMacAddress());
        mDeviceId.setText(mDevice.getXDevice().getDeviceId()+"");
        mDeviceName.setText(mDevice.getXDevice().getDeviceName());
        mDeviceVersion.setText("V"+mDevice.getXDevice().getVersion());
        mMcuVersion.setText("V"+mDevice.getXDevice().getMcuHardVersion());
        mMcuSoftVersion.setText("V"+mDevice.getXDevice().getMcuSoftVersion());
        mDevicePID.setText(mDevice.getXDevice().getProductId());
        mDeviceSession.setText(mDevice.getXDevice().getSessionId()+"");
        mDeviceIP.setText(mDevice.getXDevice().getAddress().getHostAddress());
        mDevicePort.setText(mDevice.getXDevice().getPort()+"");
        mDeviceAccessKey.setText(mDevice.getXDevice().getAccessKey()+"");
        mDeviceSubKey.setText(mDevice.getXDevice().getSubKey()+"");

    }

    @Override
    public void onClick(View view){
        switch (view.getId()){

            case R.id.header_back_ll:
                finish();
                break;
            case R.id.BtnDeviceNameUpdate:
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        DeviceDetailInfoActivity.this);
                        builder.setCancelable(false);
                        final EditText nameText=new EditText(DeviceDetailInfoActivity.this);
                        builder.setView(nameText);
                        builder.setTitle("重命名");
                        builder.setNegativeButton(
                                "确定",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                         int result= XlinkAgent.getInstance().renameDevice(mDevice.getXDevice(), nameText.getText().toString(), new RenameDeviceListener() {
                                             @Override
                                             public void onRenameResponse(XDevice device, int code) {
                                                 if(code== XlinkCode.SUCCEED){
                                                     device.setDeviceName(nameText.getText().toString());
                                                     Toast.makeText(DeviceDetailInfoActivity.this,"设备重命名成功",Toast.LENGTH_LONG).show();
                                                     mDeviceName.setText(nameText.getText().toString());
                                                     DeviceManage.getInstance().updateDevice(device);
                                                 }else{
                                                     Toast.makeText(DeviceDetailInfoActivity.this,"设备重命名失败,错误码->"+code,Toast.LENGTH_LONG).show();
                                                 }
                                             }
                                         });
                                        if(result<0){
                                            Toast.makeText(DeviceDetailInfoActivity.this,"设备重命名失败,错误码->"+result,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                        builder.setNeutralButton("取消", null);
                        builder.create().show();
                break;
            case R.id.BtnDeviceAckUpdate:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(
                        DeviceDetailInfoActivity.this);
                builder2.setCancelable(false);
                final EditText nameText1=new EditText(DeviceDetailInfoActivity.this);
                builder2.setView(nameText1);
                builder2.setTitle("设置AccessKey");
                builder2.setNegativeButton(
                        "确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                String password=nameText1.getText().toString();
                                int accessKey = -1;
                                try {
                                    accessKey = Integer.parseInt(password);
                                } catch (NumberFormatException e) {
                                    XlinkUtils.shortTips("请输入整型数字");
                                    return;
                                }
                                final int acKey=accessKey;

                                int result= XlinkAgent.getInstance().setDeviceAccessKey(mDevice.getXDevice(), accessKey, new SetDeviceAccessKeyListener() {

                                    @Override
                                    public void onSetLocalDeviceAccessKey(XDevice device, int code, int messageId) {
                                        if(code== XlinkCode.SUCCEED){
                                            device.setAccessKey(acKey);
                                            Toast.makeText(DeviceDetailInfoActivity.this,"设备accessKey设置成功",Toast.LENGTH_LONG).show();
                                            mDeviceAccessKey.setText(nameText1.getText().toString()+"");
                                            DeviceManage.getInstance().updateDevice(device);
                                        }else{
                                            Toast.makeText(DeviceDetailInfoActivity.this,"设备accessKey设置失败,错误码->"+code,Toast.LENGTH_LONG).show();
                                        }
                                    }

                                });
                                if(result<0){
                                    Toast.makeText(DeviceDetailInfoActivity.this,"设备accessKey设置失败,错误码->"+result,Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                builder2.setNeutralButton("取消", null);
                builder2.create().show();
                break;

            case R.id.mDeviceSubscribe:
                //订阅设备

                AlertDialog.Builder builder3 = new AlertDialog.Builder(
                        DeviceDetailInfoActivity.this);
                builder3.setCancelable(false);
                final EditText nameText3=new EditText(DeviceDetailInfoActivity.this);
                nameText3.setText(mDevice.getXDevice().getSubKey()+"");
                builder3.setView(nameText3);
                builder3.setTitle("订阅设备(输入subKey)");
                builder3.setNegativeButton(
                        "确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                String password=nameText3.getText().toString();
                                int subkey ;
                                try {
                                    subkey = Integer.parseInt(password);
                                } catch (NumberFormatException e) {
                                    XlinkUtils.shortTips("请输入整型数字");
                                    return;
                                }
                                final int sbKey=subkey;

                                int result=XlinkAgent.getInstance().subscribeDevice(mDevice.getXDevice(), sbKey, new SubscribeDeviceListener() {
                                    @Override
                                    public void onSubscribeDevice(XDevice device, int code) {
                                        if (code == XlinkCode.SUCCEED) {
                                            Toast.makeText(DeviceDetailInfoActivity.this,"设备订阅成功",Toast.LENGTH_LONG).show();
                                        }else{
                                            Toast.makeText(DeviceDetailInfoActivity.this,"设备订阅失败,错误码->"+code,Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                                if(result<0){
                                    Toast.makeText(DeviceDetailInfoActivity.this,"设备订阅失败,错误码->"+result,Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                builder3.setNeutralButton("取消", null);
                builder3.create().show();
                break;

            case R.id.mSetHandShakeTime:
                //设置handshake超时时间
                AlertDialog.Builder builder4 = new AlertDialog.Builder(
                        DeviceDetailInfoActivity.this);
                builder4.setCancelable(false);
                final EditText nameText4=new EditText(DeviceDetailInfoActivity.this);
                nameText4.setText("");
                builder4.setView(nameText4);
                builder4.setTitle("设置Device超时时间(不输入默认100)");
                builder4.setNegativeButton(
                        "确定",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                String password=nameText4.getText().toString();
                                int timeout ;
                                if(TextUtils.isEmpty(password)){
                                    timeout=2;
                                }else {
                                    try {
                                        timeout = Integer.parseInt(password);
                                    } catch (NumberFormatException e) {
                                        XlinkUtils.shortTips("请输入整型数字");
                                        return;
                                    }
                                }
                               int result=XlinkAgent.getInstance().setDeviceTimeOut(timeout);
                                if(result<0){
                                    Toast.makeText(DeviceDetailInfoActivity.this,"设置失败,错误码->"+result,Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(DeviceDetailInfoActivity.this,"设置成功(重连生效)",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                builder4.setNeutralButton("取消", null);
                builder4.create().show();
                break;

        }
    }
}
