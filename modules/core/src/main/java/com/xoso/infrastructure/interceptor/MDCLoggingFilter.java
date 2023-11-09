package com.xoso.infrastructure.interceptor;

import com.xoso.infrastructure.constant.ConstantCommon;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Order(1)
@Component
public class MDCLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            MDC.put(ConstantCommon.TRACK_GUID_KEY, UUID.randomUUID().toString());
            MDC.put(ConstantCommon.MESSAGE_KEY, request.getHeader("Accept-Language"));
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(ConstantCommon.TRACK_GUID_KEY);
        }
    }
}
