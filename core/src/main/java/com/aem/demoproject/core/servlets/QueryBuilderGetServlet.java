package com.aem.demoproject.core.servlets;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component(service = Servlet.class, property = {"sling.servlet.methods=" + HttpConstants.METHOD_GET,
        "sling.servlet.paths=" + "/bin/getQueryBuilderResults"})
public class QueryBuilderGetServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 5567357151861930492L;
    private static final String HTML_EXTENSION = ".html";
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());


    private QueryBuilder queryBuilder;

    @Reference
    protected void bindQueryBuilder(QueryBuilder queryBuilder){
        this.queryBuilder = queryBuilder;
    }


    @Override
    protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp) {

        resp.setContentType("text/html");
        ResourceResolver rescResolver = req.getResourceResolver();
        Session session = rescResolver.adaptTo(Session.class);
        PageManager pageMgr = rescResolver.adaptTo(PageManager.class);

        /* Frame Query Predicates - Starts */
        Map<String, String> predicatesMap = new HashMap();
        predicatesMap.put("path", "/content/demo");
        predicatesMap.put("type", "cq:Page");

        /* If the property name is same, we can use the below - Brings in pages which has title Experience OR Equipment */
        /*predicatesMap.put("property", "jcr:content/jcr:title");
        predicatesMap.put("property.1_value", "Experience");
        predicatesMap.put("property.2_value", "Equipment");*/

        /* If the property name is same, we can use the below - This may not be applicable for title. Perhaps for tags/multivalue property where title should be equal to both Experience and Equipment*/
        /*predicatesMap.put("property", "jcr:content/jcr:title");
        predicatesMap.put("property.1_value", "Experience");
        predicatesMap.put("property.2_value", "Equipment");
        predicatesMap.put("property.and", "true");*/

        /* Predicates are "AND" by default - Below brings pages which has both the property jcr:title +(AND) hideSubItemsInNav (with specified value) in its jcr:content node */
        /*predicatesMap.put("1_property", "jcr:content/jcr:title");
        predicatesMap.put("1_property.value", "Experience");
        predicatesMap.put("2_property", "jcr:content/hideSubItemsInNav");
        predicatesMap.put("2_property.value", "true");*/

        /* Below bring in pages which has the property either jcr:title OR hideSubItemsInNav (with specified value) in its jcr:content node*/
        /*predicatesMap.put("group.1_property", "jcr:content/jcr:title");
        predicatesMap.put("group.1_property.value", "Experience");
        predicatesMap.put("group.2_property", "jcr:content/hideSubItemsInNav");
        predicatesMap.put("group.2_property.value", "true");
        predicatesMap.put("group.p.or", "true");*/

        /* Use of daterange
        * Bring in pages which is last modified in last 3 months
        * */
        LocalDate localDate =  LocalDate.now();
        localDate = localDate.minusMonths(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        String formattedDate = formatter.format(localDate);
        /*predicatesMap.put("daterange.property", "jcr:content/cq:lastModified");
        predicatesMap.put("daterange.lowerBound", formattedDate);*/


        /* Use of dateComparison - equals/unequals/greater/greaterthanequal
        *  Pages that are modified after it is created.
        * */
        /*predicatesMap.put("dateComparison.property1", "jcr:content/cq:lastModified");
        predicatesMap.put("dateComparison.property2", "jcr:content/jcr:created");
        predicatesMap.put("dateComparison.operation", "greater");*/

        /* Use of relativedaterange - Pages created in last 1 week */
        /*predicatesMap.put("relativedaterange.property", "jcr:content/jcr:created");
        predicatesMap.put("relativedaterange.lowerBound", "-1w");*/

        /* Use of notexpired - Past date /Expired kinda functionality */
        predicatesMap.put("notexpired.property", "jcr:content/cq:lastReplicated");
        predicatesMap.put("notexpired.notexpired", "false");


        predicatesMap.put("p.limit", "-1");
        /* Frame Query Predicates - Ends */

        Query query = queryBuilder.createQuery(PredicateGroup.create(predicatesMap), session);
        SearchResult queryResults = query.getResult();
        LOG.debug("Total number of results={}", queryResults.getTotalMatches());
        try {
            resp.getWriter().write("<html><body>");
            resp.getWriter().write("<h2>Total number of Search results=" + queryResults.getTotalMatches() + "</h2>");
        } catch (IOException e) {
            LOG.error("IO Exception={}", e.getMessage());
        }
        /* Getting Results - Starts */
        queryResults.getHits().forEach(hit -> {
            try {
                String pagePath = hit.getPath();
                Page page = pageMgr.getPage(pagePath);
                if (null != page) {
                    String pageTitle = page.getTitle();
                    pagePath = pagePath + HTML_EXTENSION;
                    resp.getWriter().write("<a href='" + pagePath + "'>" + pageTitle + "</a><br>");
                }

            } catch (RepositoryException e) {
                LOG.error("Repo exception={}", e.getMessage());
            } catch (IOException e) {
                LOG.error("IO exception={}", e.getMessage());
            }

        });
        /* Getting Results - Ends */
    }
}
