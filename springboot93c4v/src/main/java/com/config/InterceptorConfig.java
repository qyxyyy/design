package com.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.interceptor.AuthorizationInterceptor;

@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

	@Value("${server.servlet.context-path:}")
	private String contextPath;

	@Bean
	public AuthorizationInterceptor getAuthorizationInterceptor() {
		return new AuthorizationInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		String prefix = (contextPath == null || contextPath.isEmpty()) ? "" : contextPath;
		registry.addInterceptor(getAuthorizationInterceptor())
				.addPathPatterns("/**")
				.excludePathPatterns(
						"/",
						"/login",
						"/static/**",
						"/front/**",
						"**/front/**",
						prefix + "/front/**",       // 明确排除带 context-path 的前台路径
						prefix + "/static/**",
						"/zuke/register", "/huzhu/register",
						"**/zuke/register", "**/huzhu/register",
						prefix + "/zuke/register", prefix + "/huzhu/register"
				);
		super.addInterceptors(registry);
	}
	
	/**
	 * springboot 2.0配置WebMvcConfigurationSupport之后，会导致默认配置被覆盖，要访问静态资源需要重写addResourceHandlers方法
	 */
	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/**")
        .addResourceLocations("classpath:/resources/")
        .addResourceLocations("classpath:/static/")
        .addResourceLocations("classpath:/admin/")
        .addResourceLocations("classpath:/front/")
        .addResourceLocations("classpath:/public/");
		super.addResourceHandlers(registry);
    }
}
