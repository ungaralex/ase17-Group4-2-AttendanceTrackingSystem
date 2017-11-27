package com.ase.group42.webinterface;


import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AttendanceResource extends ServerResource {

	private long attendanceId;

	@Override
	public void doInit() {
		try {
			this.attendanceId = Long.parseLong(getAttribute("attendanceId"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	@Get
	public Representation represent() {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document doc = null;

		try {
			docBuilder = docFactory.newDocumentBuilder();

			doc = docBuilder.newDocument();
			doc.setXmlVersion("1.0");

			Key<AttendanceTracking> theTracking = Key.create(AttendanceTracking.class, "defaultTracking");
			Key<Attendance> attendanceKey = Key.create(theTracking, Attendance.class, attendanceId);

			Attendance theAttendance = ObjectifyService.ofy().load().key(attendanceKey).now();

			Element attendance = doc.createElement("attendance");

			if (theAttendance != null) {
				Element attendanceIdElement = doc.createElement("attandanceId");
				attendanceIdElement.appendChild(doc.createTextNode(theAttendance.attendanceId + ""));
				attendance.appendChild(attendanceIdElement);

                Element studentIdElement = doc.createElement("studentId");
                studentIdElement.appendChild(doc.createTextNode(theAttendance.studentId + ""));
                attendance.appendChild(studentIdElement);

                Element tutorialGroupIdElement = doc.createElement("tutorialGroupId");
                tutorialGroupIdElement.appendChild(doc.createTextNode(theAttendance.tutorialGroupId + ""));
                attendance.appendChild(tutorialGroupIdElement);

                Element dateIdElement = doc.createElement("dateId");
                dateIdElement.appendChild(doc.createTextNode(theAttendance.dateId + ""));
                attendance.appendChild(dateIdElement);

                Element presentedElement = doc.createElement("presented");
                presentedElement.appendChild(doc.createTextNode(theAttendance.presented + ""));
                attendance.appendChild(presentedElement);
			}

			doc.appendChild(attendance);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return new DomRepresentation(MediaType.TEXT_XML, doc);
	}
}
