package io.xlink.wifi.pipe.util;

import io.xlink.wifi.pipe.http.XHeader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * HTTP请求体类
 */
@Deprecated
public class RequestParams {
    private String url;
    private ArrayList<XHeader> headers;
    private HttpEntity entity;

    public HttpEntity getEntity() {
	if (entity == null) {
	    // 默认是获取json 的实体
	    entity = getJsonEntity();
	}
	return entity;
    }

    public void setEntity(HttpEntity entity) {
	this.entity = entity;
    }

    /**
     * 
     * @param url
     *            请求url
     * @param type
     *            类型（判断解析方式用）
     * @param listener
     *            监听器
     */
    public RequestParams(String url) {
	// TODO Auto-generated constructor stub
	this.url = url;
    }

    public XHeader[] getHeaders() {
	XHeader[] heads = new XHeader[headers.size()];
	for (int i = 0; i < headers.size(); i++) {
	    heads[i] = headers.get(i);
	}
	return heads;
    }

    public void setHeaders(ArrayList<XHeader> headers) {
	this.headers = headers;
    }

    public void addHeader(XHeader header) {
	if (headers == null) {
	    headers = new ArrayList<XHeader>();
	}
	headers.add(header);
    }

    public String getUrl() {
	return url;
    }

    private Map<String, Object> param = new HashMap<String, Object>();

    public void put(String key, Object value) {
	param.put(key, value);

    }

    public void remove(String key) {
	param.remove(key);
    }

    @Override
    public String toString() {
	// TODO Auto-generated method stub
	return param.toString();
    }

    /**
     * 获取 get请求所需要的 key-value
     * 
     * @return
     */
    public String getGetRequsetUrl() {
	Set<Entry<String, Object>> entryKeys = param.entrySet();

	if (entryKeys.isEmpty()) {
	    return "";
	}
	StringBuffer sb = new StringBuffer();
	sb.append("?");
	Iterator<Entry<String, Object>> ite = entryKeys.iterator();
	while (ite.hasNext()) {
	    Entry<String, Object> entry = ite.next();
	    sb.append(entry.getKey());
	    sb.append("=");
	    sb.append(entry.getValue());
	    sb.append("&");
	}
	sb.deleteCharAt(sb.length() - 1);
	return sb.toString();

    }

    /**
     * 获取key-value格式 的json entitys
     * 
     * @return
     */
    public HttpEntity getJsonEntity() {

	JSONObject data = getJsonObject(param);
	StringEntity entity = null;
	try {
	    entity = new StringEntity(data.toString(), "UTF-8");
	} catch (UnsupportedEncodingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return entity;

    }

    public String getJson() {
	return getJsonObject(param).toString();
    }

    /**
     * 获取表单形式的entity
     * 
     * @return
     */
    public HttpEntity getNameValuePairEntity() {

	HttpEntity entity = null;
	List<NameValuePair> list = new ArrayList<NameValuePair>();
	Set<String> keys = param.keySet();
	Iterator<String> ite = keys.iterator();
	while (ite.hasNext()) {
	    String key = ite.next();
	    list.add(new BasicNameValuePair(key, param.get(key) + ""));
	}
	try {
	    entity = new UrlEncodedFormEntity(list, HTTP.UTF_8);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return entity;

    }

    //

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
}
