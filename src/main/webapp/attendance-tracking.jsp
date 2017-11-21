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
        Student toSearch = new Student(user.getEmail(), "", "", -1);
%>
<p>Hello, ${fn:escapeXml(user.nickname)}! (You can
    <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">sign out</a>.)</p>

<%
    if (!students.contains(toSearch)) {
%>
<form action="/sign" method="post">
    <select name="groupselect">
        <option value="1">Group 1</option>
        <option value="2">Group 2</option>
        <option value="3">Group 3</option>
        <option value="4">Group 4</option>
    </select>
    <div><input type="submit" value="Save group"/></div>
    <input type="hidden" name="guestbookName" value="${fn:escapeXml(guestbookName)}"/>
</form>
<%
    }
} else {
%>
<p>Hello!
    <a href="<%= userService.createLoginURL(request.getRequestURI()) %>">Sign in</a>
    to include your name with greetings you post.</p>
<%
    }
%>

</body>
</html>
<%-- //[END all]--%>
