package com.example.demo.interceptor;

import com.example.demo.configuration.MetricsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MyInterceptor implements HandlerInterceptor {
    @Autowired
    MetricsClient metricsClient;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        metricsClient.incrementCounter(request.getRequestURI() + "-" + request.getMethod());
        return true;
    }
}
