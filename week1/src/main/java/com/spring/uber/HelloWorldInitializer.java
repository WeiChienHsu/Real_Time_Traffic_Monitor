package com.spring.uber;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/* Java Configuration - need to import library in pom.xml */
public class HelloWorldInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {


    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{HelloWorldConfiguration.class};
    }

    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
}
