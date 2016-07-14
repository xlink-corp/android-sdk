package io.xlink.wifi.pipe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.Header;

import java.util.Map;

import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.MyApp;
import io.xlink.wifi.pipe.R;
import io.xlink.wifi.pipe.http.HttpManage;
import io.xlink.wifi.pipe.util.SharedPreferencesUtil;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.util.MyLog;

/**
 * 该activity主要用于注册;<br>
 * 然后登录获取appid ;<br>
 * 如果有appid 直接调用login start 然后到设备列表界面
 *
 * @author Liuxy
 * @2015年5月13日上午11:48:43 </br>
 * @explain
 */
public class AuthActivity extends BaseActivity {
    private String id;
    private int appid;
    private String authKey;
    private TextView tips_text;
    TelephonyManager tm;
    private boolean isRun;
    private final String TAG = "AuthActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp.getApp().auth = true;

        // SharedPreferencesUtil.q
        isRun = true;
        setContentView(R.layout.auth_activity);
        initWidget();
        XlinkAgent.getInstance().addXlinkListener(MyApp.getApp());
        tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        tips_text = (TextView) findViewById(R.id.tips);
        appid = SharedPreferencesUtil.queryIntValue("appId");
        authKey = SharedPreferencesUtil.queryValue("authKey", "");
        // 是否有appid
        if (isHaveAppid()) {
            openDeviceListActivity();
        } else {
            registerUser();

        }

    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        Log("---------onRestart ");
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log("---------onPause ");
    }

    /**
     * 打开设备列表页
     */
    private void openDeviceListActivity() {

        if (isRun) {// 防止重复打开2个DeviceListActivity界面
            if (!XlinkAgent.getInstance().isConnectedLocal()) {
                XlinkAgent.getInstance().start();
            }
            if (!XlinkAgent.getInstance().isConnectedOuterNet()) {
                // XlinkAgent.getInstance().login(MyApp.getApp().loginUser(),
                // MyApp.getApp().getAuth());
            }
            MyApp.getApp().setAppid(appid);
            MyApp.getApp().setAuth(authKey);
            isRun = false;
            DeviceListActivity.isAuthConnctDevice = true;
            // openActivity(MainActivity.class);
            finish();
            Intent intent = new Intent(this, DeviceListActivity.class);
            startActivity(intent);
            // openActivity(DeviceListActivity.class);
        }
    }

    public boolean isHaveAppid() {
        if (appid == 0 || authKey.equals("")) {
            return false;
        }
        return true;
    }

    public void setTips(String msg) {
        tips_text.setText(msg);
    }

    public void Log(String msg) {
        MyLog.e(TAG, msg);
    }

    /**
     * 注册帐号
     */
    public void registerUser() {
        // 获取imei号
        id = XlinkUtils.MD5(tm.getDeviceId());
        setTips("正在自动注册用户...");
        registerUserByMail(SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID), id, SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWORD_ID));
    }

    public void registerUserByMail(final String uid, final String name, final String pwd) {
        HttpManage.getInstance().registerUserByMail(uid, name, pwd, new HttpManage.ResultCallback<String>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                loginUser(uid, pwd);
                setAccess("请输入正确的企业ID 邮箱和密码"+ error.getMsg());
				tips_text.setText("registerUser fail msg: " + error.getMsg());
            }

            @Override
            public void onSuccess(int code, String response) {
                loginUser(uid, pwd);
            }
        });
    }

    public void loginUser(final String user, final String pwd) {
        HttpManage.getInstance().login(user, pwd, new HttpManage.ResultCallback<Map<String, Object>>() {
            @Override
            public void onError(Header[] headers, HttpManage.Error error) {
                setAccess("登入失败,请确认账号已激活:"+ error.getMsg());
                tips_text.setText("registerUser fail msg: " + error.getMsg());
            }

            @Override
            public void onSuccess(int code, Map<String, Object> response) {
                String authKey = (String) response.get("authorize");
                String accessToken = (String) response.get("access_token");
                int appid = ((Double)response.get("user_id")).intValue();
                SharedPreferencesUtil.keepShared("appId", appid);
                SharedPreferencesUtil.keepShared("authKey", authKey);
                MyApp.getApp().setAccessToken(accessToken);
                MyApp.getApp().setAppid(appid);
                MyApp.getApp().setAuth(authKey);
                openDeviceListActivity();
                finish();
            }
        });
    }

    void setAccess(String msg) {
        View view = LayoutInflater.from(this).inflate(R.layout.set_accessid,
                null);
        ((TextView) view.findViewById(R.id.set_accessid_title))
                .setText(msg);
        final EditText edit_company_id = (EditText) view.findViewById(R.id.edit_company_id);
        final EditText edit_email = (EditText) view.findViewById(R.id.edit_email);
        final EditText edit_passwd = (EditText) view.findViewById(R.id.edit_passwd);
        edit_company_id.setText(HttpManage.COMPANY_ID);
        //这里DEMO为了方便使用明文保存密码， 实际项目请根据实际情况采用安全手段保存
        edit_passwd.setText(SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWORD_ID));
        edit_email.setText(SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID));
        new AlertDialog.Builder(this)
                .setView(view)
                .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String compayId = edit_company_id.getText().toString().trim();
                        String mail = edit_email.getText().toString().trim();
                        String passwd = edit_passwd.getText().toString().trim();
                        HttpManage.COMPANY_ID=compayId;
                        SharedPreferencesUtil.keepShared(Constant.SAVE_COMPANY_ID, compayId);
                        SharedPreferencesUtil.keepShared(Constant.SAVE_EMAIL_ID, mail);
                        SharedPreferencesUtil.keepShared(Constant.SAVE_PASSWORD_ID, passwd);
                        registerUser();
                    }
                })
                .setNeutralButton("退出", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AuthActivity.this.finish();

                    }
                }).setCancelable(false).show();

    }




    @Override
    public void initWidget() {

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log("---------onResume ");
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log("---------onDestroy ");
        // unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public void onClickListener(View v) {
        // TODO Auto-generated method stub

    }
}
