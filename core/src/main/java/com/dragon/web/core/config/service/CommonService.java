package com.dragon.web.core.config.service;

import com.dragon.web.common.security.Log;
import com.dragon.web.common.util.StringUtil;
import com.dragon.web.common.vo.ParameterObject;
import com.dragon.web.common.vo.ResultObject;
import com.dragon.web.common.vo.ThreadObject;
import com.dragon.web.core.ResourcePool;
import com.dragon.web.core.SystemManager;
import com.dragon.web.db.util.DBConnection;
import com.dragon.web.db.util.DataSource;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-15 13:49
 */
public abstract class CommonService implements CommonServiceLocalInf, CommonServiceRemoteInf {
    protected ParameterObject po = null;
    protected ResultObject ro = null;
    protected DBConnection dbc = null;
    protected ThreadObject to = null;

    public ParameterObject getParameterObject() {
        return this.po;
    }

    public ResultObject getResultObject() {
        return this.ro;
    }

    public ResultObject call(ParameterObject param) {
        this.po = null;
        this.ro = null;
        this.dbc = null;
        long thisMillis = System.currentTimeMillis();
        this.ro = new ResultObject();
        this.po = param;
        this.to = this.po.getThreadObject() == null ? ResourcePool.getCurrentThreadObject() : this.po.getThreadObject();
        boolean sameThread = StringUtil.null2blank(this.to.getThreadUID()).equals(ResourcePool.getCurrentThreadObject().getThreadUID());
        ResourcePool.setCurrentThreadObject(this.to);
        this.dbc = DataSource.getDBConnection(SystemManager.getModule(this.to.getModuleName()).getPropertyValue("datasource"));
        ResourcePool.closeAllOpenedDBConnection(thisMillis);
        try {
            this.execute();
            this.dbc.commitTransaction();
        } catch (Exception e) {
            Log.exception(e);
            this.ro.setMessage(e.getMessage(), -1);
            this.ro.setValue((Object) null);
            if (this.dbc != null) {
                this.dbc.rollbackTransaction();
            }
        } finally {
            if (this.dbc != null) {
                this.dbc.close();
            }
            ResourcePool.closeAllOpenedDBConnection(thisMillis);
            if (!sameThread) {
                ResourcePool.removeAllThreadLocal();
            }
        }


        return this.ro;
    }

    protected abstract void execute() throws Exception;
}
