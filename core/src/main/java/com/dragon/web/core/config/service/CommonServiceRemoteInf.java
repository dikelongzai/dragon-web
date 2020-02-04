package com.dragon.web.core.config.service;

import com.dragon.web.common.vo.ParameterObject;
import com.dragon.web.common.vo.ResultObject;

import javax.ejb.Remote;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-15 13:47
 */
@Remote
public interface CommonServiceRemoteInf {
    ResultObject call(ParameterObject parameter);
}
