package com.aem.demoproject.core.servlets;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class, property = {
        "sling.servlet.resourceTypes=cq/Page",
        "sling.servlet.resourceTypes=demoproject/components/structure/page",
        "sling.servlet.resourceTypes=demoproject/customResourceType",
        "sling.servlet.methods=GET",
        "sling.servlet.methods=POST",
        "sling.servlet.selectors=sample",
        "sling.servlet.selectors=data",
        "sling.servlet.selectors=sampledata",
        "sling.servlet.extensions=xml",
        "sling.servlet.extensions=json",
        "sling.servlet.extensions=txt",
        "sling.servlet.extensions=html"
})

public class ResourceTypeServlet extends SlingAllMethodsServlet {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Reference
    private ResourceResolverFactory rescFactory;

    @Override
    protected void doGet(SlingHttpServletRequest req, SlingHttpServletResponse res) throws IOException {
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");

        try {
            res.getWriter().write("<html><head></head><body><p>Servlet Registration using R7 annotation</p></body></html>");
            res.setStatus(200);
        } catch (IOException e) {
            LOG.error("IOException={}", e.getMessage());
        }

    }

    @Override
    protected void doPost(SlingHttpServletRequest req, SlingHttpServletResponse res) throws IOException {
        res.setContentType("text/html");
        res.setCharacterEncoding("UTF-8");
        Map<String, Object> authMap = new HashMap<>();
        authMap.put(ResourceResolverFactory.SUBSERVICE, "demoproject-service");

        try {


            ResourceResolver resourceResolver = req.getResourceResolver();
            if (resourceResolver != null) {

                User user = resourceResolver.adaptTo(User.class);
                if (user != null) {
                    LOG.info("User Id from request={} and path={}", user.getID(), user.getPath());
                } else {
                    LOG.info("User obj null");
                }
                Session session = resourceResolver.adaptTo(Session.class);
                LOG.info("Session Id={}", session.getUserID());

                /* From Service User */
                ResourceResolver serviceResourceResolver = rescFactory.getServiceResourceResolver(authMap);
                if (serviceResourceResolver != null) {
                    LOG.info("Service resolver user id={}", serviceResourceResolver.getUserID());
                    LOG.info("session Service resolver user id={}", serviceResourceResolver.adaptTo(Session.class).getUserID());
                    UserManager userManager = serviceResourceResolver.adaptTo(UserManager.class);
                    if (userManager != null) {
                        Authorizable authorizable = userManager.getAuthorizable(session.getUserID());
                        Group cugGroup = (Group) userManager.getAuthorizable("demo-cug");
                        if (cugGroup != null && cugGroup.isMember(authorizable)) {
                            LOG.info("Session is part of CUG");
                        } else {
                            LOG.info("Session is not part of CUG");
                        }
                    }
                }
                /*UserManager userManager = resourceResolver.adaptTo(UserManager.class);
                if (userManager != null) {
                    Authorizable authorizable = userManager.getAuthorizable(session.getUserID());
                    Group cugGroup = (Group)userManager.getAuthorizable("demo-cug");
                    if(cugGroup != null && cugGroup.isMember(authorizable)){
                        LOG.info("Session is part of CUG");
                    }
                    else{
                        LOG.info("Session is not part of CUG");
                    }
                }
                else{
                    LOG.info("Unable to get UserManager from resolver");
                }*/


            } else {
                LOG.info("No resolver!!");
            }

            res.getWriter().write("<html><head></head><body><p>Servlet Registration using R7 annotation</p></body></html>");
            res.setStatus(200);
        } catch (IOException e) {
            LOG.error("IOException={}", e.getMessage());
        } catch (RepositoryException e) {
            LOG.error("RepositoryException={}", e.getMessage());
        } catch (LoginException e) {
            LOG.error("LoginException={}", e.getMessage());
        }

    }
}
