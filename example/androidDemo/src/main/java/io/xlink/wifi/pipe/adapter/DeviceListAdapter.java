package io.xlink.wifi.pipe.adapter;

import io.xlink.wifi.pipe.R;
import io.xlink.wifi.pipe.bean.Device;
import io.xlink.wifi.pipe.util.XlinkUtils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 设备列表适配器
 * 
 * @author Liuxy
 * @2015年3月25日下午5:27:31 </br>
 * @explain
 */
public class DeviceListAdapter extends BaseAdapter {

    private Context mContext;
    private List<Device> devices;

    public DeviceListAdapter(Context context, ArrayList<Device> device) {

	this.mContext = context;
	this.devices = device;
    }

    public void setDevices(ArrayList<Device> devices) {
	this.devices = devices;
    }

    @Override
    public int getCount() {
	return devices.size();
    }

    @Override
    public Object getItem(int position) {
	return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
	return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

	if (convertView == null) {
	    convertView = LayoutInflater.from(mContext).inflate(
		    R.layout.device_list_item, parent, false);
	}
	Device device = devices.get(position);
	TextView textView = XlinkUtils.getAdapterView(convertView,
		R.id.device_text);
	byte[] bsmac = XlinkUtils.stringToByteArray(device.getMacAddress());
	device.mac = XlinkUtils.getHexBinString(bsmac, ":");
	String msg = "设备:  " + device.mac + " ("
		+ device.getXDevice().getDeviceId() + ")";
	textView.setText(msg);

	return convertView;

    }

}
