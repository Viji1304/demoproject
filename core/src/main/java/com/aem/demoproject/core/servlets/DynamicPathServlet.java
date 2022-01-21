package com.aem.demoproject.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

@Component(service = Servlet.class)
@Designate(ocd = DynamicPathServlet.Config.class)
public class DynamicPathServlet extends SlingAllMethodsServlet {

    @ObjectClassDefinition(name = "DynamicPathServlet", description = "Paths to Enable this Servlet")
    public @interface Config {

        @AttributeDefinition(name = "Resource Types", description = "Standard Sling servlet property")
        String[] sling_servlet_paths() default {"/bin/dynamicpath"};

        @AttributeDefinition(name = "Methods", description = "Standard Sling servlet property")
        String[] sling_servlet_methods() default {"GET"};

    }

    private static final long serialVersionUID = 1L;
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
            throws ServletException, IOException {

        resp.getWriter().println("Inside Dynamic Path Servlet");

    }

    @Activate
    protected void activate(Config configValues){
        LOG.info("config values={}", configValues.sling_servlet_paths());
    }

}
