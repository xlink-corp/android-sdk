package io.xlink.wifi.pipe.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.R;
import io.xlink.wifi.pipe.activity.DeviceActivity;
import io.xlink.wifi.pipe.adapter.DataPointListAdapter;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.bean.DataPoint;
import io.xlink.wifi.sdk.listener.SetDataPointListener;

/**
 * @author LiuXinYi
 * @version 1.0.0
 * @Date 2015年7月20日 下午2:24:18
 * @Description []
 */
public class DataPointFragment extends BaseFragment implements OnClickListener {
    private boolean start = false;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (view == null) {
            view = inflater.inflate(R.layout.datapiont_fragment, container, false);
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

    private DataPointListAdapter adapter;
    private final Handler handler = new Handler();

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private void initWidget(View view) {
        listView = (ListView) view.findViewById(R.id.ls_datapoint);
        view.findViewById(R.id.btn_cmd).setOnClickListener(this);
        adapter = new DataPointListAdapter(getActivity());
        listView.setAdapter(adapter);
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constant.BROADCAST_DEVICE_DATAPOINT_RECV);
        // 注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);

        if (adapter != null) {
            adapter.setData(((DeviceActivity) getActivity()).dataPoints);
        }

        adapter.setOnItemClickListener(new DataPointListAdapter.OnItemClickListener() {
            @Override
            public void onSettingClick(int index,DataPoint dataPoint) {
                List<DataPoint> dataPoints = new ArrayList<DataPoint>();
                dataPoints.add(dataPoint);
                XlinkAgent.getInstance().setDataPoint(((DeviceActivity) getActivity()).device.getXDevice(), dataPoints, new SetDataPointListener() {
                    @Override
                    public void onSetDataPoint(XDevice xdevice, int code, int messageId) {
                        if (code == XlinkCode.SUCCEED) {
                            XlinkUtils.shortTips("设置数据端点成功");
                        } else {
                            XlinkUtils.shortTips("设置数据端点失败：" + code);
                        }
                    }
                });
            }
        });
    }


    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.BROADCAST_DEVICE_DATAPOINT_RECV)) {
                List<DataPoint> dataPoints = (List<DataPoint>) intent.getSerializableExtra(Constant.DATA);
                if (adapter != null) {
                    adapter.setData(dataPoints);
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cmd:

                break;
            default:
                break;
        }
    }

}
