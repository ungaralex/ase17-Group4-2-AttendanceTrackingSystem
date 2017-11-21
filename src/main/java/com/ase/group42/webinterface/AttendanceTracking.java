package com.ase.group42.webinterface;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class AttendanceTracking {
    @Id public String trackingSystem;
}
