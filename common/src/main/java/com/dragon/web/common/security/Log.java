package com.dragon.web.common.security;


import java.util.Properties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
/**
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-28 15:30
 */
public class Log {
    private static Logger log = null;
    private static boolean logDebug = true;
    private static boolean logWarn = true;
    private static boolean logException = true;
    private static boolean logBiz = true;
    private static DBLogInf dbLog = null;
    public static long maxLongTimeMilliseconds = 100000L;
    public static int maxBigDataSize = 8000;

    public Log() {
    }

    public static boolean isLogDebug() {
        return logDebug;
    }

    public static void setLogDebug(boolean logDebug) {
        logDebug = logDebug;
    }

    public static boolean isLogWarn() {
        return logWarn;
    }

    public static void setLogWarn(boolean logWarn) {
        logWarn = logWarn;
    }

    public static boolean isLogException() {
        return logException;
    }

    public static void setLogException(boolean logException) {
        logException = logException;
    }

    public static boolean isLogBiz() {
        return logBiz;
    }

    public static void setLogBiz(boolean logBiz) {
        logBiz = logBiz;
    }

    public static DBLogInf getDBLog() {
        return dbLog;
    }

    public static void setDBLog(DBLogInf dbLog) {
        dbLog = dbLog;
    }

    public static long getMaxLongTimeMilliseconds() {
        return maxLongTimeMilliseconds;
    }

    public static void setMaxLongTimeMilliseconds(long maxLongTimeMilliseconds) {
        maxLongTimeMilliseconds = maxLongTimeMilliseconds;
    }

    public static int getMaxBigDataSize() {
        return maxBigDataSize;
    }

    public static void setMaxBigDataSize(int maxBigDataSize) {
        maxBigDataSize = maxBigDataSize;
    }

    public static void init(Properties props) {
        try {
            LogManager.resetConfiguration();
            PropertyConfigurator.configure(props);
            log = Logger.getRootLogger();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static void debug(String msg, Throwable e) {
        if (logDebug) {
            if (log != null) {
                if (e != null) {
                    log.error(msg, e);
                } else {
                    log.debug(msg);
                }
            } else {
                System.out.println(msg);
            }

            if (e != null) {
                e.printStackTrace();
            }
        }

    }

    public static void debug(Object msg) {
        if (logDebug) {
            if (log != null) {
                log.debug(msg);
            } else {
                System.out.println(msg);
            }

            if (msg instanceof Throwable) {
                ((Throwable)msg).printStackTrace();
                if (log != null) {
                    log.error("", (Throwable)msg);
                }
            }
        }

    }

    public static void warn(Object msg) {
        if (logWarn) {
            if (log != null) {
                log.warn(msg);
            } else {
                System.out.println(msg);
            }

            if (dbLog != null) {
                if (msg instanceof Throwable) {
                    dbLog.logException((Throwable)msg, "");
                } else {
                    dbLog.logException((Throwable)null, msg == null ? "" : msg.toString());
                }
            }
        }

        debug(msg);
    }

    public static void exception(Object msg) {
        if (logException) {
            if (log != null) {
                log.error(msg);
            } else {
                System.out.println(msg);
            }

            if (dbLog != null) {
                if (msg instanceof BizException) {
                    dbLog.logException((Throwable)null, ((BizException)msg).getMessage());
                } else if (msg instanceof Throwable) {
                    dbLog.logException((Throwable)msg, "");
                } else {
                    dbLog.logException((Throwable)null, msg == null ? "" : msg.toString());
                }
            }
        }

        debug(msg);
    }

    public static boolean logLongTime(long millisecondsStart, long millisecondsEnd, String message) {
        if (millisecondsEnd - millisecondsStart >= maxLongTimeMilliseconds) {
            dbLog.logLongTime(millisecondsStart, millisecondsEnd, message);
            return true;
        } else {
            return false;
        }
    }

    public static boolean logBigData(int size, String message) {
        if (size >= maxBigDataSize) {
            if (dbLog != null) {
                dbLog.logBigData(size, message);
            }

            return true;
        } else {
            return false;
        }
    }
}
