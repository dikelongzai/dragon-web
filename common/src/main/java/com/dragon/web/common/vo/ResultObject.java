package com.dragon.web.common.vo;

import com.dragon.web.common.util.StringUtil;

import java.io.Serializable;

/**
 * ResultObject
 *
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-15 13:34
 */
public class ResultObject implements Serializable {
    private static final long serialVersionUID = 2851494523377731661L;
    private StringBuffer message = new StringBuffer();
    private int message_level = 0;
    public static final int MESSAGE_LEVEL_NORMAL = 0;
    public static final int MESSAGE_LEVEL_WARN = 1;
    public static final int MESSAGE_LEVEL_ERROR = -2;
    public static final int MESSAGE_LEVEL_EXCEPTION = -1;

    private Object value = null;

    public ResultObject() {

    }


    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return this.value;
    }

    public int getMessageLevel() {
        return this.message_level;
    }

    public String getMessage() {
        return this.message.toString();
    }

    public void setMessage(String msg) {
        this.message = new StringBuffer(StringUtil.null2blank(msg));
        this.message_level = 0;
    }

    public void setMessage(String msg, int level) {
        this.message = new StringBuffer(msg);
        this.message_level = level;
    }

    public void appendMessage(String msg) {
        this.message.append(StringUtil.null2blank(msg));
    }
}
