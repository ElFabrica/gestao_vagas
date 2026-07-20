package com.example.gestao_vagas.config;

import com.example.gestao_vagas.modules.logs.filters.RequestLogFilter;
import com.example.gestao_vagas.modules.logs.services.AccessLogService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestLogFilter> requestLogFilter(AccessLogService accessLogService) {
        var registration = new FilterRegistrationBean<RequestLogFilter>();
        registration.setFilter(new RequestLogFilter(accessLogService));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");

        return registration;
    }
}
