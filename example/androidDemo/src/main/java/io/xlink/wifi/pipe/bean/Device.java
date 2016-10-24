package io.xlink.wifi.pipe.bean;

import java.io.Serializable;

import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkCode;

public class Device implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // xlink 可识别的设备 实例
    private XDevice xDevice;
    // 设备授权码
    private String password;
    private int accessKey;
    public boolean isSelect;
    private short th;
    private int wind;
    private boolean switch_;
    public String mac;
    private boolean isSubscribe =false;

    public short getTh() {
        return th;
    }

    public void setTh(short temp) {
        this.th = temp;
    }

    public int getWind() {
        return wind;
    }

    public void setWind(int wind) {
        this.wind = wind;
    }

    public boolean isSwitch_() {
        return switch_;
    }

    public void setSwitch_(boolean switch_) {
        this.switch_ = switch_;
    }

    public String getPassword() {
        return password;
    }

    public boolean isConnect() {
        if (xDevice.getDevcieConnectStates() == XlinkCode.DEVICE_STATE_OFFLIEN) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return xDevice.toString() + " pwd:" + password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(int accessKey) {
        this.accessKey = accessKey;
    }

    public Device(XDevice xDevice) {
        this.xDevice = xDevice;
    }

    public String getMacAddress() {
        return xDevice.getMacAddress();
    }

    public boolean isSubscribe() {
        return isSubscribe;
    }

    public void setSubscribe(boolean subscribe) {
        isSubscribe = subscribe;
    }

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        if (o instanceof Device) {
            Device d = (Device) o;
            return xDevice.equals(d.getXDevice());
        } else if (o instanceof XDevice) {
            return xDevice.equals(o);
        }
        return super.equals(o);
    }

    public XDevice getXDevice() {
        return xDevice;
    }

    public void setxDevice(XDevice xDevice) {
        this.xDevice = xDevice;
    }

    public String getName() {
        return xDevice.getDeviceName();
    }

    // /**
    // * 是否在线（不管公网，还是内网）
    // *
    // * @return
    // */
    // public boolean isOnline() {
    // //
    // return xDevice.getState() != ResponseCode.DEVICE_STATE_OFFLINE;
    // }
}
