package com.dragon.web.dd.config;

/**
 * 列查询类型
 * @author dikelongzai 15399073387@163.com
 * @date 2019-12-28 14:41
 */
public enum ColumnQueryType {
    auto,
    equal,
    like,
    startWith,
    endWith,
    between;

    private ColumnQueryType() {
    }
}
