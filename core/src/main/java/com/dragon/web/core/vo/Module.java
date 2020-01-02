package com.dragon.web.core.vo;

import org.dom4j.Element;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-02 18:54
 */
public class Module implements Serializable {
    private static final long serialVersionUID = 6237822176071110880L;
    public static final String PROPERTY_SERVICE = "service";
    public static final String PROPERTY_DATASOURCE = "datasource";
    public static final String PROPERTY_ENCODING = "encoding";
    protected static final String MODULE_NAME = "name";
    protected static final String MODULE_PROPERTY = "property";
    //模块属性缓存
    private Map<String, String> props = new HashMap();
    //模块名称
    private String name = "";

    public Module(String name) {
        this.name = name;
    }
    /**
     * Element 构造模块Element 必要参数 [name property]
     * @param el
     */
    public Module(Element el) {
        this.name = el.attributeValue(MODULE_NAME);
        List lst = el.selectNodes(MODULE_PROPERTY);
        if (lst != null) {
            Iterator iterator = lst.iterator();
            while (iterator.hasNext()) {
                Element element = (Element) iterator.next();
                this.props.put(element.attributeValue(MODULE_NAME), element.getTextTrim());
            }
        }

    }

    public String getName() {
        return this.name;
    }

    public void setPropertyValue(String name, String value) {
        this.props.put(name, value);
    }

    public String getPropertyValue(String name) {
        return (String) this.props.get(name);
    }
}
