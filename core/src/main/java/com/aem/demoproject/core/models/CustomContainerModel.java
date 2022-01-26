package com.aem.demoproject.core.models;

import com.adobe.cq.export.json.ComponentExporter;
import com.adobe.cq.export.json.ExporterConstants;
import com.adobe.cq.wcm.core.components.models.LayoutContainer;
import lombok.experimental.Delegate;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.via.ResourceSuperType;

@Model(adaptables = {SlingHttpServletRequest.class, Resource.class}, adapters = {LayoutContainer.class, ComponentExporter.class},
        resourceType = CustomContainerModel.RESOURCE_TYPE,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME, selector = "customcontainer", extensions = ExporterConstants.SLING_MODEL_EXTENSION)
public class CustomContainerModel implements LayoutContainer {

    protected static final String RESOURCE_TYPE = "demoproject/components/content/container";

    /* Uses lombok's Delegate annotation
     * to delegate all public methods of LayoutContainer
     * along with this super type and
     * excludes to mention the methods to override */
    @Delegate(types = LayoutContainer.class, excludes = ContainerExcludes.class)
    @Self
    @Via(type = ResourceSuperType.class)
    private LayoutContainer layoutContainer;

    @Override
    public String getId() {
        return "CustomId";
    }

    @Override
    public String getExportedType() {
        return CustomContainerModel.RESOURCE_TYPE;
    }

    public String getTestString() {
        return "Additional Test String in JSON response";
    }

    private interface ContainerExcludes {
        String getId();
        String getExportedType();
    }


}
