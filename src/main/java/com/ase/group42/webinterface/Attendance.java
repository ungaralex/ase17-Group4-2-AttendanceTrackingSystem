package com.ase.group42.webinterface;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;

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

	public Long studentId;
	public Long tutorialGroupId;
	@Index public Long dateId;
	public boolean presented;
	
	public Attendance() {}
	
	public Attendance(Long studentId, Long tutorialGroupId, Long dateId, boolean presented) {
		this();
		this.studentId = studentId;
		this.tutorialGroupId = tutorialGroupId;
		this.dateId = dateId;
		this.presented = presented;
		
		this.trackingSystem = Key.create(AttendanceTracking.class, "defaultTracking");
	}
	
	/**
	 * Separate constructor to register students that showed up
	 * @param s the Student
	 * @param week the current week
	 */
	public Attendance(Student s, Long week) {
		this(s.id, 0L + s.group, week, false);
	}

	public Attendance(Student s, Long week, boolean presented) {
		this(s.id, 0L + s.group, week, presented);
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

	@Override
	public String toString() {
		return new Date(dateId).toString();
	}
}
