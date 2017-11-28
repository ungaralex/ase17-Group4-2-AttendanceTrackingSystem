package com.ase.group42.webinterface;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * RESTlet Application to provide root Restlet
 * @author frederic
 *
 */
public class AttendanceTrackingRESTApplication extends Application {
	
	/**
	 * This method creates a root Restlet that will handle all incoming calls
	 * @return Said Restlet
	 */
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		
		router.attach("/attendance", AttendanceResource.class);
		
		return router;
	}
	
}
