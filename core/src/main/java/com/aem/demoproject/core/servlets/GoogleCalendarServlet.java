package com.aem.demoproject.core.servlets;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

@Component(service = Servlet.class, property = { "sling.servlet.methods=" + HttpConstants.METHOD_GET,
		"sling.servlet.paths=" + "/bin/calendar" })
public class GoogleCalendarServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	private transient final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void doGet(final SlingHttpServletRequest req, final SlingHttpServletResponse resp)
			throws ServletException, IOException {

		String credPath = "C://Users/xyz/Desktop/GoogleCalendarCreds.json"; // Local File System Path to Service Account Creds JSON 
		String calendarId = "primary";
		ServiceAccountCredentials serviceAccountCredentials = ServiceAccountCredentials
				.fromStream(new FileInputStream(credPath));
		if (serviceAccountCredentials != null) {
			LOG.info("Creds associated with Service Account={}", serviceAccountCredentials.getClientEmail());
		}
		GoogleCredentials googleCreds = serviceAccountCredentials
				.createScoped(Collections.singletonList(CalendarScopes.CALENDAR_EVENTS));
		HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(googleCreds);

		Calendar calendarClient;
		try {
			calendarClient = new Calendar.Builder(new NetHttpTransport(), new GsonFactory(), requestInitializer)
					.build();

			/* Dummy Event Details - Start */
			Event event = new Event().setSummary("Google Calendar API Integration")
					.setLocation("800 Howard St., San Francisco, CA 94103")
					.setDescription("A chance to hear more about Google's Calendar API integration.");

			@SuppressWarnings("deprecation")
			Date eventStartDate = new Date(2021, 8, 13);

			@SuppressWarnings("deprecation")
			Date eventEndDate = new Date(2021, 8, 15);

			DateTime startDateTime = new DateTime(eventStartDate);
			EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone("America/Los_Angeles");
			event.setStart(start);

			DateTime endDateTime = new DateTime(eventEndDate);
			EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone("America/Los_Angeles");
			event.setEnd(end);
			
			/* Dummy Event Details - End */
			
			event = calendarClient.events().insert(calendarId, event).execute();
			LOG.info("Event HTML Link={}", event.getHtmlLink());
			resp.getWriter().write("Event Web Link=" + event.getHtmlLink());
		} catch (Exception e) {
			LOG.error("Exception={}", e.getMessage());
		}

	}
}
