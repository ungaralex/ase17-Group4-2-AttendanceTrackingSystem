package com.ase.group42.webinterface;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import java.util.Arrays;

@Entity
public class Student {
    @Parent Key<AttendanceTracking> trackingSystem;
    @Id public Long id;

    @Index public String email;
    public int group;
    public String[] tokens;

    public Student() {
    }

    public Student(String email, int group) {
        this();

        this.email = email;
        this.group = group;
        // Again, this could be easier with Java 8...
        this.tokens = new String[13];
        for (int i=0; i<tokens.length; i++)
            tokens[i] = new RandomString().nextString();

        trackingSystem = Key.create(AttendanceTracking.class, "defaultTracking");
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() != Student.class)
            return false;
        Student st = (Student) o;
        return st.email.equals(this.email);
    }
}
