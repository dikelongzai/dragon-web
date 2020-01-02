package com.dragon.web.common.util;

import java.util.*;
import org.dom4j.Attribute;
import org.dom4j.Element;

/**
 * xml解析工具
 * @author dikelongzai 15399073387@163.com
 * @date 2020-01-02 17:51
 */
public class XMLUtil {
    private static final String[] xmlCode = new String[256];

    static {
        xmlCode[39] = "&apos;";
        xmlCode[34] = "&quot;";
        xmlCode[38] = "&amp;";
        xmlCode[60] = "&lt;";
        xmlCode[62] = "&gt;";
    }

    public XMLUtil() {
    }

    public static String encode(String string) {
        if (string == null) {
            return "";
        } else {
            StringBuffer buffer = new StringBuffer();

            for(int i = 0; i < string.length(); ++i) {
                char character = string.charAt(i);
                try {
                    String xmlchar = xmlCode[character];
                    if (xmlchar == null) {
                        buffer.append(character);
                    } else {
                        buffer.append(xmlCode[character]);
                    }
                } catch (ArrayIndexOutOfBoundsException exception) {
                    buffer.append(character);
                }
            }

            return buffer.toString();
        }
    }

    /**
     * 添加节点属性
     * @param element
     * @param name
     * @param value
     */
    public static void setAttributeValue(Element element, String name, String value) {
        if (element != null && !StringUtil.isEmpty(name)) {
            if (element.attribute(name) != null) {
                element.attribute(name).setValue(StringUtil.null2blank(value));
            } else {
                element.addAttribute(name, StringUtil.null2blank(value));
            }

        }
    }

    /**
     * Element删除节点
     * @param element
     *          XML element
     * @param name
     *          节点名称
     */
    public static void delAttribute(Element element, String name) {
        if (element != null) {
            if (element.attribute(name) != null) {
                element.remove(element.attribute(name));
            }
        }
    }

    /**
     * 添加节点
     * eg:<elementName>text</elementName>
     * @param parent 父节点
     * @param name elementName
     * @param text 名称
     *
     */
    public static void setElementText(Element parent, String name, String text) {
        if (parent.element(name) != null) {
            parent.element(name).setText(text);
        } else {
            parent.addElement(name).setText(text);
        }

    }

    /**
     * 获取节点属性值
     * @param element
     * @param name
     * @return
     */
    public static String getAttributeValue(Element element, String name) {
        if (element == null) {
            return "";
        } else {
            String attr = element.attributeValue(name);
            if (attr == null) {
                attr = "";
            }

            return attr;
        }
    }

    /**
     * 获取 Element text
     * @param element
     * @return
     */
    public static String getText(Element element) {
        if (element == null) {
            return "";
        } else {
            String text = element.getText();
            if (text == null) {
                text = "";
            }

            return text;
        }
    }

    /**
     * 子节点获取
     * @param element
     * @param childNodeName
     *              子节点名称
     * @return
     */
    public static String getChildText(Element element, String childNodeName) {
        if (element != null && !StringUtil.isEmpty(childNodeName) && element.selectSingleNode(childNodeName) != null) {
            String text = element.selectSingleNode(childNodeName).getText();
            if (text == null) {
                text = "";
            }

            return text;
        } else {
            return "";
        }
    }

    /**
     * 添加子节点
     * @param element
     * @param childNodeName
     * @param text
     */
    public static void setChildText(Element element, String childNodeName, String text) {
        if (element != null && !StringUtil.isEmpty(childNodeName)) {
            if (element.selectSingleNode(childNodeName) == null) {
                element.addElement(childNodeName).setText(text);
            } else {
                element.selectSingleNode(childNodeName).setText(text);
            }

        }
    }

    /**
     *
     * @param lst
     * @return
     */
    private static int getNewIndex(List<String> lst) {
        for(int i = 0; i <= lst.size(); ++i) {
            if (lst.indexOf(String.valueOf(i)) < 0) {
                return i;
            }
        }

        return 0;
    }

    /**
     * 按照index排序
     * @param ele
     * @param childTag
     *         子节点标签
     */
    public static void sortChildByIndex(Element ele, String childTag) {
        if (ele != null && !StringUtil.isEmpty(childTag)) {
            List lst = ele.selectNodes(childTag);
            if (lst != null && lst.size() >= 2) {
                int[] lst2 = new int[lst.size()];
                int i1 = 0;
                int i2 = 0;
                List<String> lst3 = new ArrayList();
                for(int i = 0; i < lst.size(); ++i) {
                    Element ele2 = (Element)lst.get(i);
                    int index = StringUtil.parseInt(ele2.attributeValue("index"), -1);
                    if (index >= 0) {
                        if (lst3.indexOf(String.valueOf(index)) >= 0) {
                            index = getNewIndex(lst3);
                        }

                        lst3.add(String.valueOf(index));
                        lst2[i] = index;
                    } else if (index < 0) {
                        lst2[i] = -1;
                    }
                }

                int j;
                for(int i = 0; i < lst2.length; ++i) {
                    if (lst2[i] < 0) {
                        j = getNewIndex(lst3);
                        lst3.add(String.valueOf(j));
                        lst2[i] = j;
                    }

                    i1 = Math.min(i1, lst2[i]);
                    i2 = Math.max(i2, lst2[i]);
                }

                for(int i = i2; i >= i1; --i) {
                    for(j = 0; j < lst2.length; ++j) {
                        if (i == lst2[j]) {
                            Element ele2 = (Element)lst.get(j);
                            ele.remove(ele2);
                            ele.elements().add(0, ele2);
                        }
                    }
                }

            }
        }
    }

    /**
     * Element->Map<String, String>
     * @param el
     * @return
     */
    public static Map<String, String> getAttrMap(Element el) {
        Map<String, String> res = new HashMap();
        List lst = el.attributes();
        if (lst != null && lst.size() > 0) {
            for(int i = 0; i < lst.size(); ++i) {
                Attribute attr = (Attribute)lst.get(i);
                res.put(attr.getName(), attr.getValue());
            }
        }

        return res;
    }

    /**
     * add map<K,V> Element
     * @param el
     * @param m
     */
    public static void setAttrMap(Element el, Map<String, String> m) {
        if (m != null) {
            Iterator it = m.entrySet().iterator();

            while(it.hasNext()) {
                Map.Entry<String, String> en = (Map.Entry)it.next();
                setAttributeValue(el, (String)en.getKey(), (String)en.getValue());
            }

        }
    }
}
