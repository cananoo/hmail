package com.hmall.common.intercepter;

import cn.hutool.core.util.StrUtil;
import com.hmall.common.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 网关将用户保存在请求头中后发给各个服务，各个服务又将请求头中的用户保存在ThreadLocal中供业务使用；
 */
public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取请求头中的用户
        String userInfo = request.getHeader("user-info");
        // 2. 判断是否为空,不为空,存入UserContext
        if (StrUtil.isNotBlank(userInfo)){
             // 存入ThreadLocal
            UserContext.setUser(Long.valueOf(userInfo));
        }
        // 3. 放行
          return  true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清除用户
        UserContext.removeUser();
    }
}
