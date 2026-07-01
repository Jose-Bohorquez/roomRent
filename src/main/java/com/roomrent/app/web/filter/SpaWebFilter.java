package com.roomrent.app.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class SpaWebFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Root → redirect to React portal landing
        if (path.equals("/") || path.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/portal/");
            return;
        }

        // React portal SPA: forward all clean portal paths to portal/index.html
        if (path.startsWith("/portal") && !path.contains(".")) {
            request.getRequestDispatcher("/portal/index.html").forward(request, response);
            return;
        }

        // Angular SPA: forward all other clean paths (non-API, non-file) to Angular index
        if (
            !path.startsWith("/api") &&
            !path.startsWith("/management") &&
            !path.startsWith("/v3/api-docs") &&
            !path.startsWith("/portal") &&
            !path.contains(".") &&
            path.matches("/(.*)")
        ) {
            request.getRequestDispatcher("/index.html").forward(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
