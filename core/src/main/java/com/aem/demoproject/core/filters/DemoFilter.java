package com.aem.demoproject.core.filters;

import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.IncludeOptions;
import com.day.cq.wcm.commons.WCMUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.engine.EngineConstants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

@Component(service = Filter.class,
        property = {
                EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_COMPONENT,
                "sling.filter.pattern=" + "/content/demo/language-masters/en/.*",
                "sling.filter.resourceTypes=" + "demoproject/components/content/helloworld"
        })

public class DemoFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(DemoFilter.class);

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
                         final FilterChain filterChain) throws IOException, ServletException {

        final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
        logger.info("request for {}, with selector {} and suffix={} ", slingRequest
                .getRequestPathInfo().getResourcePath(), slingRequest
                .getRequestPathInfo().getSelectorString(), slingRequest.getRequestPathInfo().getSuffix());

        /*IncludeOptions includeOptions = IncludeOptions.getOptions(request,true);
        includeOptions.setDecorationTagName("");*/


        ComponentContext componentContext = WCMUtils.getComponentContext(request);
        componentContext.setDecorate(false);

        filterChain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }

}