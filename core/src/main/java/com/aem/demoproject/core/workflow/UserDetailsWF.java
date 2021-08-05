package com.aem.demoproject.core.workflow;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Iterator;

@Component(service = WorkflowProcess.class, immediate = true, property = {"process.label=User Details - DemoProject"})
public class UserDetailsWF implements WorkflowProcess {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(WorkItem workItem, WorkflowSession wfSession, MetaDataMap metaDataMap)
            throws WorkflowException {

        Session session = wfSession.adaptTo(Session.class);
        LOG.info("sesssion obj={}", session);
        ResourceResolver rescResolver = wfSession.adaptTo(ResourceResolver.class);
        UserManager userManagerResc = rescResolver.adaptTo(UserManager.class);
        LOG.info("UserManager Obj from Resource Resolver={}", userManagerResc);
        String userOrGroupId = "admin";
        try {
            Authorizable authorizable = userManagerResc.getAuthorizable(userOrGroupId);
            if (authorizable.isGroup()) {
                LOG.info("Id is a group");
                Group userGroup = (Group) authorizable;
                Iterator<Authorizable> declaredMembers = userGroup.getDeclaredMembers();
                while (declaredMembers.hasNext()) {
                    declaredMembers.next();
                }
            }
            else{
                LOG.info("It is a user !!");
            }
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
    }
}