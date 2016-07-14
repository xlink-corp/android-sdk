package io.xlink.wifi.pipe.manage;

import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.MyApp;
import io.xlink.wifi.pipe.bean.Device;
import io.xlink.wifi.pipe.util.XTGlobals;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;

public class DeviceManage {

    /**
     * 存放自己定义的设备
     */
    public static ConcurrentHashMap<String, Device> deviceMap = new ConcurrentHashMap<String, Device>();
    private static DeviceManage instance;

    public static DeviceManage getInstance() {
        if (instance == null) {
            instance = new DeviceManage();
        }
        return instance;
    }

    private DeviceManage() {
    }

    private static final String TAG = "DeviceManage";
    private static String PASSWORD = "password";
    private static String ACCESSKEY = "accessKey";

    // 通过静态语句快，优先初始化，避免因为线程安全，重复调用.
    static {
        Map<String, String> stringmap = XTGlobals.getAllProperty();
        // if (stringmap == null || stringmap.size() == 0
        // ) {
        // stringmap = new HashMap<String, String>();
        // stringmap
        // .put("ACCF2356AF72",
        // "{\"port\":5987,\"dname\":\"xlink_dev\",\"init\":true,\"did\":0,\"pid\":\"acc4ee5e4a3f4735a3242fecc59b1d87\",\"mac\":\"ACCF2356AF72\",\"msv\":1,\"mhv\":1,\"version\":1,\"ip\":\"192.168.36.141\"}");
        // }

        Iterator<Entry<String, String>> iter = stringmap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            JSONObject obj;
            try {
                obj = new JSONObject(entry.getValue());
                XDevice xdev = XlinkAgent.JsonToDevice(obj);
                if (xdev != null) {
                    Device device = new Device(xdev);
                    if (!obj.isNull(PASSWORD)) {
                        device.setPassword(obj.getString(PASSWORD));
                    }
                    if (!obj.isNull(ACCESSKEY)) {
                        device.setAccessKey(obj.getInt(ACCESSKEY));
                    }
                    Log.e(TAG, "get Device :" + device);
                    deviceMap.put(xdev.getMacAddress(), device);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public static final ArrayList<Device> listDev = new ArrayList<Device>();

    public synchronized ArrayList<Device> getDevices() {
        listDev.clear();
        Iterator<Entry<String, Device>> iter = deviceMap.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, Device> entry = iter.next();
            listDev.add(entry.getValue());
        }
        return listDev;
    }

    /**
     * 修改设备密码
     *
     * @param mac
     * @param password
     */
    public void setAuth(String mac, String password) {
        Device device = deviceMap.get(mac);
        if (device != null) {
            device.setPassword(password);
            updateDevice(device);
        }

    }

    public Device getDevice(XDevice xd) {
        return deviceMap.get(xd.getMacAddress());
    }

    public Device getDevice(int deviceId) {
        Device dev = null;
        for (Device device : getDevices()) {
            if (device.getXDevice().getDeviceId() == deviceId) {
                dev = device;
                break;
            }
        }
        return dev;
    }

    /**
     * 保存设备到数据库
     */
    public void saveDevice(Device device) {
        JSONObject jo = XlinkAgent.deviceToJson(device.getXDevice());
        if (jo == null) {
            return;
        }
        if (device.getPassword() != null) {
            try {
                jo.put(PASSWORD, device.getPassword());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (device.getAccessKey() > 0) {
            try {
                jo.put(ACCESSKEY, device.getAccessKey());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "save device:" + jo);
        XTGlobals.setProperty(device.getMacAddress(), jo.toString());
    }

    public synchronized void clearAllDevice() {
        for (Device device : getDevices()) {
            XTGlobals.deleteProperty(device.getMacAddress());
        }
        deviceMap.clear();
        listDev.clear();
        XlinkAgent.getInstance().removeAllDevice();
    }

    public void addDevice(Device dev) {
        deviceMap.put(dev.getMacAddress(), dev);
        saveDevice(dev);
    }

    public void addDevice(XDevice dev) {
        Device device = deviceMap.get(dev.getMacAddress());
        if (device != null) { // 如果已经保存过设备，就不add
            device.setxDevice(dev);
            deviceMap.put(dev.getMacAddress(), device);
            saveDevice(device);
            return;
        }
        device = new Device(dev);
        deviceMap.put(dev.getMacAddress(), device);
        saveDevice(device);
    }

    public void updateDevice(Device device) {
        deviceMap.remove(device.getMacAddress());
        deviceMap.put(device.getMacAddress(), device);
        saveDevice(device);
    }

    public void updateNoSaveDevice(Device dev) {
        deviceMap.remove(dev.getMacAddress());
        deviceMap.put(dev.getMacAddress(), dev);
    }

    public void updateDevice(XDevice xdevice) {
        Device device = deviceMap.get(xdevice.getMacAddress());
        if (device == null) {
            return;
        }
        device.setxDevice(xdevice);
        updateDevice(device);
    }

    public void removeDevice(XDevice dev) {
        removeDevice(dev.getMacAddress());
    }

    public void removeDevice(String mac) {
        deviceMap.remove(mac);
        XlinkAgent.getInstance().removeDevice(mac);
        XTGlobals.deleteProperty(mac);
    }

    public Device getDevice(String mac) {
        return deviceMap.get(mac);
    }


    /**
     * 根据日期转化成一个byte
     *
     * @param _week
     * @return
     */
    public static byte weekToByte(ArrayList<Integer> _week) {
        byte b = 0;
        if (_week == null || _week.size() == 0) {
            return b;
        }
        for (Integer i : _week) {
            if (i > 6) { // 最多只有0-6
                continue;
            }
            b = XlinkUtils.setByteBit(i, b);
        }
        if (b == (byte) 0x00) {
            return b;
        }
        b = XlinkUtils.setByteBit(7, b);
        return b;
    }

    /**
     * 通知UI，定时器已经改变了
     */
    public void notificationTimer(Device device) {
        Intent intent = new Intent(Constant.BROADCAST_TIMER_UPDATE);
        intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
        MyApp.getApp().sendBroadcast(intent);
    }

    /**
     * 通知UI，插座状态已经改变了
     */
    public void notificationSocket(Device device, int status) {
        Intent intent = new Intent(Constant.BROADCAST_SOCKET_STATUS);
        intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
        intent.putExtra(Constant.STATUS, status);
        MyApp.getApp().sendBroadcast(intent);
    }

    public String getWeekList(ArrayList<Integer> intlist) {
        StringBuffer sb = new StringBuffer();
        for (Integer i : intlist) {
            sb.append(i + " ");
        }

        return sb.toString();
    }

    // public void

    // private void insert(String title, String neilon, String date) {
    // SQLiteDatabase db = dbHelper.getReadableDatabase();
    // ContentValues values = new ContentValues();
    //
    // if (title.equals("")) {
    // // for(a=1;;a++){
    // values.put("title", "无标题");
    // values.put("neilon", neilon);
    // values.put("date", date);
    // // }
    //
    // } else {
    // values.put("title", title);
    // values.put("neilon", neilon);
    // values.put("date", date);
    // }
    // db.insert(TABLE, null, values);
    // db.close();
    //
    // }
    //
    // private void del(String date) {
    // SQLiteDatabase db = dbHelper.getReadableDatabase();
    // db.delete(TABLE, "date =?", new String[] { date });
    // db.close();
    // }
    //
    // private void queryAll() {
    // SQLiteDatabase db = dbHelper.getReadableDatabase();
    // Cursor c = db.query(TABLE, null, null, null, null, null, null);// 查询并获得游标
    // if (c.moveToFirst()) {// 判断游标是否为空
    // for (int i = 0; i < c.getCount(); i++) {
    // c.move(i);// 移动到指定记录
    // String mac = c.getString(c.getColumnIndex("mac"));
    // String did = c.getString(c.getColumnIndex("did"));
    // String address = c.getString(c.getColumnIndex("address"));
    // String name = c.getString(c.getColumnIndex("name"));
    // int port = c.getInt(c.getColumnIndex("port"));
    // String isInit = c.getString(c.getColumnIndex("isinit"));
    // String version = c.getString(c.getColumnIndex("version"));
    // int mhv = c.getInt(c.getColumnIndex("mhv"));
    // int msv = c.getInt(c.getColumnIndex("msv"));
    // String productid = c.getString(c.getColumnIndex("productid"));
    //
    // }
    // }
    // }
    //
    // private void updata(String title, String neilon, String date, String
    // jiuData) {
    // SQLiteDatabase db = dbHelper.getReadableDatabase();
    // ContentValues values = new ContentValues();
    // if (title.equals("")) {
    // values.put("title", "无标题");
    // values.put("neilon", neilon);
    // values.put("date", date);
    // } else {
    // values.put("title", title);
    // values.put("neilon", neilon);
    // values.put("date", date);
    // }
    // db.update(TABLE, values, "date=?", new String[] { jiuData });
    // db.close();
    // }
}
