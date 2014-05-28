/* (c) 2014 AMIS.  All rights reserved. */
package adapter;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.Servlet;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osgi.service.http.HttpService;

import bean.WebApplication;

import com.bea.wlevs.ede.api.Adapter;
import com.bea.wlevs.ede.api.StreamSender;
import com.bea.wlevs.ede.api.StreamSource;
import com.sun.jersey.spi.container.servlet.ServletContainer;

import event.RegisterOccupancyEvent;
import event.UnregisterOccupancyEvent;

@Path("/occupancy")
public class OccupancyAdapter implements Adapter, StreamSource {

	private static StreamSender eventSender;
	private String rootContext;

	public void setRootContext(String rootContext) {
		this.rootContext = rootContext;
	}

	public OccupancyAdapter() {
		super();
	}

	public void setHttpService(HttpService httpService) throws Exception {
		System.out.println("Setting up httpservice at " + rootContext);
		Hashtable<String, String> initParams = new Hashtable<String, String>();
		initParams.put("javax.ws.rs.Application", WebApplication.class.getName());
		Servlet jerseyServlet = new ServletContainer();

		httpService.registerServlet(rootContext, jerseyServlet, initParams, null);
	}

	@POST
	@Path("/register")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.TEXT_PLAIN })
	public void registerJSONAlert(@FormParam("data") String data)
			throws IOException {
		if (data != null && data.length() > 0) {
			System.out.println("received data: " + data);

			RegisterOccupancyEvent e = parseRegisterJSON(data);

			if (e != null) {
				if (eventSender != null) {
					eventSender.sendInsertEvent(e);
				} else {
					System.out.println("eventsender is null");
				}
			}
		}

	}

	@POST
	@Path("/unregister")
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED, MediaType.TEXT_PLAIN })
	public void unregsiterJSONAlert(@FormParam("data") String data)
			throws IOException {
		if (data != null && data.length() > 0) {
			System.out.println("received data: " + data);

			UnregisterOccupancyEvent e = parseUnregisterJSON(data);

			if (e != null) {
				if (eventSender != null) {
					eventSender.sendInsertEvent(e);
				} else {
					System.out.println("eventsender is null");
				}
			}
		}

	}

	public RegisterOccupancyEvent parseRegisterJSON(String js) {
		// {"cardId":"a3b8df2c988b","deviceId":"14.311.313","roomId":226,"deskNum":2}

		// System.out.println(js);
		JSONParser parser = new JSONParser();
		JSONObject jo;
		try {
			jo = (JSONObject) parser.parse(js);
			RegisterOccupancyEvent event = new RegisterOccupancyEvent(
					jo.get("cardId"), jo.get("deviceId"), jo.get("roomId"),
					jo.get("deskNum"));
			// System.out.println(event.toString());
			return event;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}

	}

	public UnregisterOccupancyEvent parseUnregisterJSON(String js) {
		// {"cardId":"a3b8df2c988b","deviceId":"14.311.313"}

		// System.out.println(js);
		JSONParser parser = new JSONParser();
		JSONObject jo;
		try {
			jo = (JSONObject) parser.parse(js);
			UnregisterOccupancyEvent event = new UnregisterOccupancyEvent(
					jo.get("cardId"), jo.get("deviceId"));
			// System.out.println(event.toString());
			return event;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void setEventSender(StreamSender sender) {
		eventSender = sender;
	}
}
