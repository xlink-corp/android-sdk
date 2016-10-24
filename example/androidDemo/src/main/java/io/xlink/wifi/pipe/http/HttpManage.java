package io.xlink.wifi.pipe.http;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import io.xlink.wifi.pipe.MyApp;


/**
 * Created by MYFLY on 2016/1/12.
 */
public class HttpManage {

    private static HttpManage instance;

    public static String COMPANY_ID = "";
//    private final String host = "http://api-test.xlink.cn:8887";
    private final String host = "http://api2.xlink.cn";
//    private final String host="http://139.224.7.17";
    // url
    public final String registerUrl = host + "/v2/user_register";
    public final String loginUrl = host + "/v2/user_auth";
    public final String forgetUrl = host + "/v2/user/password/forgot";
    //.管理员（用户）获取所有设备分享请求列表
    public final String shareListUrl = host + "/v2/share/device/list";
    public final String getUserInfoUrl = host + "/v2/user/{user_id}";
    //获取某个用户绑定的设备列表。
    public final String subscribeListUrl = host + "/v2/user/%d/subscribe/devices";
    // public final String subscribeListUrl = host + "/v2/user/%d/subscribe/devices?version=%d";
    //设备管理员分享设备给指定用户
    public final String shareDeviceUrl = host + "/v2/share/device";
    //用户拒绝设备分享
    public final String denyShareUrl = host + "/v2/share/device/deny";
    //用户确认设备分享
    public final String acceptShareUrl = host + "/v2/share/device/accept";
    //获取设备信息
    public final String getDeviceUrl = host + "/v2/product/{product_id}/device/{device_id}";
    //订阅设备（待定）
    public final String subscribeUrl = host + "/v2/user/{user_id}/subscribe";
    //修改用户信息
    public final String modifyUserUrl = host + "/v2/user/{user_id}";
    //重置密码
    public final String resetPasswordUrl = host + "/v2/user/password/reset";
    //获取数据端点列表
    public final String getDatapointsUrl = host + "/v2/product/{product_id}/datapoints";
    //取消订阅设备
    public final String unsubscribeUrl = host + "/v2/user/{user_id}/unsubscribe";

    //.管理员或用户删除这条分享记录
    public final String deleteShareUrl = host + "/v2/share/device/delete/{invite_code}";

    //检查固件版本
//    public final String checkUpdateUrl = host + "/v1/user/device/version";
    public final String checkUpdateUrl = "http://app.xlink.cn/v1/user/device/version";
    //固件升级
//    public final String upgradeUrl = host + "/v1/user/device/version";
    public final String upgradeUrl = "http://app.xlink.cn/v1/user/device/upgrade";


    /**
     * code : 5031001
     * msg : service unavailable
     */


    public static HttpManage getInstance() {
        if (instance == null) {
            instance = new HttpManage();
        }
        return instance;
    }

    /**
     * 全局的http代理
     */
    private static AsyncHttpClient client = new AsyncHttpClient();

    static {
        // 设置网络超时时间
        client.setTimeout(5000);
        client.setConnectTimeout(3000);
    }


    /**
     * http 邮箱注册接口
     *
     * @param mail 用户 邮箱
     * @param name 昵称（别名，仅供后台管理平台观看，对用户来说记住uid和pwd就行）
     * @param pwd  密码
     */
    public void registerUserByMail(String mail, String name, String pwd, final ResultCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", mail);
        params.put("nickname", name);
        params.put("corp_id", COMPANY_ID);
        params.put("password", pwd);
        params.put("source", "2");
        post(registerUrl, params, callback);
    }

    /**
     * http 邮箱登录接口
     *
     * @param mail 用户 邮箱
     * @param pwd  密码
     */
    public void login(String mail, String pwd, final ResultCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", mail);
        params.put("corp_id", COMPANY_ID);
        params.put("password", pwd);
        post(loginUrl, params, callback);
    }

