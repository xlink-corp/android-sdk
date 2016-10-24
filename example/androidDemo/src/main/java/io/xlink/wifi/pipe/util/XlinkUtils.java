package io.xlink.wifi.pipe.util;

import io.xlink.wifi.pipe.MyApp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

public class XlinkUtils {
    /**
     * Map 转换为json
     * 
     * @param map
     * @return
     */
    public static JSONObject getJsonObject(Map<String, Object> map) {
	JSONObject jo = new JSONObject();
	Iterator<Entry<String, Object>> iter = map.entrySet().iterator();
	while (iter.hasNext()) {
	    Entry<String, Object> entry = iter.next();
	    try {
		jo.put(entry.getKey(), entry.getValue());
	    } catch (JSONException e) {
		e.printStackTrace();
	    }
	}
	return jo;

    }

    /**
     * 截取 byte
     * 
     * @param src
     *            源数据
     * @param off
     *            偏移量
     * @param len
     *            长度
     * @return
     */
    public static byte[] subBytes(byte[] bytes, int offset, int len) {
	byte[] b = new byte[len];

	System.arraycopy(bytes, offset, b, 0, len);
	return b;
    }

    /**
     * BASE64解密
     * 
     * @param key
     * @return
     * @throws IOException
     */
    public static byte[] base64Decrypt(String key) {
	byte[] bs = Base64.decode(key, Base64.DEFAULT);
	if (bs == null || bs.length == 0) {
	    bs = key.getBytes();
	}
	return bs;
    }

    /**
     * 判断网络是否连接
     * 
     * @param context
     * @return
     */
    public static boolean isConnected() {

	ConnectivityManager connectivity = (ConnectivityManager) MyApp.getApp()
		.getSystemService(Context.CONNECTIVITY_SERVICE);

	if (null != connectivity) {

	    NetworkInfo info = connectivity.getActiveNetworkInfo();
	    if (info != null && info.isAvailable()) {
		return true;
	    }
	}
	return false;
    }

    public static String getHexBinString(byte[] bs) {
	StringBuffer log = new StringBuffer();
	for (int i = 0; i < bs.length; i++) {
	    log.append(String.format("%02X", (byte) bs[i]) + " ");
	}
	return log.toString();
    }

    public static String getHexBinString(byte[] bs, String re) {
	StringBuffer log = new StringBuffer();
	for (int i = 0; i < bs.length; i++) {
	    if (i == bs.length - 1) {
		log.append(String.format("%02X", (byte) bs[i]));
	    } else {
		log.append(String.format("%02X", (byte) bs[i]) + re);
	    }
	}
	return log.toString();
    }

    /**
     * 把byte转化成 二进制.
     * 
     * @param aByte
     * @return
     */
    public static String getBinString(byte aByte) {
	String out = "";
	int i = 0;
	for (i = 0; i < 8; i++) {
	    int v = (aByte << i) & 0x80;
	    v = (v >> 7) & 1;
	    out += v;
	}
	return out;
    }

    static private final int bitValue0 = 0x01; // 0000 0001
    static private final int bitValue1 = 0x02; // 0000 0010
    static private final int bitValue2 = 0x04; // 0000 0100
    static private final int bitValue3 = 0x08; // 0000 1000
    static private final int bitValue4 = 0x10; // 0001 0000
    static private final int bitValue5 = 0x20; // 0010 0000
    static private final int bitValue6 = 0x40; // 0100 0000
    static private final int bitValue7 = 0x80; // 1000 0000

    /**
     * 设置flags
     * 
     * @param index
     *            第几个bit，从零开始排
     * @param value
     *            byte值
     * @return
     */
    public static byte setByteBit(int index, byte value) {
	if (index > 7) {
	    throw new IllegalAccessError("setByteBit error index>7!!! ");
	}
	byte ret = value;
	if (index == 0) {
	    ret |= bitValue0;
	} else if (index == 1) {
	    ret |= bitValue1;
	} else if (index == 2) {
	    ret |= bitValue2;
	} else if (index == 3) {
	    ret |= bitValue3;
	} else if (index == 4) {
	    ret |= bitValue4;
	} else if (index == 5) {
	    ret |= bitValue5;
	} else if (index == 6) {
	    ret |= bitValue6;
	} else if (index == 7) {
	    ret |= bitValue7;
	}
	return ret;
    }

