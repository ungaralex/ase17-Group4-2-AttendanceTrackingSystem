package com.ase.group42.webinterface;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import org.json.simple.parser.ParseException;
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
import java.io.IOException;

import static com.ase.group42.webinterface.TimeUtils.checkDate;
import static com.ase.group42.webinterface.TimeUtils.getWeekId;

public class AttendanceTokenResource extends ServerResource {
    private long studentId;

    @Override
    public void doInit() {
        try {
            this.studentId = Long.parseLong(getAttribute("studentId"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Get
    public Representation represent() throws IOException, ParseException {
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
            token.appendChild(doc.createTextNode(checkDate(student) ? student.tokens[getWeekId()] : "WRONG_DATE"));
            doc.appendChild(token);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return new DomRepresentation(MediaType.TEXT_XML, doc);
    }
}
