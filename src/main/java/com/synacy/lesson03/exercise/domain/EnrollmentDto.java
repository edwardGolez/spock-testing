package com.synacy.lesson03.exercise.domain;

import java.util.Set;

public class EnrollmentDto implements PrinterFormattable, EmailAttachable {


	private Student student;
	private Set<CourseClass> enrolledClasses;

	public Student getStudent() {
		return student;
	}

	public StudentProfile getStudentProfile() {
		return student.profile;
	}

	public Set<CourseClass> getEnrolledClasses() {
		return enrolledClasses;
	}
}
