package io.xlink.wifi.pipe.bean;

import io.xlink.wifi.pipe.util.XlinkUtils;

import java.io.Serializable;

/**
 * @author LiuXinYi
 * @Date 2015年7月20日 下午4:02:42
 * @Description []
 * @version 1.0.0
 */
public class CmdBean implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public String name;
    public String strCmd;
    private byte[] data;

    @Override
    public boolean equals(Object o) {
	// TODO Auto-generated method stub
	return name.equals(((CmdBean)o).name);
    }

    @Override
    public int hashCode() {
	// TODO Auto-generated method stub
	return name.hashCode();
    }

    public byte[] getData() {
	if (data == null) {
	    data = XlinkUtils.stringToByteArray(strCmd);
	}
	return data;
    }

    public void setData(byte[] data) {
	this.data = data;
    }
}
