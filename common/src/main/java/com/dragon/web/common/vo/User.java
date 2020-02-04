package com.dragon.web.common.vo;

import com.dragon.web.common.util.MapUtil;
import com.dragon.web.common.util.StringUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 简化用户模型 {user_id,login_id,user_name,ip,status} 其余属性用values 扩展属性 extValues
 *
 * @author dikelongzai 15399073387@163.com
 * @date 2020-02-04 20:05
 */
public class User implements Cloneable, Serializable {
    private static final long serialVersionUID = -8031360378913624238L;
    public static final String USER_ID = "USER_ID";
    public static final String LOGIN_ID = "LOGIN_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String IP = "IP";
    public static final String STATUS = "STATUS";
    public static final String LAST_LOGIN_TIME = "LAST_LOGIN_TIME";
    public static final String MUST_UPDATE_PWD = "MUST_UPDATE_PWD";
    private Map<String, String> values = new HashMap();
    private Map<String, String> extValues = new HashMap();
    public static final String STATUS_NORMAL = "1";
    public static final String STATUS_FREEZED = "2";
    public static final String STATUS_LOCKED = "3";
    private Date lastActiveTime = new Date();
    public boolean isLogined = false;
    private String local = "zh-CN";

    public String getLocal() {
        return local;
    }

    public boolean isLogined() {
        return isLogined;
    }

    private boolean valueChanged = false;
    private String sessionId = "";

    public User() {
        this.setLogined(false);
        this.setLoginTime(new Date());

    }
    public void setIp(String ip) {
        this.values.put(IP, ip);
    }

    public User(Map<String, String> values) {
        this.setLogined(false);
        this.values = values;

    }

    public Object clone() {
        User u = new User();
        this.copyTo(u, false);
        return u;
    }

    public String getUserId() {
        return (String) this.values.get(USER_ID);
    }

    public String getLoginId() {
        return (String) this.values.get(LOGIN_ID);
    }

    public String getUserName() {
        return (String) this.values.get(USER_NAME);
    }

    public String getIp() {
        return (String) this.values.get(IP);
    }

    public String getStatus() {
        return (String) this.values.get(STATUS);
    }

    protected void setStatus(String status) {
        this.valueChanged = true;
        this.values.put(STATUS, status);
    }

    public void setLoginTime(Date loginTime) {
        this.valueChanged = true;
        this.values.put(LAST_LOGIN_TIME, StringUtil.dateToString(loginTime));
    }

    public Date getLoginTime() {
        return StringUtil.toDate((String) this.values.get("LAST_LOGIN_TIME"));
    }

    public void setMustUpdatePWD(boolean mustUpdatePWD) {
        this.valueChanged = true;
        this.values.put(MUST_UPDATE_PWD, mustUpdatePWD ? "1" : "0");
    }

    public boolean isMustUpdatePWD() {
        return StringUtil.isTrue((String) this.values.get("MUST_UPDATE_PWD"));
    }

    public Date getLastActiveTime() {
        return this.lastActiveTime;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setValue(String key, String value) {
        this.valueChanged = true;
        this.values.put(key, value);
    }

    public String getExtendValue(String key) {
        this.valueChanged = true;
        return (String) this.extValues.get(key);
    }

    public void setExtendValue(String key, String value) {
        this.valueChanged = true;
        this.extValues.put(key, value);
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public void setLogined(boolean isLogined) {
        this.isLogined = isLogined;
    }

    public void setLastActiveTime(Date time) {
        if (this.lastActiveTime.compareTo(time) < 0) {
            this.lastActiveTime = time;
        }

    }

    public void copyTo(User user, boolean valueChanged) {
        if (this != user) {
            synchronized (this) {
                user.valueChanged = valueChanged;
                user.setSessionId(this.sessionId);
                user.setLogined(this.isLogined);
                user.setLocal(this.local);
                user.setLastActiveTime(this.lastActiveTime);
                user.values = MapUtil.cloneMap(this.values);
                user.extValues = MapUtil.cloneMap(this.extValues);

            }
        }
    }


    public boolean isValueChanged() {
        return valueChanged;
    }

    public void setValueChanged(boolean valueChanged) {
        this.valueChanged = valueChanged;
    }
}
