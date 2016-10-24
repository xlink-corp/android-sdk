package io.xlink.wifi.pipe.http;

/**
 * Created by MYFLY on 2016/1/5.
 */
public class HttpConstant {

    public static final int PARAM_SUCCESS = 200;

    /*******************************错误码分隔符 ********************************/
    /**
     * 网络IO错误
     */
    public static final int PARAM_NETIO_ERROR = 1001001;
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /**
     * HTTP 400 下返回的错误码
     */
    /**
     * 请求数据字段验证不通过
     */
    public static final int PARAM_VALID_ERROR = 4001001;
    /**
     * 请求数据必须字段不可为空
     */
    public static final int PARAM_MUST_NOT_NULL = 4001002;
    /**
     * 手机验证码不存在
     */
    public static final int PHONE_VERIFYCODE_NOT_EXISTS = 4001003;
    /**
     * 手机验证码错误
     */
    public static final int PHONE_VERIFYCODE_ERROR = 4001004;
    /**
     * 注册的手机号已存在
     */
    public static final int REGISTER_PHONE_EXISTS = 4001005;
    /**
     * 注册的邮箱已存在
     */
    public static final int REGISTER_EMAIL_EXISTS = 4001006;
    /**
     * 密码错误
     */
    public static final int ACCOUNT_PASSWORD_ERROR = 4001007;
    /**
     * 帐号不合法
     */
    public static final int ACCOUNT_VAILD_ERROR = 4001008;
    /**
     * 企业成员状态不合法
     */
    public static final int MEMBER_STATUS_ERROR = 4001009;
    /**
     * 刷新token不合法
     */
    public static final int REFRESH_TOKEN_ERROR = 4001010;
    /**
     * 未知成员角色类型
     */
    public static final int MEMBER_ROLE_TYPE_UNKOWN = 4001011;
    /**
     * 只有管理员才能邀请
     */
    public static final int MEMBER_INVITE_NOT_ADMIN = 4001012;
    /**
     * 不可修改其他成员信息
     */
    public static final int CAN_NOT_MODIFY_OTHER_MEMBER_INFO = 4001013;
    /**
     * 不能删除本人
     */
    public static final int CAN_NOT_DELETE_YOURSELF = 4001014;
    /**
     * 未知的产品连接类型
     */
    public static final int PRODUCT_LINK_TYPE_UNKOWN = 4001015;
    /**
     * 已发布的产品不可删除
     */
    public static final int CAN_NOT_DELETE_RELEASE_PRODUCT = 4001016;
    /**
     * 固件版本已存在
     */
    public static final int FIRMWARE_VERSION_EXISTS = 4001017;
    /**
     * 数据端点未知数据类型
     */
    public static final int DATAPOINT_TYPE_UNKOWN = 4001018;
    /**
     * 数据端点索引已存在
     */
    public static final int DATAPOINT_INDEX_EXISTS = 4001019;
    /**
     * 已发布的数据端点不可删除
     */
    public static final int CANT_NOT_DELETE_RELEASED_DATAPOINT = 4001020;
    /**
     * 该产品下设备MAC地址已存在
     */
    public static final int DEVICE_MAC_ADDRESS_EXISTS = 4001021;
    /**
     * 不能删除已激活的设备
     */
    public static final int CAN_NOT_DELETE_ACTIVATED_DEVICE = 4001022;
    /**
     * 扩展属性Key为预留字段
     */
    public static final int PROPERTY_KEY_PROTECT = 4001023;
    /**
     * 设备扩展属性超过上限
     */
    public static final int PROPERTY_LIMIT = 4001024;
    /**
     * 新增已存在的扩展属性
     */
    public static final int PROPERTY_ADD_EXISTS = 4001025;
    /**
     * 更新不存在的扩展属性
     */
    public static final int PROPERTY_UPDATE_NOT_EXISTS = 4001026;
    /**
     * 属性字段名不合法
     */
    public static final int PROPERTY_KEY_ERROR = 4001027;
    /**
     * 邮件验证码不存在
     */
    public static final int EMAIL_VERIFYCODE_NOT_EXISTS = 4001028;
    /**
     * 邮件验证码错误
     */
    public static final int EMAIL_VERIFYCODE_ERROR = 4001029;
    /**
     * 用户状态不合法
     */
    public static final int USER_STATUS_ERROR = 4001030;
    /**
     * 用户手机尚未认证
     */
    public static final int USER_PHONE_NOT_VAILD = 4001031;
    /**
     * 用户邮箱尚未认证
     */
    public static final int USER_EMAIL_NOT_VAILD = 4001032;
    /**
     * 用户已经订阅设备
     */
    public static final int USER_HAS_SUBSCRIBE_DEVICE= 4001033;
    /**
     * 用户没有订阅该设备
     */
    public static final int USER_HAVE_NO_SUBSCRIBE_DEVICE = 4001034;
    /**
     * 自动升级任务名称已存在
     */
    public static final int UPGRADE_TASK_NAME_EXISTS = 4001035;
    /**
     * 升级任务状态未知
     */
    public static final int UPGRADE_TASK_STATUS_UNKOWN = 4001036;
    /**
     * 已有相同的起始版本升级任务
     */
    public static final int UPGRADE_TASK_HAVE_STARTING_VERSION = 4001037;
    /**
     * 设备激活失败
     */
    public static final int DEVICE_ACTIVE_FAIL = 4001038;
    /**
     * 设备认证失败
     */
    public static final int DEVICE_AUTH_FAIL = 4001039;
    /**
     * 订阅设备认证码错误
     */
    public static final int SUBSCRIBE_AUTHORIZE_CODE_ERROR = 4001041;
    /**
     * 授权名称已存在
     */
    public static final int EMPOWER_NAME_EXISTS = 4001042;
    /**
     * 该告警规则名称已存在
     */
    public static final int ALARM_RULE_NAME_EXISTS = 4001043;
    /**
     * 数据变名称已存在
     */
    public static final int DATA_TABLE_NAME_EXISTS = 4001045;
    /**
     * 产品固件文件超过大小限制
     */
    public static final int PRODUCT_FIRMWARE_FILE_SIZE_LIMIT = 4001046;
    /**
     * apn密钥文件超过大小限制
     */
    public static final int APP_APN_LICENSE_FILE_SIZE_LIMIT = 4001047;
    /**
     * APP的APN功能未启用
     */
    public static final int APP_APN_IS_NOT_ENABLE = 4001048;
    /**
     * 产品未允许用户注册设备
     */
    public static final int PRODUCT_CAN_NOT_REGISTER_DEVICE = 4001049;
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /**
     * HTTP 403 下返回的错误码
     */
    /**
     * 禁止访问
     */
    public static final int INVALID_ACCESS = 4031001;
    /**
     * 禁止访问，需要Access-Token
     */
    public static final int NEED_ACCESS_TOKEN = 4031002;
    /**
     * 无效的Access-Token
     */
    public static final int ACCESS_TOKEN_INVALID = 4031003;
    /**
     * 需要企业的调用权限
     */
    public static final int NEED_CORP_API= 4031004;
    /**
     * 需要企业管理员权限
     */
    public static final int NEED_CORP_ADMIN_MEMBER = 4031005;
    /**
     * 需要数据操作权限
     */
    public static final int NEED_DATA_PERMISSION = 4031006;
    /**
     * 禁止访问私有数据
     */
    public static final int INVAILD_ACCESS_PRIVATE_DATA = 4031007;
    /**
     * 分享已经被取消
     */
    public static final int SHARE_CANCELED = 4031008;
    /**
     * 分享已经接受
     */
    public static final int SHARE_ACCEPTED = 4031009;

    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /**
     * HTTP 404 下返回的错误码
     */
    /**
     * URL找不到
     */
    public static final int URL_NOT_FOUND = 4041001;
    /**
     * 企业成员帐号不存在
     */
    public static final int MEMBER_ACCOUNT_NO_EXISTS = 4041002;
    /**
     * 企业成员不存在
     */
    public static final int MEMBER_NOT_EXISTS = 4041003;
    /**
     * 激活的成员邮箱不存在
     */
    public static final int MEMBER_INVITE_EMAIL_NOT_EXISTS = 4041004;
    /**
     * 产品信息不存在
     */
    public static final int PRODUCT_NOT_EXISTS = 4041005;
    /**
     * 产品固件不存在
     */
    public static final int FIRMWARE_NOT_EXISTS = 4041006;
    /**
     * 数据端点不存在
     */
    public static final int DATAPOINT_NOT_EXISTS = 4041007;
    /**
     * 设备不存在
     */
    public static final int DEVICE_NOT_EXISTS = 4041008;
    /**
     * 设备扩展属性不存在
     */
    public static final int DEVICE_PROPERTY_NOT_EXISTS  = 4041009;
    /**
     * 企业不存在
     */
    public static final int CORP_NOT_EXISTS = 4041010;
    /**
     * 用户不存在
     */
    public static final int USER_NOT_EXISTS = 4041011;
    /**
     * 用户扩展属性不存在
     */
    public static final int USER_PROPERTY_NOT_EXISTS = 4041012;
    /**
     * 升级任务不存在
     */
    public static final int UPGRADE_TASK_NOT_EXISTS = 4041013;
    /**
     * 第三方身份授权不存在
     */
    public static final int EMPOWER_NOT_EXISTS = 4041014;
    /**
     * 告警规则不存在
     */
    public static final int ALARM_RULE_NOT_EXISTS = 4041015;
    /**
     * 数据表不存在
     */
    public static final int DATA_TABLE_NOT_EXISTS = 4041016;
    /**
     * 数据不存在
     */
    public static final int DATA_NOT_EXISTS = 4041017;
    /**
     * 分享资源不存在
     */
    public static final int SHARE_NOT_EXISTS = 4041018;
    /**
     * 企业邮箱不存在
     */
    public static final int CORP_EMAIL_NOT_EXISTS = 4041019;
    /**
     * APP不存在
     */
    public static final int APP_NOT_EXISTS = 4041020;


    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /*******************************错误码分隔符 ********************************/
    /**
     * HTTP 503下返回的错误码
     */
    /**
     * 服务端发生异常
     */
    public static final int SERVICE_EXCEPTION = 5031001;


}
