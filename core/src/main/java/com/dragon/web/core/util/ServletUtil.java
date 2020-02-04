package com.dragon.web.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dragon.web.common.security.Log;
import com.dragon.web.common.security.UnloginException;
import com.dragon.web.common.util.MapUtil;
import com.dragon.web.common.util.StringUtil;
import com.dragon.web.common.vo.ResultObject;
import com.dragon.web.common.vo.ThreadObject;
import com.dragon.web.common.vo.User;
import com.dragon.web.core.ResourcePool;
import com.dragon.web.core.SystemManager;
import com.dragon.web.core.config.service.ServiceContext;
import lombok.val;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author dikelongzai 15399073387@163.com
 * @date 2020-02-04 20:04
 */
public class ServletUtil {
    public static final String KEY_PASSWORD = "password";
    private static final Map<String, User> all_session_users = new HashMap();
    public static User getUser(HttpServletRequest request) {
        User u = (User)request.getSession().getAttribute("KEY_USER_BIND_SESSION");
        if (u == null) {
            u = new User();
            request.getSession().setAttribute("KEY_USER_BIND_SESSION", u);
        }
        all_session_users.put(request.getSession().getId(), u);
        String local = request.getLocale().getLanguage() + "-" + request.getLocale().getCountry();
        u.setIp(request.getRemoteAddr());
        return u;
    }

    /**
     * 过期session处理
     * @param session
     */
//    public static void checkAllUserOnline() {
//        ResourcePool.getScheduledThreadPoolExecutor().schedule(new Runnable() {
//            public void run() {
//
//            }
//
//        } );
//
//    }

    public static void newSession(HttpSession session) {
    }

    public static int deleteSession(String id) {
        User u = (User)all_session_users.get(id);
        all_session_users.remove(id);
        if (u != null) {
            if (u.isLogined()) {
                ServiceContext.callService("base", "BaseUserLogout", (Map)null);
            }

            return 1;
        } else {
            return 0;
        }
    }

    public static Map<String, User> getAllOnlineUsers() {
        return all_session_users;
    }

    public static ResultObject callService(String module, String service, Map params, HttpServletRequest request) {
        ThreadObject to = ResourcePool.getCurrentThreadObject();
        Map map = getRequestParameterMap(request, to);
        MapUtil.copyMap(params, map, 0);
        return callService(module, service, map, getUser(request));
    }

    public static ResultObject callService(String module, String service, Map params, User user) {
        ThreadObject to = ResourcePool.getCurrentThreadObject();
        to.setModuleName(module);
        if (StringUtil.isEmpty(to.getThreadUID())) {
            to.setThreadUID(StringUtil.getUUID() + System.currentTimeMillis());
        }
        long dt1 = System.currentTimeMillis();
        ResultObject ro = ServiceContext.callService(module, service, params);
        long dt2 = System.currentTimeMillis();
        Log.logLongTime(dt1, dt2, "service:" + module + "." + service);
        return ro;
    }

    /**
     * 根据request判断用户是否登录
     * @param request
     * @return
     * @throws UnloginException
     */
    public static User checkLogin(HttpServletRequest request) throws UnloginException {
        User user = getUser(request);
        if (user != null && user.isLogined()) {
            return user;
        } else {
            StringBuffer sb = request.getRequestURL();
            throw new UnloginException("用户尚未登录，或者是登录已过期。 ", sb != null ? sb.toString() : "");
        }
    }

    /**
     * 获取request参数
     * @param request
     * @param unexpectParams
     * @return
     */
    public static JSONObject getRequestParameters(HttpServletRequest request, String[] unexpectParams) {
        JSONObject res = new JSONObject();
        Map<String, String[]> parameterMap = request.getParameterMap();
        parameterMap.entrySet().stream().forEach(entry->{
            if(StringUtil.exists(entry.getKey(), unexpectParams, false, false)){
                String[] values=entry.getValue();
                if (values != null && values.length == 1) {
                    res.put(entry.getKey(), values[0]);
                } else {
                    res.put(entry.getKey(), values);
                }
            }
        });
        return res;
    }

    public static Map getRequestParameterMap(HttpServletRequest request, ThreadObject to) {
        HashMap map = new HashMap();
        StringBuffer buf = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while((line = reader.readLine()) != null) {
                buf.append(line);
            }
            JSONObject json =JSON.parseObject(buf.toString());
            json.entrySet().stream().forEach(entry->{
                if (entry.getValue() instanceof String[]) {
                    map.put(entry.getKey(), entry.getValue());
                } else if (!(entry instanceof JSONArray)) {
                    map.put(entry.getKey(), entry.getValue().toString());
                } else {
                    int cnt = ((JSONArray)entry.getValue()).size();
                    String[] vals = new String[cnt];
                    for(int i = 0; i < cnt; ++i) {
                        vals[i] = ((JSONArray)entry.getValue()).getString(i);
                    }
                    map.put(entry.getKey(), vals);
                }
            });
        }catch (Exception e){
            Log.exception(e);
        }
        return map;
    }

}
