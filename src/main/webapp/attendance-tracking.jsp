<%-- //[START all]--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%-- //[START imports]--%>
<%@ page import="com.googlecode.objectify.Key" %>
<%@ page import="com.googlecode.objectify.ObjectifyService" %>
<%-- //[END imports]--%>

<%@ page import="java.util.List" %>
<%@ page import="com.ase.group42.webinterface.Student" %>
<%@ page import="com.ase.group42.webinterface.AttendanceTracking" %>
<%@ page import="org.json.simple.JSONArray" %>
<%@ page import="org.json.simple.parser.JSONParser" %>
<%@ page import="java.io.FileReader" %>
<%@ page import="org.json.simple.parser.ParseException" %>
<%@ page import="org.json.simple.JSONObject" %>
<%@ page import="java.io.File" %>
<%@ page import="com.ase.group42.webinterface.Attendance" %>
<%@ page import="java.util.Random" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <link type="text/css" rel="stylesheet" href="/stylesheets/main.css"/>
</head>

<body>

<%
    String guestbookName = request.getParameter("guestbookName");
    if (guestbookName == null) {
        guestbookName = "defaultTracking";
    }
    pageContext.setAttribute("guestbookName", guestbookName);
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
        pageContext.setAttribute("user", user);

        Key<AttendanceTracking> trackingKey = Key.create(AttendanceTracking.class, "defaultTracking");
        List<Student> students = ObjectifyService.ofy()
                .load()
                .type(Student.class)
                .ancestor(trackingKey)
                .order("email")
                .list();
        Student toSearch = new Student(user.getEmail(), -1);

%>
<p>Hello, ${fn:escapeXml(user.nickname)}! (You can
    <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>

<%

    int sIdx = students.indexOf(toSearch);
    if (sIdx == -1) {
%>
<form action="/sign" method="post">
    <select name="groupselect">
        <option value="1">Group 1</option>
        <option value="2">Group 2</option>
        <option value="3">Group 3</option>
        <option value="4">Group 4</option>
        <option value="5">Group 5</option>
        <option value="6">Group 6</option>
    </select>
    <div><input type="submit" value="Save group"/></div>
    <input type="hidden" name="guestbookName" value="${fn:escapeXml(guestbookName)}"/>
</form>
<%
    } else {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            File gFile = new File(classLoader.getResource("groups.json").getFile());
            JSONArray groups = (JSONArray) new JSONParser().parse(new FileReader(gFile));
            toSearch = students.get(sIdx);
            JSONObject group = (JSONObject) groups.get(toSearch.group-1);

            pageContext.setAttribute("group", (String) group.get("group"));
            pageContext.setAttribute("instructor", (String) group.get("instructor"));
            pageContext.setAttribute("day", (String) group.get("day"));
            pageContext.setAttribute("room", (String) group.get("room"));
            pageContext.setAttribute("time", (String) group.get("time"));

            /* START Testing */
            Attendance att = new Attendance(toSearch, 1L, true);
            ObjectifyService.ofy().save().entity(att).now();
            /* END Testin */
%>

<p><b>Group: </b>${fn:escapeXml(group)}</p>
<p><b>Instructor: </b>${fn:escapeXml(instructor)}</p>
<p><b>Day: </b>${fn:escapeXml(day)}</p>
<p><b>Room: </b>${fn:escapeXml(room)}</p>
<p><b>Time: </b>${fn:escapeXml(time)}</p>

<br><p><b>Last 10 attendances: </b></p>
<%
            List<Attendance> attendances = ObjectifyService.ofy()
                    .load()
                    .type(Attendance.class)
                    .ancestor(trackingKey)
                    .order("-dateId")
                    .list();
            int ctr = 0;
            for (Attendance atd : attendances) {
                if (atd.studentId.equals(toSearch.id)) {
                    pageContext.setAttribute("atd", atd.toString());
%>
<p>Attendance on: ${fn:escapeXml(atd)}</p>
<%
                    ctr++;
                }
                if (ctr == 10)
                    break;
            }
            if (ctr == 0) {
%>
<p><b>No attendances listed!</b></p>
<%
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
} else {
%>
<p>Hello!
    <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
    to subscribe to a group or view your group selection.</p>
<%
    }
%>

</body>
</html>
<%-- //[END all]--%>
