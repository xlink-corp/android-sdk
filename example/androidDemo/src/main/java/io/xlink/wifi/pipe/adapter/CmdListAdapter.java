package io.xlink.wifi.pipe.adapter;

import io.xlink.wifi.pipe.R;
import io.xlink.wifi.pipe.bean.CmdBean;
import io.xlink.wifi.pipe.util.XlinkUtils;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author LiuXinYi
 * @Date 2015年7月20日 下午4:53:02
 * @Description []
 * @version 1.0.0
 */
public class CmdListAdapter extends BaseAdapter {
    private ArrayList<CmdBean> cmdBeans;
    private Context mContext;

    public CmdListAdapter(Context context, ArrayList<CmdBean> cmdBeans) {
	this.mContext = context;
	this.cmdBeans = cmdBeans;
    }

    public void setCmds(ArrayList<CmdBean> cmdBeans) {
	this.cmdBeans = cmdBeans;
	notifyDataSetChanged();
    }

    @Override
    public int getCount() {
	// TODO Auto-generated method stub
	return cmdBeans.size();
    }

    @Override
    public Object getItem(int position) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public long getItemId(int position) {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	if (convertView == null) {
	    convertView = LayoutInflater.from(mContext).inflate(
		    R.layout.cmd_tiem, parent, false);
	}
	CmdBean b = cmdBeans.get(position);
	TextView textView = XlinkUtils.getAdapterView(convertView,
		R.id.text_cmd_name);
	textView.setText(b.name );
	return convertView;
    }

}
