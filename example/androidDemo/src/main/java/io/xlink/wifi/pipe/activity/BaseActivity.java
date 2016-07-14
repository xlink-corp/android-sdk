package io.xlink.wifi.pipe.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import io.xlink.wifi.pipe.MyApp;
import io.xlink.wifi.pipe.view.CustomDialog;

/**
 * Activity 基类
 *
 * @author Liuxy
 * @2015年5月8日上午9:33:39 </br>
 * @explain
 */
public abstract class BaseActivity extends Activity implements OnClickListener {

    /*
     */
    private static final int ACTIVITY_CREATE = 1;
    private static final int ACTIVITY_START = 2;
    private static final int ACTIVITY_RESUME = 3;
    private static final int ACTIVITY_PAUSE = 4;
    private static final int ACTIVITY_STOP = 5;
    private static final int ACTIVITY_DESTROY = 6;
    public int activityState;

    /**
     * 初始化view控件
     */
    public abstract void initWidget();

    @Override
    public void onClick(View v) {
        onClickListener(v);
    }

    public Dialog createProgressDialog(String title, String tips) {
        ProgressDialog progressDialog = CustomDialog.createProgressDialog(this,
                title, tips);
        return progressDialog;
    }

    // public void setTAG(String tag) {
    // TAG = tag;
    // }
    public CustomDialog createTipsDialog(String title, String tips) {
        CustomDialog dialog = CustomDialog.createErrorDialog(this, title, tips,
                null);
        dialog.show();
        return dialog;
    }

    /**
     * 点击事件
     *
     * @param v
     */
    public abstract void onClickListener(View v);

    /**
     * 跳转界面
     *
     * @param paramClass
     */
    protected void openActivity(Class<?> paramClass) {
        Log.e(getClass().getSimpleName(),
                "openActivity：：" + paramClass.getSimpleName());
        openActivity(paramClass, null);
    }

    protected void openActivity(Class<?> paramClass, Bundle paramBundle) {
        Intent localIntent = new Intent(this, paramClass);
        if (paramBundle != null)
            localIntent.putExtras(paramBundle);
        startActivity(localIntent);
    }

    protected void openActivity(String paramString) {
        openActivity(paramString, null);
    }

    protected void openActivity(String paramString, Bundle paramBundle) {
        Intent localIntent = new Intent(paramString);
        if (paramBundle != null)
            localIntent.putExtras(paramBundle);
        startActivity(localIntent);
    }

    /**
     * 启动服务
     *
     * @param paramClass
     */
    protected void startService(Class<?> paramClass) {
        Intent localIntent = new Intent(this, paramClass);
        startService(localIntent);
    }

    /**
     * 关闭服务
     *
     * @param paramClass
     */
    protected void stopService(Class<?> paramClass) {
        Intent localIntent = new Intent(this, paramClass);
        stopService(localIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityState = ACTIVITY_CREATE;
        // Log("---------onCreat ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityState = ACTIVITY_START;
        // Log("---------onStart ");
    }

    // public void Log(String text) {
    //
    // Log.e(TAG, text);
    // }

    @Override
    protected void onResume() {
        super.onResume();
        activityState = ACTIVITY_RESUME;
        // Log("---------onResume ");
        MyApp.getApp().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityState = ACTIVITY_PAUSE;
        // Log("---------onPause ");
    }

    @Override
    protected void onStop() {
        super.onResume();
        activityState = ACTIVITY_STOP;
        // Log("---------onStop ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Log("---------onRestart ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        activityState = ACTIVITY_DESTROY;
        // Log("---------onDestroy ");

    }
}
