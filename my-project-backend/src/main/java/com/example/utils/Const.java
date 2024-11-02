package com.example.utils;


public final class Const {
    // JWT黑名单
    public final static String JWT_BLACK_LIST = "jwt:blacklist:";
    // JWT频率
    public final static String JWT_FREQUENCY = "jwt:frequency:";
    // 流量限制计数器
    public final static String FLOW_LIMIT_COUNTER = "flow:counter:";
    // 流量限制阻塞
    public final static String FLOW_LIMIT_BLOCK = "flow:block:";
    // 邮箱验证限制
    public final static String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    // 邮箱验证数据
    public final static String VERIFY_EMAIL_DATA = "verify:email:data:";
    // 订单流量限制
    public final static int ORDER_FLOW_LIMIT = -101;
    // 订单CORS
    public final static int ORDER_CORS = -102;
    // 用户ID
    public final static String ATTR_USER_ID = "userId";
    // 邮件队列
    public final static String MQ_MAIL = "mail";
    // 默认角色
    public final static String ROLE_DEFAULT = "user";

}
