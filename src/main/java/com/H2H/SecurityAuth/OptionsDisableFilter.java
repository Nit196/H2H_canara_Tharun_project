package com.H2H.SecurityAuth;

import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OptionsDisableFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
    throws IOException, ServletException 
    {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        
        if (request.getMethod().equals("OPTIONS")) {
        	System.out.println("option request get.............");
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } else {
           
            chain.doFilter(req, res);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }

    @Override
    public void destroy() {
        
    }
}
