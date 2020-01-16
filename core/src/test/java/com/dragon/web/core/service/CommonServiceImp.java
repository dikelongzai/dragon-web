package com.dragon.web.core.service;

import com.dragon.web.common.security.Log;
import com.dragon.web.core.config.service.CommonService;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-16 16:19
 */
public class CommonServiceImp extends CommonService {
    @Override
    protected void execute() throws Exception {
        String str=this.dbc.selectDataBySql("SELECT 1 FROM DUAL");
        Log.debug(str);
    }
}
