package io.xlink.wifi.pipe.fragment;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.R;
import io.xlink.wifi.pipe.activity.DeviceActivity;
import io.xlink.wifi.pipe.adapter.CmdListAdapter;
import io.xlink.wifi.pipe.bean.CmdBean;
import io.xlink.wifi.pipe.manage.CmdManage;

/**
 * @author LiuXinYi
 * @version 1.0.0
 * @Date 2015年7月20日 下午2:24:18
 * @Description []
 */
public class LogFragment extends BaseFragment implements OnClickListener,
        OnItemClickListener, OnItemLongClickListener {
    private boolean start = false;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    private ScrollView scrollView;
    private TextView log_textView;
    private ListView cmd_Listview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (view == null) {
            view = inflater.inflate(R.layout.log_fragment, container, false);
            initWidget(view);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
        return view;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        updateListView();
    }

    public void updateListView() {
        if (adapter != null) {
            cmdBeans = CmdManage.getInstance().getCmds();
            adapter.setCmds(cmdBeans);
        }
    }

    private ArrayList<CmdBean> cmdBeans;
    private CmdListAdapter adapter;
    private final Handler handler = new Handler();

    public void Log(String log) {
        if (log_textView == null) {
            return;
        }
        if (log_textView.length() > 6000) {
            String temptext = log_textView.getText().toString();
            temptext = temptext.substring(temptext.length() - 2000,
                    temptext.length());
            log_textView.setText(temptext);
        }
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        log.replace("  ", " ");
        String dateString = formatter.format(new Date());
        log_textView.append(dateString + ":");
        log_textView.append("\n");
        log_textView.append(log);
        log_textView.append("\n");
        handler.post(new Runnable() {

            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private void initWidget(View view) {
        scrollView = (ScrollView) view.findViewById(R.id.scroll);
        log_textView = (TextView) view.findViewById(R.id.log_text);
        cmd_Listview = (ListView) view.findViewById(R.id.cmd_list);
        view.findViewById(R.id.btn_cmd).setOnClickListener(this);
        cmdBeans = CmdManage.getInstance().getCmds();
        adapter = new CmdListAdapter(getActivity(), cmdBeans);
        cmd_Listview.setAdapter(adapter);
        cmd_Listview.setOnItemClickListener(this);
        cmd_Listview.setOnItemLongClickListener(this);

        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constant.BROADCAST_LOG_ACTION);
        // 注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String logstr = intent.getStringExtra(Constant.DATA);
            if (action.equals(Constant.BROADCAST_LOG_ACTION)){
                Log(logstr);
            }
        }
    };

    //测试代码
//    private boolean openHandler=false;
//    private byte num;
//
//    private Runnable runnable=new Runnable() {
//        @Override
//        public void run() {
//            num++;
//            if(num>0xff)
//                num=0;
//            byte[] data1=new byte[]{(byte)0xaa,0x01,(byte)0x80,0x00,0x0a,0x00,0x4c,(byte)0xAC,(byte)0xCF,0x23,(byte)0x9C,0x0D,(byte)0xA6,(byte)0xB0,0x55};
//            byte[] data2=new byte[]{(byte)0xAA,0x02,(byte)0x80,0x00,0x0A,0x00,0x4D,(byte)0xAC,(byte)0xCF,0x23,(byte)0x9C,0x0D,(byte)0xA6,(byte)0xB2,0x55};
////            byte[] data=new byte[]{(byte)38,30,30,30,30,30,num,35,35,35,35,35,35,35,55};
//            ((DeviceActivity) getActivity()).sendData(data1, "ceshi");
//            ((DeviceActivity) getActivity()).sendData(data2, "ceshi");
//            handler.postDelayed(runnable,200);
//        }
//    };
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cmd:
                ((DeviceActivity) getActivity()).openCmdFg();
//               测试代码
//                openHandler=!openHandler;
//                if(openHandler){
//                    handler.postDelayed(runnable,100);
//                }else{
//                    handler.removeCallbacks(runnable);
//                }

                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        CmdBean cmdBean = cmdBeans.get(position);
        ((DeviceActivity) getActivity()).sendData(cmdBean.getData(), cmdBean.name);

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        final CmdBean cmdBean = cmdBeans.get(position);
        showDialog("确定删除‘" + cmdBean.name + "’指令吗",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CmdManage.getInstance().deleteCmd(cmdBean.name);
                        cmdBeans = CmdManage.getInstance().getCmds();
                        adapter.setCmds(cmdBeans);
                    }
                });
        return true;
    }

    public void showDialog(String tips,
                           DialogInterface.OnClickListener clickListener) {
        Builder builder = new Builder(getActivity());
        builder.setCancelable(false);
        builder.setMessage(tips);
        builder.setTitle("提示");
        builder.setNegativeButton("确定", clickListener);
        builder.setNeutralButton("取消", null);
        builder.create().show();
    }

}
