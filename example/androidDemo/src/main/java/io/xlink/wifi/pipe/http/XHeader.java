package io.xlink.wifi.pipe.http;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

/**
 * @author LiuXinYi
 * @Date 2015年5月25日 下午5:44:05
 * @Description [封装http header 便于传输]
 * @version 1.0.0
 */
public class XHeader implements Header {
    private String name;
    private String value;

    public XHeader(String name, String value, HeaderElement[] element) {
	// TODO Auto-generated constructor stub
	this.value = value;
	this.name = name;
    }
    public XHeader(String name, String value) {
        // TODO Auto-generated constructor stub
        this.value = value;
        this.name = name;
    }
    @Override
    public HeaderElement[] getElements() throws ParseException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return name;
    }

    @Override
    public String getValue() {
	// TODO Auto-generated method stub
	return value;
    }

}
