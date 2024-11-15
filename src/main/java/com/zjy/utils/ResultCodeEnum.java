package com.zjy.utils;

/**
 * 统一返回结果状态信息类
 */
public enum ResultCodeEnum {

    SUCCESS(200, "success"),
    USERACCOUNT_ERROR(501, "账户名不规范"),
    PASSWORD_ERROR(503, "密码不规范"),
    NOTLOGIN(504, "未登录"),
    MESSAGE_USED(505, "信息被占用"),
    MESSAGE_NUll(506,"请填写完整信息"),
    PHONE_ERROR(507,"电话格式错误"),
    LOGIN_ERROR(508,"账户或密码错误/未注册！"),
    NO_GLY(509,"需要管理员权限"),
    DELETE_ERROE(510,"删除失败"),
    BLACK_ACCOUNT(511,"封号用户！");

    private Integer code;
    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}