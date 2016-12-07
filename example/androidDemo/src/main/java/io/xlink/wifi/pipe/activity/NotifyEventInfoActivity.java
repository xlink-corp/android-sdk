package io.xlink.wifi.pipe.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import io.xlink.wifi.pipe.R;
import io.xlink.wifi.sdk.bean.EventNotify;

public class NotifyEventInfoActivity extends BaseActivity {

    public static final String NOTIFY_BUNDLE="NOTIFY_BUNDLE";

    private TextView notifyText;
    private TextView nofityData;
    private EventNotify mEventNotify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify_event_info);
        mEventNotify=(EventNotify)getIntent().getSerializableExtra(NOTIFY_BUNDLE);
        initWidget();
    }

    @Override
    public void initWidget() {
        notifyText=(TextView)findViewById(R.id.notifyText);
        nofityData=(TextView)findViewById(R.id.nofityData);
        if(mEventNotify!=null) {
            notifyText.setText(new Gson().toJson(mEventNotify));
            try {
                nofityData.setText("notifyData-->" + (new String(mEventNotify.notifyData, "UTF-8")));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClickListener(View v) {

    }
}
