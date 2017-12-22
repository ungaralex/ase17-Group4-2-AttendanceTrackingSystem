package com.ase.group42.webinterface;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import org.json.simple.parser.ParseException;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.ase.group42.webinterface.TimeUtils.checkDate;
import static com.ase.group42.webinterface.TimeUtils.getWeekId;

public class PostMinimalAttendanceResource extends ServerResource {
    @Post
    public Representation postAttendance(DomRepresentation attendanceDom) {
        Document doc = null;
        try {
            doc = attendanceDom.getDocument();
            NodeList attendanceInfo = doc.getElementsByTagName("attendance").item(0).getChildNodes();

            Map<String, String> infoMap = new HashMap<>();
            for (int i=0; i<attendanceInfo.getLength(); i++) {
                Node node = attendanceInfo.item(i);
                infoMap.put(node.getNodeName(), node.getTextContent());
            }

            Key<AttendanceTracking> theTracking = Key.create(AttendanceTracking.class, "defaultTracking");
            Key<Student> studentKey = Key.create(theTracking, Student.class, Long.parseLong(infoMap.get("studentId")));

            Student student = ObjectifyService.ofy().load().key(studentKey).now();

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            doc.setXmlVersion("1.0");
            Element wasSaved = doc.createElement("saved");
            String succes = "error";

            int weekId = getWeekId();
            if (student != null &&
                    student.tokens[weekId] != null &&
                    student.tokens[weekId].equals(infoMap.get("token")) &&
                    checkDate(student)) {
                    Attendance attendance = new Attendance(student, new Date().getTime(), infoMap.get("token"), Boolean.parseBoolean(infoMap.get("presented")));
                    ObjectifyService.ofy().save().entity(attendance).now();

                    student.tokens[weekId] = null;
                    ObjectifyService.ofy().save().entity(student).now();

                    succes = "success";
            }

            wasSaved.appendChild(doc.createTextNode(succes));
            doc.appendChild(wasSaved);
        } catch (IOException | ParserConfigurationException | ParseException e) {
            e.printStackTrace();
        }

        return new DomRepresentation(MediaType.TEXT_XML, doc);
    }
}
