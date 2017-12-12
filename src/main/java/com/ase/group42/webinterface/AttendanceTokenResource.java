package com.ase.group42.webinterface;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import org.json.simple.JSONArray;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.ReadableRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AttendanceTokenResource extends ServerResource {
    private long studentId;
    private int weekNumber;

    @Override
    public void doInit() {
        try {
            this.studentId = Long.parseLong(getAttribute("studentId"));
            this.weekNumber = Integer.parseInt(getAttribute("weekNumber"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Get
    public Representation represent() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        Document doc = null;

        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            doc.setXmlVersion("1.0");

            Key<AttendanceTracking> theTracking = Key.create(AttendanceTracking.class, "defaultTracking");
            Key<Student> studentKey = Key.create(theTracking, Student.class, studentId);

            Student student = ObjectifyService.ofy().load().key(studentKey).now();

            Element token = doc.createElement("token");
            token.appendChild(doc.createTextNode(student.tokens[weekNumber]));
            doc.appendChild(token);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return new DomRepresentation(MediaType.TEXT_XML, doc);
    }
}
