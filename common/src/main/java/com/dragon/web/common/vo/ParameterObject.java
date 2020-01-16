package com.dragon.web.common.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-15 13:37
 */
public class ParameterObject implements Serializable {
    private static final long serialVersionUID = 2132169905032238936L;

    private Map parameters = null;
    //    private User user = null;
    private ThreadObject threadObject = null;

    public ThreadObject getThreadObject() {
        return this.threadObject;
    }

    public void setThreadObject(ThreadObject t) {
        this.threadObject = t;
    }

    public ParameterObject(Map params) {
        this.parameters = new HashMap();
    }

}
