package com.ase.group42.webinterface;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StudentInfoResource extends ServerResource {

    private String mail;

    @Override
    public void doInit() {
        this.mail = getAttribute("mail");
    }

    @Get
    public Representation getStudentInfo() {
        Key<AttendanceTracking> theTracking = Key.create(AttendanceTracking.class, "defaultTracking");

        List<Student> studentList = ObjectifyService.ofy()
                .load()
                .type(Student.class)
                .ancestor(theTracking)
                .order("email")
                .list();
        int studentIndex = Collections.binarySearch(studentList, new Student(mail, -1), new Comparator<Student>() {
            @Override
            public int compare(Student o1, Student o2) {
                return o1.email.compareTo(o2.email);
            }
        });
        Student student = studentIndex != -1 ? studentList.get(studentIndex) : null;

        Document doc = null;
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            doc.setXmlVersion("1.0");
            Element studentId = doc.createElement("studentId");
            studentId.appendChild(doc.createTextNode(student != null ? student.id + "" : "NOT_FOUND"));
            doc.appendChild(studentId);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return new DomRepresentation(MediaType.TEXT_XML, doc);
    }
}
