package com.synacy.lesson03.exercise;

import com.synacy.lesson03.exercise.domain.CourseClass;
import com.synacy.lesson03.exercise.domain.EnrollmentDto;
import com.synacy.lesson03.exercise.domain.Printable;
import com.synacy.lesson03.exercise.domain.StudentEmailType;

public class EnrollmentService {

	private StudentProfileService studentProfileService;
	private CourseClassService courseClassService;
	private EnrollmentNotificationService enrollmentNotificationService;
	private StudyLoadFormatter studyLoadFormatter;
	private SystemService systemService;

	public void processEnrollment(EnrollmentDto enrollmentDetails) {

		studentProfileService.updateStudentProfile(enrollmentDetails.getStudentProfile());

		for (CourseClass courseClass : enrollmentDetails.getEnrolledClasses()) {
			courseClassService.enrollStudentToClass(enrollmentDetails.getStudent(), courseClass);
		}

		enrollmentNotificationService.emailStudent(StudentEmailType.ENROLLMENT, enrollmentDetails);

		Printable studyLoadPrintable = studyLoadFormatter.format(enrollmentDetails);
		systemService.print(studyLoadPrintable);
	}

	public StudentProfileService getStudentProfileService() {
		return studentProfileService;
	}

	public void setStudentProfileService(StudentProfileService studentProfileService) {
		this.studentProfileService = studentProfileService;
	}

	public CourseClassService getCourseClassService() {
		return courseClassService;
	}

	public void setCourseClassService(CourseClassService courseClassService) {
		this.courseClassService = courseClassService;
	}

	public EnrollmentNotificationService getEnrollmentNotificationService() {
		return enrollmentNotificationService;
	}

	public void setEnrollmentNotificationService(EnrollmentNotificationService enrollmentNotificationService) {
		this.enrollmentNotificationService = enrollmentNotificationService;
	}

	public StudyLoadFormatter getStudyLoadFormatter() {
		return studyLoadFormatter;
	}

	public void setStudyLoadFormatter(StudyLoadFormatter studyLoadFormatter) {
		this.studyLoadFormatter = studyLoadFormatter;
	}

	public SystemService getSystemService() {
		return systemService;
	}

	public void setSystemService(SystemService systemService) {
		this.systemService = systemService;
	}
}
