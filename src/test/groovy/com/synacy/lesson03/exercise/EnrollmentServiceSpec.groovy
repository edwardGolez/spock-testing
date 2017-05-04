package com.synacy.lesson03.exercise

import com.synacy.lesson03.exercise.domain.*

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

    def "processEnrollment should update the student profile"() {
        given:
        EnrollmentDto enrollmentDetails = Mock()
        StudentProfile studentProfile = Mock()

        enrollmentDetails.getStudentProfile() >> studentProfile

        def enrolledClasses = []
        enrollmentDetails.getEnrolledClasses() >> enrolledClasses

        when:
        service.processEnrollment(enrollmentDetails)

        then:
        1 * studentProfileService.updateStudentProfile(studentProfile)
    }

    def "processEnrollment should enroll a student to multiple course classes"() {
        given:
        EnrollmentDto enrollmentDetails = Mock()
        Student student = enrollmentDetails.getStudent()

        CourseClass courseClass1 = Mock()
        CourseClass courseClass2 = Mock()
        def enrolledClasses = [
                courseClass1, courseClass2
        ]
        enrollmentDetails.getEnrolledClasses() >> enrolledClasses

        when:
        service.processEnrollment(enrollmentDetails)

        then:
        1 * courseClassService.enrollStudentToClass(student, courseClass1)
        1 * courseClassService.enrollStudentToClass(student, courseClass2)
    }

    def "processEnrollment should notify student via email with the details of its enrollment"() {
        given:
        EnrollmentDto enrollmentDetails = Mock()

        def enrolledClasses = []
        enrollmentDetails.getEnrolledClasses() >> enrolledClasses

        when:
        service.processEnrollment(enrollmentDetails)

        then:
        1 * enrollmentNotificationService.emailStudent(StudentEmailType.ENROLLMENT, enrollmentDetails)
    }

    def "processEnrollment should print study load of student with the details of its enrollment"() {
        given:
        EnrollmentDto enrollmentDetails = Mock()
        StudyLoad studyLoad = Mock()

        def enrolledClasses = []
        enrollmentDetails.getEnrolledClasses() >> enrolledClasses

        studyLoadFormatter.format(enrollmentDetails) >> studyLoad

        when:
        service.processEnrollment(enrollmentDetails)

        then:
        1 * systemService.print(studyLoad)
    }
}
