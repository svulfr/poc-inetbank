package ru.ulfr.poc;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;

/**
 * Replacement for Security Filter registration
 * This class is activated by Servlet Engine on startup and registers Spring Security Filter Chain
 */
public class SecurityInitializer extends AbstractSecurityWebApplicationInitializer implements WebApplicationInitializer {
    @Override
    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
        super.beforeSpringSecurityFilterChain(servletContext);
    }
}