    /**
     * 读取 flags 里的小bit
     * 
     * @param anByte
     * @param index
     * @return
     */
    public static boolean readFlagsBit(byte anByte, int index) {
	if (index > 7) {
	    throw new IllegalAccessError("readFlagsBit error index>7!!! ");
	}
	int temp = anByte << (7 - index);
	temp = temp >> 7;
	temp &= 0x01;
	if (temp == 1) {
	    return true;
	}
	// if((anByte & (01<<index)) !=0){
	// return true;
	// }
	return false;
    }

    /**
     * 将16位的short转换成byte数组
     * 
     * @param s
     *            short
     * @return byte[] 长度为2
     * */
    public static byte[] shortToByteArray(short s) {
	byte[] targets = new byte[2];
	for (int i = 0; i < 2; i++) {
	    int offset = (targets.length - 1 - i) * 8;
	    targets[i] = (byte) ((s >>> offset) & 0xff);
	}
	return targets;
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T getAdapterView(View convertView, int id) {
	SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
	if (viewHolder == null) {
	    viewHolder = new SparseArray<View>();
	    convertView.setTag(viewHolder);
	}
	View childView = viewHolder.get(id);
	if (childView == null) {
	    childView = convertView.findViewById(id);
	    viewHolder.put(id, childView);
	}
	return (T) childView;
    }

    public final static String MD5(String s) {
	char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'A', 'B', 'C', 'D', 'E', 'F' };
	try {
	    byte[] btInput = s.getBytes();
	    // 获得MD5摘要算法的 MessageDigest 对象
	    MessageDigest mdInst = MessageDigest.getInstance("MD5");
	    // 使用指定的字节更新摘要
	    mdInst.update(btInput);
	    // 获得密文
	    byte[] md = mdInst.digest();
	    // 把密文转换成十六进制的字符串形式
	    int j = md.length;
	    char str[] = new char[j * 2];
	    int k = 0;
	    for (int i = 0; i < j; i++) {
		byte byte0 = md[i];
		str[k++] = hexDigits[byte0 >>> 4 & 0xf];
		str[k++] = hexDigits[byte0 & 0xf];
	    }
	    return new String(str);
	} catch (Exception e) {
	    e.printStackTrace();
	    return null;
	}
    }

    /**
     * BASE64加密
     * 
     * @param key
     * @return
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    public static String base64EncryptUTF(byte[] key)
	    throws UnsupportedEncodingException {
	return new String(Base64.encode(key, Base64.DEFAULT), "UTF-8");
    }

    public static String base64Encrypt(byte[] key) {
	return new String(Base64.encode(key, Base64.DEFAULT));
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi() {
	ConnectivityManager cm = (ConnectivityManager) MyApp.getApp()
		.getSystemService(Context.CONNECTIVITY_SERVICE);

	if (cm == null || cm.getActiveNetworkInfo() == null) {
	    return false;
	}

	return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
	Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
	activity.startActivity(wifiSettingsIntent);
    }

    /**
     * 字符串 16进制转bytes
     * 
     * @param hexString
     * @return
     */
    public static byte[] stringToByteArray(String hexString) {
	if (hexString.isEmpty() || hexString.length() % 2 != 0)
	    return null;
	hexString.replaceAll(":", "");
	hexString = hexString.toLowerCase();
	final byte[] byteArray = new byte[hexString.length() / 2];
	int k = 0;
	for (int i = 0; i < byteArray.length; i++) {
	    // 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
	    byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
	    byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
	    byteArray[i] = (byte) (high << 4 | low);
	    k += 2;
	}
	return byteArray;
    }

    public static void shortTips(String tip) {
	Log.e("Tips", tip);
	Toast.makeText(MyApp.getApp(), tip, Toast.LENGTH_SHORT).show();
    }

    public static void longTips(String tip) {
	Toast.makeText(MyApp.getApp(), tip, Toast.LENGTH_LONG).show();
    }

}
