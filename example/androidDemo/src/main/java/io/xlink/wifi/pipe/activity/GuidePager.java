package io.xlink.wifi.pipe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import io.xlink.wifi.pipe.R;

/**
 * @author Chendh
 * @ClassName: GuidePager
 * @Description: TODO(引导页面)
 * @date 2015年3月9日 上午9:41:03
 */
@Deprecated
public class GuidePager extends Activity {
    private static final int GOTO_MAINACTIVITY = 0;
    private static final int TIME = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_pager);
        // MyApp.getIns().setCurrentActivity(this);
        mHandler.sendEmptyMessageDelayed(GOTO_MAINACTIVITY, TIME);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            if (msg.what == GOTO_MAINACTIVITY) {
                Intent intent = new Intent(GuidePager.this, AuthActivity.class);
                startActivity(intent);
                finish();
            }
            super.handleMessage(msg);
        }

    };

}