    /**
     * http //.管理员（用户）获取所有设备分享请求列表
     */
    public void getShareList(final ResultCallback callback) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        get(shareListUrl, headers, callback);
    }

    /**
     * 11.获取用户详细信息
     */
    public void getUserInfo(int userId, final ResultCallback callback) {
        String url = getUserInfoUrl.replace("{user_id}", userId + "");
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        get(url, headers, callback);
    }

    /**
     * 11.获取用户详细信息
     */
    public void getDatapoints(String pid, final ResultCallback callback) {
        String url = getDatapointsUrl.replace("{product_id}", pid);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        get(url, headers, callback);
    }

    /**
     * http 忘记密码
     *
     * @param mail 用户 邮箱
     */
    public void forgetPasswd(String mail, final ResultCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", mail);
        params.put("corp_id", COMPANY_ID);
        post(forgetUrl, params, callback);
    }

    /**
     * 设备管理员分享设备给指定用户
     *
     * @param mail 用户 邮箱
     */
    public void shareDevice(String mail, int deviceId, final ResultCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user", mail);
        params.put("expire", "7200");
        params.put("mode", "email");
        params.put("device_id", deviceId + "");
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        post(shareDeviceUrl, headers, params, callback);
    }

    /**
     * 用户拒绝设备分享
     *
     * @param inviteCode 分享ID
     */
    public void denyShare(String inviteCode, final ResultCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("invite_code", inviteCode);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        post(denyShareUrl, headers, params, callback);
    }

    /**
     * 订阅设备
     *
     * @param userId userId
     */
    public void subscribe(String userId, String productId, int deviceId, final ResultCallback callback) {
        String url = subscribeUrl.replace("{user_id}", userId);
        Map<String, String> params = new HashMap<String, String>();
        params.put("product_id", productId);
        params.put("device_id", deviceId + "");
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        post(url, headers, params, callback);
    }
    /**
     * 取消订阅设备
     *
     * @param userId userId
     */
    public void unsubscribe(int userId,  int deviceId, final ResultCallback callback) {
        String url = unsubscribeUrl.replace("{user_id}", userId+"");
        Map<String, String> params = new HashMap<String, String>();
        params.put("device_id", deviceId + "");
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        post(url, headers, params, callback);
    }

    /**
     * .修改用户信息
     *
     * @param userId userId
     */
    public void modifyUser(int userId, String nickname, final ResultCallback callback) {
        String url = modifyUserUrl.replace("{user_id}", userId + "");
        Map<String, String> params = new HashMap<String, String>();
        params.put("nickname", nickname);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        put(url, headers, params, callback);
    }

    /**
     * .重置密码
     */
    public void resetPassword(String newPasswd, String oldPasswd, final ResultCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("old_password", oldPasswd);
        params.put("new_password", newPasswd);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        put(resetPasswordUrl, headers, params, callback);
    }

    /**
     * 用户确认设备分享
     *
     * @param inviteCode 分享ID
     */
    public void acceptShare(String inviteCode, final ResultCallback callback) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("invite_code", inviteCode);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        post(acceptShareUrl, headers, params, callback);
    }

    /**
     * 获取设备信息
     *
     * @param deviceId 设备ID
     */
    public void getDevice(String productIdd, int deviceId, final ResultCallback callback) {
        String url = getDeviceUrl.replace("{device_id}", deviceId + "");
        url = url.replace("{product_id}", productIdd);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        get(url, headers, callback);
    }

    /**
     * http //.获取某个用户绑定的设备列表。
     */
    public void getSubscribeList(int uid, int versionid, final ResultCallback callback) {
        String url = String.format(subscribeListUrl, uid);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        get(url, headers, callback);
    }

    /**
     * http //.获取某个用户绑定的设备列表。
     */
    public void deleteShare(String inviteCode, final ResultCallback callback) {
        String url = deleteShareUrl.replace("{invite_code}", inviteCode);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Token", MyApp.getApp().getAccessToken());
        delete(url, headers, callback);
    }

