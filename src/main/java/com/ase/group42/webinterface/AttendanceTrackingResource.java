package com.ase.group42.webinterface;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

public class AttendanceTrackingResource extends ServerResource {

    @Get
    public Representation represent() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document doc = null;

        try {
            docBuilder = docFactory.newDocumentBuilder();

            doc = docBuilder.newDocument();
            doc.setXmlVersion("1.0");
            Element attendanceTracking = doc.createElement("attendanceTracking");
            doc.appendChild(attendanceTracking);
            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode("defaultTracking"));
            attendanceTracking.appendChild(name);

            Element attendances = doc.createElement("attendances");
            attendanceTracking.appendChild(attendances);

            Key<AttendanceTracking> theTracking = Key.create(AttendanceTracking.class, "defaultTracking");

            List<Attendance> attendanceList = ObjectifyService.ofy()
                    .load()
                    .type(Attendance.class)
                    .ancestor(theTracking)
                    .order("dateId")
                    .list();

            for (Attendance attendance : attendanceList) {
                Element attendanceElement = doc.createElement("attendance");

                Element attendanceIdElement = doc.createElement("attandanceId");
                attendanceIdElement.appendChild(doc.createTextNode(attendance.attendanceId + ""));
                attendanceElement.appendChild(attendanceIdElement);

                Element studentIdElement = doc.createElement("studentId");
                studentIdElement.appendChild(doc.createTextNode(attendance.studentId + ""));
                attendanceElement.appendChild(studentIdElement);

                Element tutorialGroupIdElement = doc.createElement("tutorialGroupId");
                tutorialGroupIdElement.appendChild(doc.createTextNode(attendance.tutorialGroupId + ""));
                attendanceElement.appendChild(tutorialGroupIdElement);

                Element dateIdElement = doc.createElement("dateId");
                dateIdElement.appendChild(doc.createTextNode(attendance.dateId + ""));
                attendanceElement.appendChild(dateIdElement);

                Element presentedElement = doc.createElement("presented");
                presentedElement.appendChild(doc.createTextNode(attendance.presented + ""));
                attendanceElement.appendChild(presentedElement);

                attendances.appendChild(attendanceElement);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return new DomRepresentation(MediaType.TEXT_XML, doc);
    }
}
