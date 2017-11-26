package com.ase.group42.webinterface;


import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ServerResource;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

public class AttendanceResource extends ServerResource {
	
	@SuppressWarnings("unchecked")
	public Representation represent() {
		
		JSONArray list = new JSONArray();
		
		
		Key<AttendanceTracking> theTracking = Key.create(AttendanceTracking.class, "defaultTracking");
		//Create ancestor key
		List<Attendance> attendanceList = ObjectifyService.ofy().load().type(Attendance.class).ancestor(theTracking).list();
		
		for (Attendance attendance : attendanceList) {
			JSONObject currentObj = new JSONObject();
			long attendanceId = attendance.attendanceId;
			long studentId = attendance.student.id;
			int groupId = attendance.student.group;
			int weekId = attendance.weekId;
			boolean presented = attendance.presented;
			
			currentObj.put("attendance_id", attendanceId);
			currentObj.put("student_id", studentId);
			currentObj.put("tutorial_group_id", groupId);
			currentObj.put("week_id", weekId);
			currentObj.put("presented", presented);
			
			list.add(currentObj);
			
			
		}
		
		return new JsonRepresentation(list);
	}
	
}
