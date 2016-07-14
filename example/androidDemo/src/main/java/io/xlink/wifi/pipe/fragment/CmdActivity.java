package io.xlink.wifi.pipe.fragment;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.R;
import io.xlink.wifi.pipe.activity.BaseActivity;
import io.xlink.wifi.pipe.bean.CmdBean;
import io.xlink.wifi.pipe.manage.CmdManage;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.pipe.view.ClearableEditText;

/**
 * @author LiuXinYi
 * @version 1.0.0
 * @Date 2015年7月20日 下午5:13:06
 * @Description []
 */
public class CmdActivity extends BaseActivity implements OnClickListener {


    private final Handler handler = new Handler();
    public TextView cmd_textview;


    public StringBuilder cmd;
    private ScrollView cmd_scroll;
    private TextView headertitle;
    private View title_save;
    private View device_status_layout;
    private View rl_cmd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cmd_fragmnet);
        initWidget();
    }

    @Override
    public void initWidget() {
//        cmd_scroll = (ScrollView) findViewById(R.id.cmd_scroll);
        cmd_textview = (TextView) findViewById(R.id.cmd_textview);
        cmd_textview.setMovementMethod(ScrollingMovementMethod.getInstance());

        findViewById(R.id.num_0).setOnTouchListener(numTouch);
        findViewById(R.id.num_1).setOnTouchListener(numTouch);
        findViewById(R.id.num_2).setOnTouchListener(numTouch);
        findViewById(R.id.num_3).setOnTouchListener(numTouch);
        findViewById(R.id.num_4).setOnTouchListener(numTouch);
        findViewById(R.id.num_5).setOnTouchListener(numTouch);
        findViewById(R.id.num_6).setOnTouchListener(numTouch);
        findViewById(R.id.num_7).setOnTouchListener(numTouch);
        findViewById(R.id.num_8).setOnTouchListener(numTouch);
        findViewById(R.id.num_9).setOnTouchListener(numTouch);
        findViewById(R.id.num_a).setOnTouchListener(numTouch);
        findViewById(R.id.num_b).setOnTouchListener(numTouch);
        findViewById(R.id.num_c).setOnTouchListener(numTouch);
        findViewById(R.id.num_d).setOnTouchListener(numTouch);
        findViewById(R.id.num_e).setOnTouchListener(numTouch);
        findViewById(R.id.num_f).setOnTouchListener(numTouch);
        findViewById(R.id.num_del).setOnClickListener(this);
        findViewById(R.id.num_del).setOnTouchListener(
                new OnTouchListener() {// 一直按着del键，能一直在删除

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                isFocus = true;
                                handler.postDelayed(delCmdHandler,
                                        ViewConfiguration.getLongPressTimeout());
                                break;
                            case MotionEvent.ACTION_UP:
                                isFocus = false;
                                handler.removeCallbacks(delCmdHandler);
                                break;

                            default:
                                break;
                        }

                        return false;
                    }
                });
        findViewById(R.id.btn_send_cmd).setOnClickListener(this);

        headertitle = (TextView) findViewById(R.id.header_title);
        title_save = findViewById(R.id.title_save);
        device_status_layout = findViewById(R.id.device_status_layout);
        headertitle.setText("指令设定");
        device_status_layout.setVisibility(View.GONE);
        title_save.setVisibility(View.VISIBLE);
        title_save.setOnClickListener(this);
        findViewById(R.id.header_back_ll).setOnClickListener(this);

        cmd_textview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboardManager.hasPrimaryClip()) {
                    cmd_textview.append(clipboardManager.getPrimaryClip().getItemAt(0).getText());
                    handler.post(runnable);
                }

                return false;
            }
        });
    }

    boolean isNumFocus;
    private OnTouchListener numTouch = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Button bv = (Button) v;
                    isNumFocus = true;
                    numAddHandler.num_handler = bv.getText().toString().trim();
                    handler.postDelayed(numAddHandler, 500);// 500毫秒的等待时间,
                    break;
                case MotionEvent.ACTION_UP:
                    isNumFocus = false;
                    numAddHandler.num_handler = null;
                    handler.removeCallbacks(numAddHandler);
                    numListener.onClick(v);// 调用点击事件
                    break;

                default:
                    break;
            }

            return false;
        }
    };

    boolean isFocus = false;
    private Runnable delCmdHandler = new Runnable() {

        @Override
        public void run() {
            if (isFocus) {
                delCmd();
                handler.postDelayed(delCmdHandler, 200);
            }
        }
    };

    private NumRunnableHandler numAddHandler = new NumRunnableHandler() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (isNumFocus && num_handler != null) {
                cmdAdd(num_handler);
                handler.postDelayed(numAddHandler, 100);
            }

        }
    };

    private void cmdAdd(String num) {
        if (cmd == null) {
            cmd = new StringBuilder();
        }
        cmd.append(num);
        if (cmd.length() % 2 == 0) {
            cmd_textview.append(num + " ");
        } else {
            cmd_textview.append(num);
        }
        handler.post(runnable);
    }

    private OnClickListener numListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Button btn = (Button) v;
            String num = btn.getText().toString().trim();
            cmdAdd(num);
        }
    };
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int offset = cmd_textview.getLineCount() * cmd_textview.getLineHeight();
            if (offset > cmd_textview.getHeight()) {
                cmd_textview.scrollTo(0, offset - cmd_textview.getHeight());
            }
        }
    };

    private void saveCmd() {
        final ClearableEditText editText = new ClearableEditText(this);
        new AlertDialog.Builder(this).setTitle("输入保存名称")
                .setView(editText).setNeutralButton("取消", null)
                .setNegativeButton("保存", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String name = editText.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            XlinkUtils.shortTips("名称不能为空");
                            saveCmd();
                            return;
                        }
                        if (name.length() > 20) {
                            XlinkUtils.shortTips("请不要超过20位数。");
                            return;
                        }
                        if (CmdManage.getInstance().getCmd(name) != null) {
                            XlinkUtils.shortTips("已经存在该指令");
                            saveCmd();
                            return;
                        }
                        CmdBean cmdBean = new CmdBean();
                        cmdBean.name = name;
                        cmdBean.strCmd = cmd.toString();
                        cmdBean.setData(XlinkUtils
                                .stringToByteArray(cmdBean.strCmd));
                        CmdManage.getInstance().addCmd(cmdBean);
                        finish();

                    }
                }).show();

    }

    void delCmd() {
        if (cmd == null) {
            return;
        }
        if (cmd.length() == 0) {
            return;
        }
        String cmdText = cmd_textview.getText().toString();
        char c = cmdText.charAt(cmdText.length() - 1);
        if (c == ' ') {
            cmdText = cmdText.substring(0, cmdText.length() - 2);
        } else {
            cmdText = cmdText.substring(0, cmdText.length() - 1);
        }
        cmd.delete(cmd.length() - 1, cmd.length());
        cmd_textview.setText(cmdText);
        handler.post(runnable);
    }


    @Override
    public void onClickListener(View v) {

        switch (v.getId()) {
            case R.id.title_save:
                if (cmd == null || cmd.length() == 0) {
                    XlinkUtils.shortTips("请输入指令。");
                    return;
                }
                if (cmd.length() % 2 != 0) {
                    XlinkUtils.shortTips("输入指令需为偶数");
                    return;
                }
                saveCmd();
                break;
            case R.id.num_del:
                delCmd();
                break;
            case R.id.header_back_ll:
                finish();
                break;
            case R.id.btn_send_cmd:
                if (cmd == null || cmd.length() == 0) {
                    XlinkUtils.shortTips("请输入指令。");
                    return;
                }
                if (cmd.length() % 2 != 0) {
                    XlinkUtils.shortTips("输入指令需为偶数");
                    return;
                }
                byte[] bs = XlinkUtils.stringToByteArray(cmd.toString());
//                ((DeviceActivity) getActivity()).sendData(bs, null);
                Intent intent = new Intent(Constant.BROADCAST_SEND_DATA);
                intent.putExtra(Constant.DATA, bs);
                sendBroadcast(intent);
                finish();
                break;
            default:
                break;
        }
        // TODO Auto-generated method stub

    }

}

abstract class NumRunnableHandler implements Runnable {
    public String num_handler;

}