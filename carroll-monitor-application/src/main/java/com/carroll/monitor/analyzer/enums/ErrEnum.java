package com.carroll.monitor.analyzer.enums;

/**
 * @author: carroll
 * @date 2019/9/9
 */
public enum ErrEnum {
    LOGIN_ERROR("AUTH.0001", "用户名或密码错误"),
    NOT_LOGIN_ERROR("AUTH.0002", "未登录"),
    LOG_OUT_ERROR("AUTH.0003", "注销登录失败"),
    NO_PERMISSION_ERROR("AUTH.0004", "无权执行此操作"),
    PWD_ERROR("AUTH.0005", "密码错误"),
    USER_NOT_EXISTS("USER.0001", "用户不存在"),
    USER_EXISTS("USER.0002", "用户已存在"),
    DATA_NOT_EXIST("COMMON.0000", "数据不存在"),
    DATA_EXIST("COMMON.0001", "数据存在"),
    PROJECT_TAG_ALREADY_EXIST("PROJECT.0001", "监控项目tag已存在"),
    ITEM_TAG_ALREADY_EXIST("ITEM.0001", "监控项tag已存在"),

    SERVICE_UNAVAILABLE("M_0001", "服务不可用"),
    SIGN_ERROR("M_0002", "签名错误"),
    PROJECT_NOT_EXIST("M_0003", "监控项目不存在"),
    PROJECT_ID_ERROR("M_0004", "监控项目有误"),
    WARNINGDATA_NOT_EXIST("M_0005", "告警数据不存在"),
    WARNINGDATA_PROJECT_NOT_MATCH("M_0006", "告警数据与监控项目不匹配"),
    OPERATOR_EMAIL_ALREADY_EXIST("M_0007", "运营人员邮箱已存在"),
    OPERATOR_SMS_ALREADY_EXIST("M_0008", "运营人员电话已存在"),
    OPERATOR_ID_NOT_BLANK("M_0009", "id不能为空"),
    MONITORITEM_PROJECT_NOT_MATCH("M_0010", "告警内容不属于该项目"),
    MONITORITEM_CYCLE_ERROR("M_0011", "重复告警周期，请输入5的倍数数字"),
    OPERATOR_NOT_EXIST("M_0013", "运营人员不存在"),
    OPERATOR_PROJECT_NOT_MATCH("M_0014", "运营人员与监控项目不匹配"),
    MONITORITEM_OPERATOR_ALREADY_BIND("M_0015", "运营人员与监控项目已绑定，删除运营人员失败"),
    WARNINGDATA_STATUS_NOT_NULL("M_0016", "告警数据状态必填");

    private String errCode;
    private String errMsg;

    ErrEnum(String errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getCode() {
        return errCode;
    }

    public String getMsg() {
        return errMsg;
    }
}
