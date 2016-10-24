package io.xlink.wifi.pipe.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.MyApp;
import io.xlink.wifi.pipe.R;
import io.xlink.wifi.pipe.http.HttpManage;

/**
 * @author LiuXinYi
 * @version 1.0.0
 * @Date 2015年5月21日 上午10:26:58
 * @Description []
 */
public class AboutActivity extends BaseActivity {

    private TextView company_id;
    private TextView about_app_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_act);
        initWidget();
    }

    TextView tv_v, tv_p, about_access_id, about_access_key;
    EditText et;

    @Override
    public void initWidget() {
        findViewById(R.id.header_back_ll).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        finish();
                    }
                });
        // TODO Auto-generated method stub
        tv_v = (TextView) findViewById(R.id.about_ver);
        tv_p = (TextView) findViewById(R.id.about_pid);
        about_app_id = (TextView) findViewById(R.id.about_app_id);
        company_id = (TextView) findViewById(R.id.company_id);
        tv_v.setText("ver " + MyApp.getApp().versionCode + ".0");
        tv_p.setText(Constant.PRODUCTID);
        company_id.setText(HttpManage.COMPANY_ID);
        about_app_id.setText(MyApp.getApp().appid+"");
    }

    @Override
    public void onClickListener(View v) {

    }
}
