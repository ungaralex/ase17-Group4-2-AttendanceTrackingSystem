package com.ase.group42.webinterface;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

/**
 * This class represents a student's attendance to groups. 
 * Each instance of this class represents one student's attendance to one week of one of his /her groups.
 * @author frederic
 *
 */
@Entity
public class Attendance {
	@Parent Key<AttendanceTracking> trackingSystem;
	@Id public Long attendanceId;
	
	@Index public Student student;
	int weekId;
	boolean presented;
	
	public Attendance() {}
	
	public Attendance(Student s, int week, boolean presented) {
		this();
		this.student = s;
		this.weekId = week;
		this.presented = presented;
		
		this.trackingSystem = Key.create(AttendanceTracking.class, "defaultTracking");
	}
	
	/**
	 * Separate constructor to register students that showed up
	 * @param s the Student
	 * @param week the current week
	 */
	public Attendance(Student s, int week) {
		this(s,week,true);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Attendance)) {
			return false;
		}
		else {
			Attendance a = (Attendance) o;
			return a.attendanceId == this.attendanceId;
		}
	}
}
