package com.dragon.web.common.security;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-28 15:32
 */
public interface DBLogInf {
    void logBiz(String var1, Object var2);

    void logLogin();

    void logLogout();

    void logException(Throwable throwable, String str);

    void logLongTime(long var1, long var3, String var5);

    void logBigData(int var1, String var2);
}
