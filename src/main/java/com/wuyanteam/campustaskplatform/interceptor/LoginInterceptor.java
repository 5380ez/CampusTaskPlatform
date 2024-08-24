package com.wuyanteam.campustaskplatform.interceptor;

import com.wuyanteam.campustaskplatform.utils.JWTUtils;
import com.wuyanteam.campustaskplatform.utils.ThreadLocalUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component  // 拦截器对象注入
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 令牌验证                         请求头名字
        String token = request.getHeader("Authorization");
        // 验证token
        try {
            Map<String, Object> claims = JWTUtils.getClaimsByToken(token);
            ThreadLocalUtils.set(claims);            // 把业务数据存储到ThreadLocal
            return true;            // 解析成功应该是放行
        } catch (Exception e) {
            // http响应状态码401
            response.setStatus(401);
            // 不放行
            return false;
        }
    }
}