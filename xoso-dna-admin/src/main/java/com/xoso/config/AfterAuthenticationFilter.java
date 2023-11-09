package com.xoso.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Configuration
public class AfterAuthenticationFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        var req = (HttpServletRequest) request;
        HttpSession session = req.getSession(true);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            //TODO: sent otp

            session.setAttribute("otpVerified", false);
        }
        chain.doFilter(request, response);
    }
}
