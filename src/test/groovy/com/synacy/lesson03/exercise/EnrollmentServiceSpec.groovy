package com.synacy.lesson03.exercise

import spock.lang.Specification

class EnrollmentServiceSpec extends Specification {

    EnrollmentService service

    StudentProfileService studentProfileService
    CourseClassService courseClassService
    EnrollmentNotificationService enrollmentNotificationService
    SystemService systemService
    StudyLoadFormatter studyLoadFormatter

    void setup() {
        service = new EnrollmentService()

        studentProfileService = Mock()
        courseClassService = Mock()
        enrollmentNotificationService = Mock()
        systemService = Mock()
        studyLoadFormatter = Mock()

        service.studentProfileService = studentProfileService
        service.courseClassService = courseClassService
        service.enrollmentNotificationService = enrollmentNotificationService
        service.systemService = systemService
        service.studyLoadFormatter = studyLoadFormatter
    }

    def "processEnrollment should update student profile"() {

    }

    def "processEnrollment should enroll student to class"() {

    }

    def "processEnrollment should notify student via email"() {

    }

    def "processEnrollment should print study load of student"() {

    }
}
