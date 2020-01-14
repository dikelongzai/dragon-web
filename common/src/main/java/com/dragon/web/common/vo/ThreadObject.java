package com.dragon.web.common.vo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-07 12:17
 */
public class ThreadObject  implements Serializable, Cloneable{
    private static final long serialVersionUID = -3629427800325977202L;
    private String requestURL = null;
    private String moduleName = null;
    private String threadUID = null;
    private Date createTime = null;
    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getThreadUID() {
        return threadUID;
    }

    public void setThreadUID(String threadUID) {
        this.threadUID = threadUID;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }



}
