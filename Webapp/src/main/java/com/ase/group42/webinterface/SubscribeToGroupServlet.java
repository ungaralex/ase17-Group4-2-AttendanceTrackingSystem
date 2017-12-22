package com.ase.group42.webinterface;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.ObjectifyService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SubscribeToGroupServlet extends HttpServlet {
    // Process the http POST of the form
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Student student;

        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();  // Find out who the user is.

        //TODO add group information (JSON?)
        String group = req.getParameter("groupselect");
        student = new Student(user.getEmail(), Integer.parseInt(group));

        // Use Objectify to save the greeting and now() is used to make the call synchronously as we
        // will immediately get a new page using redirect and we want the data to be present.

        ObjectifyService.ofy().save().entity(student).now();

        resp.sendRedirect("/attendance-tracking.jsp?group=" + group);
    }


}
