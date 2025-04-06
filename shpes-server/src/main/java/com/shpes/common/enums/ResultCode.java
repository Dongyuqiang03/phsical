package com.shpes.common.enums;

import com.shpes.common.api.IErrorCode;

/**
 * 常用API返回对象
 */
public enum ResultCode implements IErrorCode {
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    PARAM_ERROR(400, "参数错误"),
    
    // 用户相关：1000-1999
    USER_NOT_EXIST(1000, "用户不存在"),
    USERNAME_OR_PASSWORD_ERROR(1001, "用户名或密码错误"),
    USER_ACCOUNT_EXPIRED(1002, "账号已过期"),
    USER_ACCOUNT_LOCKED(1003, "账号已被锁定"),
    USER_ACCOUNT_DISABLED(1004, "账号已被禁用"),
    USER_DISABLED(1002, "账号已被禁用"),
    DUPLICATE_USERNAME(1005, "用户名已存在"),
    
    // 角色权限相关：2000-2999
    ROLE_NOT_EXIST(2000, "角色不存在"),
    ROLE_USED(2001, "角色已被使用"),
    PERMISSION_NOT_EXIST(2002, "权限不存在"),
    ROLE_NAME_EXISTS(2003, "角色名称已存在"),
    ROLE_CODE_EXISTS(2004, "角色编码已存在"),
    // 部门相关：3000-3999
    DEPARTMENT_NOT_EXIST(3000, "部门不存在"),
    DEPARTMENT_USED(3001, "部门已被使用"),
    
    // 体检项目相关：4000-4999
    EXAM_ITEM_NOT_EXIST(4000, "体检项目不存在"),
    EXAM_ITEM_USED(4001, "体检项目已被使用"),
    EXAM_PACKAGE_NOT_EXIST(4002, "体检套餐不存在"),
    EXAM_PACKAGE_USED(4003, "体检套餐已被使用"),
    
    // 预约相关：5000-5999
    APPOINTMENT_NOT_EXIST(5000, "预约记录不存在"),
    APPOINTMENT_TIME_CONFLICT(5001, "预约时间冲突"),
    APPOINTMENT_CAPACITY_FULL(5002, "预约名额已满"),
    APPOINTMENT_TIME_INVALID(5003, "无效的预约时间"),
    APPOINTMENT_CANCEL_TIMEOUT(5004, "超出取消预约时限"),
    APPOINTMENT_STATUS_INVALID(5013, "预约状态不允许此操作"),
    TIME_SLOT_NOT_EXIST(5005, "体检时间段不存在"),
    TIME_SLOT_EXIST(5006, "体检时间段已存在"),
    TIME_SLOT_BOOKED(5007, "体检时间段已被预约"),
    TIME_SLOT_FULL(5008, "体检时间段已满"),
    TIME_SLOT_EMPTY(5009, "体检时间段无预约"),
    TIME_SLOT_DATE_INVALID(5010, "时间段日期无效"),
    TIME_SLOT_TIME_INVALID(5011, "时间段时间无效"),
    TIME_SLOT_CAPACITY_INVALID(5012, "时间段容量无效"),
    
    // 体检结果相关：6000-6999
    EXAM_RECORD_NOT_EXIST(6000, "体检记录不存在"),
    EXAM_RESULT_NOT_EXIST(6001, "体检结果不存在"),
    EXAM_RESULT_ALREADY_SUBMITTED(6002, "体检结果已提交"),
    EXAM_RESULT_NOT_COMPLETE(6003, "体检结果未完成"),
    EXAM_RESULT_ALREADY_REVIEWED(6004, "体检结果已审核");

    private final long code;
    private final String message;

    ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}