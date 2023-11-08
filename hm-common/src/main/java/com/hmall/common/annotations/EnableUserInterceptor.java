package com.hmall.common.annotations;


import com.hmall.common.config.MvcConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 按需导入 哪个服务需要此配置 就可以去哪个服务加上@EnableUserInterceptor  --否则就去spring.factories配置自动配置的包
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Import(MvcConfig.class)
public @interface EnableUserInterceptor {
}
