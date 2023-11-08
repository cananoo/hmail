package com.hmall.gateway.filters;



import cn.hutool.core.text.AntPathMatcher;

import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoginGlobalFilter implements GlobalFilter, Ordered {
    // 保存了需要放行的路径
    private final AuthProperties authProperties;
    // 进行登录校验的工具
    private final JwtTool jwtTool;

    // 支持Ant风格的通配符匹配
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.获取request对象
        ServerHttpRequest request = exchange.getRequest();
        // 2.判断当前请求是否需要被拦截
          if (isAllowPath(request)){
              // 无需拦截，放行
              return chain.filter(exchange);
          }
        // 3.需要拦截，获取请求头中的token,解析token
        String token = null;
        List<String> headers = request.getHeaders().get("authorization");
        if (headers != null){
            token = headers.get(0);
        }

          // 解析token
        Long userID = null;
        try {
            userID = jwtTool.parseToken(token);
        } catch (Exception e) {
            ServerHttpResponse response = exchange.getResponse();
            response.setRawStatusCode(401); //401 Unauthorized
            return response.setComplete();
        }
        System.out.println("userId" + userID);
        // TODO 4.传递用户信息到下游服务
        String userInfo = userID.toString();
        // 修改请求头 -- 传递到下游微服务
        exchange.mutate().request(builder -> builder.header("user-info", userInfo)).build();

        // 5.放行
        return chain.filter(exchange);
    }

    private boolean isAllowPath(ServerHttpRequest request) {
        boolean flag = false;
        // 1.获取当前路径
        String path = request.getPath().toString();
        // 2.要放行的路径
        for (String excludePath : authProperties.getExcludePaths()) {
            boolean isMatch = pathMatcher.match(excludePath, path);
            if (isMatch) {
                flag = true;
                break;
            }
        }
        return  flag;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
