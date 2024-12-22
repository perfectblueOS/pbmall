package com.xunqi.gulimall.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GulimallCorsConfiguration {
    /**
     * 配置跨域过滤器
     *
     * 该方法用于创建和配置CorsWebFilter，以处理跨域请求
     * 跨域资源共享（CORS）配置是现代Web服务中常见需求，特别是在单页应用（SPA）与后端服务交互时
     * 通过允许所有来源（"*"），我们让任何域都可以访问我们的服务
     * 注意：在生产环境中，建议具体指定允许的来源域，而不是使用通配符"*"
     *
     * 在非简单类型跨域请求中，浏览器会先发送一个预检请求（OPTIONS），以确定服务是否允许跨域请求。
     * 简单类型请求跨域，不满足设定的发送条件，浏览器会先发送预检请求。
     * 预检请求的响应头中包含Access-Control-Allow-Origin头，以表明服务允许跨域请求。
     * 服务端配置跨域，客户端也需要配置，以允许跨域请求。
     * @return CorsWebFilter 返回配置好的跨域过滤器实例
     */
    public CorsWebFilter corsWebFilter(){
        // 创建一个响应式包下的类，用于配置跨域请求
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 创建一个跨域配置对象
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 允许任何头信息
        corsConfiguration.addAllowedHeader("*");

        // 允许任何HTTP方法
        corsConfiguration.addAllowedMethod("*");

        // 允许任何来源
        corsConfiguration.addAllowedOrigin("*");

        // 设置是否允许携带凭据的请求
        corsConfiguration.setAllowCredentials(true);

        // 将跨域配置注册到所有路径
        source.registerCorsConfiguration("/**", corsConfiguration);

        // 返回一个新的CorsWebFilter实例，用于处理跨域请求
        return new CorsWebFilter(source);
    }
}

