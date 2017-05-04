package com.synacy.lesson03.exercise

import com.synacy.lesson03.exercise.domain.CourseClass
import com.synacy.lesson03.exercise.domain.EnrollmentDto
import com.synacy.lesson03.exercise.domain.Student
import com.synacy.lesson03.exercise.domain.StudentEmailType
import com.synacy.lesson03.exercise.domain.StudentProfile
import com.synacy.lesson03.exercise.domain.StudyLoad
import spock.lang.Specification

/**
 * Created by michael on 5/4/17.
 */
class EnrollmentServiceTest extends Specification {

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

        service.setStudentProfileService(studentProfileService)
        service.setCourseClassService(courseClassService)
        service.setEnrollmentNotificationService(enrollmentNotificationService)
        service.setStudyLoadFormatter(studyLoadFormatter)
        service.setSystemService(systemService)
    }

    def "processEnrollment should update student profile"() {
        given:
            EnrollmentDto enrollmentDetails = Mock()

            StudentProfile studentProfile = Mock()

            CourseClass class1 = Mock()
            CourseClass class2 = Mock()

            def enrolledClasses = [
                class1, class2
            ]

            enrollmentDetails.getStudentProfile() >> studentProfile
            enrollmentDetails.getEnrolledClasses() >> enrolledClasses

        when:
            service.processEnrollment(enrollmentDetails)

        then:
            1 * studentProfileService.updateStudentProfile(enrollmentDetails.getStudentProfile())

    }

    def "processEnrollment should enroll student's course classes"() {
        given:
            EnrollmentDto enrollmentDetails = Mock()
            Student student = Mock()

            CourseClass class1 = Mock()
            CourseClass class2 = Mock()

            def enrolledClasses = [
                class1, class2
            ]

            enrollmentDetails.getStudent() >> student
            enrollmentDetails.getEnrolledClasses() >> enrolledClasses

        when:
            service.processEnrollment(enrollmentDetails)

        then:
            1 * courseClassService.enrollStudentToClass(student, class1);
            1 * courseClassService.enrollStudentToClass(student, class2);
    }

    def "processEnrollment should email the student"() {
        given:
            EnrollmentDto enrollmentDetails = Mock()
            Student student = Mock()

            CourseClass class1 = Mock()
            CourseClass class2 = Mock()

            def enrolledClasses = [
                class1, class2
            ]

            enrollmentDetails.getEnrolledClasses() >> enrolledClasses
        when:
            service.processEnrollment(enrollmentDetails)

        then:
            1 * enrollmentNotificationService.emailStudent(StudentEmailType.ENROLLMENT, enrollmentDetails)
    }

    def "processEnrollment should print the students load"() {
        given:
            EnrollmentDto enrollmentDetails = Mock()
            StudyLoad studyLoad = Mock()

            CourseClass class1 = Mock()
            CourseClass class2 = Mock()

            def enrolledClasses = [
                class1, class2
            ]

            enrollmentDetails.getEnrolledClasses() >> enrolledClasses
            studyLoadFormatter.format(enrollmentDetails) >> studyLoad

        when:
            service.processEnrollment(enrollmentDetails)

        then:
            1 * systemService.print(studyLoad)
    }
}
