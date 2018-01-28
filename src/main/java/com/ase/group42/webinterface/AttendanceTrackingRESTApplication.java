package com.ase.group42.webinterface;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.routing.Router;
import org.restlet.routing.Template;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;

/**
 * RESTlet Application to provide root Restlet
 * @author frederic, michael, alexander
 *
 */
public class AttendanceTrackingRESTApplication extends Application {
	
	/**
	 * This method creates a root Restlet that will handle all incoming calls
	 * @return Said Restlet
	 */
	public Restlet createInboundRoot() {
		Router baseRouter = new Router(getContext());
		
		// Section for Student Services, authentication through google account
		Router studRouter = new Router(getContext());
		studRouter.attach("/attendance", AttendanceTrackingResource.class);
		studRouter.attach("/attendance/{attendanceId}", AttendanceResource.class);
		studRouter.attach("/tokens/{studentId}", AttendanceTokenResource.class);
		studRouter.attach("/info/{mail}", StudentInfoResource.class);
		
		// Guard the restlet with BASIC authentication.
		ChallengeAuthenticator studGuard = new ChallengeAuthenticator(null, ChallengeScheme.HTTP_BASIC, "testRealm");
		// Instantiates a Verifier of identifier/secret couples based on a simple Map.
		MapVerifier mapVerifier = new MapVerifier();
		// Load a single static login/secret pair.
		mapVerifier.getLocalSecrets().put("ase-student", "ase2017".toCharArray());
		studGuard.setVerifier(mapVerifier);
		studGuard.setNext(studRouter);

	    
	    // Section for Tutor Services, hard coded authentication
		Router tutRouter = new Router(getContext());
		tutRouter.attach("/postAttendance", PostMinimalAttendanceResource.class);
		tutRouter.attach("/attendance", AttendanceTrackingResource.class);
		
		// Guard the restlet with BASIC authentication.
		ChallengeAuthenticator tutGuard = new ChallengeAuthenticator(null, ChallengeScheme.HTTP_BASIC, "testRealm");
		// Instantiates a Verifier of identifier/secret couples based on a simple Map.
		MapVerifier mapVerifier2 = new MapVerifier();
		// Load a single static login/secret pair.
		mapVerifier2.getLocalSecrets().put("ase-tutor", "ase2017".toCharArray());
		tutGuard.setVerifier(mapVerifier2);
		tutGuard.setNext(tutRouter);
		
		
		baseRouter.attach("/stud", studGuard).setMatchingMode(Template.MODE_STARTS_WITH);
		baseRouter.attach("/tut", tutGuard).setMatchingMode(Template.MODE_STARTS_WITH);
		
	    return baseRouter;
	}
	
}
