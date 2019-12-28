package com.dragon.web.common.security;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-28 15:33
 */
public class BizException extends RuntimeException{
    private static final long serialVersionUID = -83213558840258115L;

    public BizException(String msg) {
        super(msg);
    }
}
