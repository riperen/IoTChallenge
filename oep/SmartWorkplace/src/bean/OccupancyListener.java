/* (c) 2014 AMIS.  All rights reserved. */
package bean;

import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.Servlet;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.http.HttpService;

import com.bea.wlevs.ede.api.StreamSender;
import com.bea.wlevs.ede.api.StreamSink;
import com.bea.wlevs.ede.api.StreamSource;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import event.RegisterOccupancyEvent;

@Path("/occupancies")
public class OccupancyListener implements StreamSink, StreamSource {
	private String rootContext;
	private StreamSender eventSender;
	static LimitedQueue<RegisterOccupancyEvent> occupancies = 
			new LimitedQueue<RegisterOccupancyEvent>(100);

	public void setRootContext(String rootContext) {
		this.rootContext = rootContext;
	}

	public void onInsertEvent(Object event) {

		if (event instanceof RegisterOccupancyEvent) {
			RegisterOccupancyEvent registerEvent = (RegisterOccupancyEvent) event;
			triggerBO(registerEvent);
			
			if (!occupancies.contains(registerEvent))
				occupancies.add(registerEvent);

		}
	}

	@Override
	public void setEventSender(StreamSender sender) {
		this.eventSender = sender;

	}

	public void setHttpService(HttpService httpService) throws Exception {
		System.out.println("Setting up httpservice at " + rootContext);
		Hashtable<String, String> initParams = new Hashtable<String, String>();
		initParams.put("javax.ws.rs.Application",
				WebApplication.class.getName());
		Servlet jerseyServlet = new ServletContainer();

		httpService.registerServlet(rootContext, jerseyServlet, initParams,
				null);

	}

	@GET
	@Path("/list")
	@Produces(MediaType.APPLICATION_JSON)
	public String getOccupancies() {
		String returnS = "({\"occupancies\":[";
		for (Iterator<RegisterOccupancyEvent> i = occupancies.iterator(); i
				.hasNext();) {
			RegisterOccupancyEvent e = i.next();

			returnS += e.toJSON();
			if (i.hasNext())
				returnS += ", ";

		}

		returnS += "]})";
		return returnS;
	}

	private void triggerBO(RegisterOccupancyEvent e) {
		System.out.println("----- PUSHING EVENT FOR BAM JMS / DB -----");
		System.out.println(e);
		if (eventSender != null) {
			eventSender.sendInsertEvent(e);
		} else {
			System.out.println("eventSender is null");
		}

	}
}
