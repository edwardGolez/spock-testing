package com.synacy.lesson03.exercise

import com.synacy.lesson03.exercise.domain.CourseClass
import com.synacy.lesson03.exercise.domain.EnrollmentDto
import com.synacy.lesson03.exercise.domain.Printable
import com.synacy.lesson03.exercise.domain.Student
import com.synacy.lesson03.exercise.domain.StudentEmailType
import com.synacy.lesson03.exercise.domain.StudentProfile
import com.synacy.lesson03.exercise.domain.StudyLoad

/**
 * Created by kenichigouang on 5/4/17.
 */
class EnrollmentServiceSpec extends spock.lang.Specification {

    EnrollmentService service

    StudentProfileService studentProfileService
    CourseClassService courseClassService
    EnrollmentNotificationService enrollmentNotificationService
    StudyLoadFormatter studyLoadFormatter
    SystemService systemService

    void setup() {
        service = new EnrollmentService()

        studentProfileService = Mock()
        courseClassService = Mock()
        enrollmentNotificationService = Mock()
        studyLoadFormatter = Mock()
        systemService = Mock()

        service.studentProfileService = studentProfileService
        service.courseClassService = courseClassService
        service.enrollmentNotificationService = enrollmentNotificationService
        service.studyLoadFormatter = studyLoadFormatter
        service.systemService = systemService
    }

    def "ProcessEnrollment should update student profile on processing on enrollment"() {
        given:
        EnrollmentDto enrollmentDetails = Mock()
        StudentProfile studentProfile = Mock()

        enrollmentDetails.getStudentProfile() >> studentProfile
        enrollmentDetails.getEnrolledClasses() >> []

        when:
        service.processEnrollment(enrollmentDetails)

        then:
        1 * studentProfileService.updateStudentProfile(studentProfile)
    }

    def "ProcessEnrollment should enroll student to a class/classes on processing of enrollment"() {
        given:
        EnrollmentDto enrollmentDetails = Mock()
        Student student = Mock()

        CourseClass courseClass1 = Mock()
        CourseClass courseClass2 = Mock()

        def enrolledClasses = [
                courseClass1, courseClass2
        ]

        enrollmentDetails.getStudent() >> student
        enrollmentDetails.getEnrolledClasses() >> enrolledClasses

        when:
        service.processEnrollment(enrollmentDetails)

        then:
        1 * courseClassService.enrollStudentToClass(student, courseClass1)
        1 * courseClassService.enrollStudentToClass(student, courseClass2)
    }

    def "ProcessEnrollment should notify student via email on processing of enrollment"() {
        given:
        EnrollmentDto enrollmentDetails = Mock()

        enrollmentDetails.getEnrolledClasses() >> []

        when:
        service.processEnrollment(enrollmentDetails)

        then:
        1 * enrollmentNotificationService.emailStudent(StudentEmailType.ENROLLMENT, enrollmentDetails)

    }

    def "ProcessEnrollment should have the system print study load in study load format"() {
        given:
        EnrollmentDto enrollmentDetails = Mock()
        StudyLoad studyLoad= Mock()

        enrollmentDetails.getEnrolledClasses() >> []
        studyLoadFormatter.format(enrollmentDetails) >> studyLoad
        Printable studyLoadPrintable = studyLoad

        when:
        service.processEnrollment(enrollmentDetails)

        then:
        1 * systemService.print(studyLoadPrintable)

    }
}