//    public void checkUpdate(String deviceId,final ResultCallback callback){
//        String url = checkUpdateUrl;
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("deviceid",deviceId);
//        Header[] headers = new Header[3];
//        String data = new Gson().toJson(map);
//        Map<String,String> header = new HashMap<String, String>();
//        // AccessID
//        header.put("X-AccessId", HttpAgent.UPDATE_ACCESS_ID);
//        header.put("X-ContentMD5", HttpAgent.MD5(data));
//        header.put("X-Sign", HttpAgent.MD5(HttpAgent.UPDATE_SECRET_KEY + HttpAgent.MD5(data)));
//        post(url, header, map, callback);
//    }
//
//    public void upgrade(String deviceId,final ResultCallback callback){
//        String url = upgradeUrl;
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("deviceid",deviceId);
//        Header[] headers = new Header[3];
//        String data = new Gson().toJson(map);
//        Map<String,String> header = new HashMap<String, String>();
//        // AccessID
//        header.put("X-AccessId", HttpAgent.UPDATE_ACCESS_ID);
//        header.put("X-ContentMD5", HttpAgent.MD5(data));
//        header.put("X-Sign", HttpAgent.MD5(HttpAgent.UPDATE_SECRET_KEY + HttpAgent.MD5(data)));
//        post(url, header, map, callback);
//    }
//=========================================================================================

    private void post(String url, Map<String, String> params, ResultCallback callback) {
        // 请求entity
        StringEntity entity = params2StringEntity(params);
        client.post(MyApp.getApp(), url, entity, "application/json", callback);
    }

    private void get(String url, Map<String, String> headers, ResultCallback callback) {
        Header[] headersdata = map2Header(headers);
        client.get(MyApp.getApp(), url, headersdata, null, callback);
    }

    private void delete(String url, Map<String, String> headers, ResultCallback callback) {
        Header[] headersdata = map2Header(headers);
        client.delete(MyApp.getApp(), url, headersdata, null, callback);
    }

    private void post(String url, Map<String, String> headers, Map<String, String> params, ResultCallback callback) {
        // 请求entity
        StringEntity entity = params2StringEntity(params);
        Header[] headersdata = map2Header(headers);
        client.post(MyApp.getApp(), url, headersdata, entity, "application/json", callback);
    }

    private void put(String url, Map<String, String> headers, Map<String, String> params, ResultCallback callback) {
        // 请求entity
        StringEntity entity = params2StringEntity(params);
        Header[] headersdata = map2Header(headers);
        client.put(MyApp.getApp(), url, headersdata, entity, "application/json", callback);
    }

    private StringEntity params2StringEntity(Map<String, String> params) {
        StringEntity entity = null;
        try {
            entity = new StringEntity(new Gson().toJson(params), "UTF-8");
        } catch (Exception e) {
        }
        return entity;
    }

    private Header[] map2Header(Map<String, String> headers) {
        if (headers == null) {
            return null;
        }
        Header[] headersdata = new Header[headers.size()];
        int i = 0;
        for (String key : headers.keySet()) {
            headersdata[i] = new XHeader(key, headers.get(key));
            i++;
        }
        return headersdata;
    }

    public static abstract class ResultCallback<T> extends TextHttpResponseHandler {
        Type mType;
        private Gson mGson;

        public ResultCallback() {
            mType = getSuperclassTypeParameter(getClass());
            mGson = new Gson();
        }

        @Override
        public void onFailure(int code, Header[] headers, String msg, Throwable throwable) {
            if (code > 0) {
                try {
                    ErrorEntity errorEntity = mGson.fromJson(msg, ErrorEntity.class);
                    onError(headers, errorEntity.error);
                } catch (Exception e) {
                    ErrorEntity errorEntity = new ErrorEntity();
                    errorEntity.error.setMsg(throwable.getMessage());
                    errorEntity.error.setCode(HttpConstant.PARAM_NETIO_ERROR);
                    onError(headers, errorEntity.error);
                }
            } else {
                ErrorEntity errorEntity = new ErrorEntity();
                errorEntity.error.setMsg(throwable.getMessage());
                errorEntity.error.setCode(HttpConstant.PARAM_NETIO_ERROR);
                onError(headers, errorEntity.error);
            }
        }

        @Override
        public void onSuccess(int code, Header[] headers, String msg) {
            if (mType == String.class) {
                onSuccess(code, (T) msg);
            } else {
                T o = mGson.fromJson(msg, mType);
                onSuccess(code, o);
            }
        }


        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();
            System.out.println(superclass);
            if (superclass instanceof Class) {
                System.out.println(superclass);
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onError(Header[] headers, Error error);

        public abstract void onSuccess(int code, T response);
    }


    public static class Error {
        private int code;
        private String msg;

        public void setCode(int code) {
            this.code = code;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    private static class ErrorEntity {
        public Error error;

        public ErrorEntity() {
            error = new Error();
        }
    }
}
