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
    BLACK_ACCOUNT(511,"封号用户！"),
    CHANGE_REEOR(512,"更新失败！"),
    CDETAIL_ERROR(513,"完善信息失败！"),
    PASSWORD_RE(514,"与原密码相同！"),
    PASSWORD_BYY(515,"两次新密码不一致！"),
    PASSWORD_ERRORING(516,"密码输入错误！"),
    PHONE_ERRORING(517,"电话号码不正确！"),
    PHONE_RE(518,"新旧电话号码相同！"),
    UPDATE_ERROE(519,"没有值修改"),
    EMAILL_ERROR(520,"邮箱格式错误！");


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