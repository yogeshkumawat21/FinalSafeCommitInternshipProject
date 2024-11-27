package com.App.Yogesh.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final long serialVersionUID = -7858869558953243875L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.error("Unauthorized access attempt to: {}", request.getRequestURI());  // Log the URI of the unauthorized request

        response.setStatus(HttpStatus.UNAUTHORIZED.value());  // Set status to 401 Unauthorized
        response.setContentType("application/json");  // Return JSON content

        // Create a structured error response
        String errorResponse = "{ \"success\": false, \"message\": \"Unauthorized: You must be logged in to access this resource.\" }";

        // Write the response body
        response.getWriter().write(errorResponse);
        response.getWriter().flush();
    }
}
