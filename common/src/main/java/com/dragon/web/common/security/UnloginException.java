package com.dragon.web.common.security;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-02-04 20:52
 */
public class UnloginException extends Exception {
    private static final long serialVersionUID = 4826829570756949501L;
    private String requestUrl = "";

    public UnloginException() {
        super("可能您的帐户已在其他地方登录，或者是登录已过期。 ");
    }

    public UnloginException(String msg) {
        super(msg);
    }

    public UnloginException(String msg, String requestUrl) {
        super(msg);
        this.requestUrl = requestUrl;
    }

    public String getRequestUrl() {
        return this.requestUrl;
    }
}
