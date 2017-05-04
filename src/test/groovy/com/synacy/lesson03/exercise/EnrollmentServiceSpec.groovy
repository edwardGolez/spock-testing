package com.synacy.lesson03.exercise

import com.synacy.lesson03.exercise.domain.CourseClass
import com.synacy.lesson03.exercise.domain.EnrollmentDto
import com.synacy.lesson03.exercise.domain.Printable
import com.synacy.lesson03.exercise.domain.Student
import com.synacy.lesson03.exercise.domain.StudentEmailType
import com.synacy.lesson03.exercise.domain.StudentProfile
import com.synacy.lesson03.exercise.domain.StudyLoad
import spock.lang.Specification

/**
 * Created by banjoe on 5/4/17.
 */
class EnrollmentServiceSpec extends Specification {

    private EnrollmentService service

    private CourseClassService courseClassService
    private EnrollmentNotificationService enrollmentNotificationService
    private StudentProfileService studentProfileService
    private StudyLoadFormatter studyLoadFormatter
    private SystemService systemService

    void setup() {
        service = new EnrollmentService()

        courseClassService = Mock(CourseClassService)
        enrollmentNotificationService = Mock(EnrollmentNotificationService)
        studentProfileService = Mock(StudentProfileService)
        studyLoadFormatter = Mock(StudyLoadFormatter)
        systemService = Mock(SystemService)

        service.enrollmentNotificationService = enrollmentNotificationService
        service.studentProfileService = studentProfileService
        service.studyLoadFormatter = studyLoadFormatter
        service.systemService = systemService
        service.courseClassService = courseClassService
    }

    def "ProcessEnrollment should update student's profile"() {
        given:
        EnrollmentDto enrollmentDetails = Mock(EnrollmentDto)

        StudentProfile studentProfile = Mock(StudentProfile)
        enrollmentDetails.getStudentProfile() >> studentProfile

        enrollmentDetails.getEnrolledClasses() >> []

        when: service.processEnrollment(enrollmentDetails)

        then:
        1 * studentProfileService.updateStudentProfile(studentProfile)
    }

    def "ProcessEnrollment should enroll student to a class"() {
        given:
        EnrollmentDto enrollmentDetails = Mock(EnrollmentDto)

        StudentProfile studentProfile = Mock(StudentProfile)
        enrollmentDetails.getStudentProfile() >> studentProfile

        Student student = Mock(Student)
        enrollmentDetails.getStudent() >> student

        CourseClass courseClass1 = Mock(CourseClass)
        CourseClass courseClass2 = Mock(CourseClass)

        Set enrolledClasses = [
                courseClass1, courseClass2
        ]

        enrollmentDetails.getEnrolledClasses() >> enrolledClasses

        when: service.processEnrollment(enrollmentDetails)

        then:
        1 * courseClassService.enrollStudentToClass(student, courseClass1)
        1 * courseClassService.enrollStudentToClass(student, courseClass2)
    }

    def "ProcessEnrollment should notify and email the enrollment details to student"() {
        given:
        EnrollmentDto enrollmentDetails = Mock(EnrollmentDto)

        enrollmentDetails.getEnrolledClasses() >> []

        when:
        service.processEnrollment(enrollmentDetails)

        then:
        1 * enrollmentNotificationService.emailStudent(StudentEmailType.ENROLLMENT, enrollmentDetails)
    }

    def "ProcessEnrollment should format enrollment details into studyload format"() {
        given:
        EnrollmentDto enrollmentDetails = Mock(EnrollmentDto)

        enrollmentDetails.getEnrolledClasses() >> []

        when: service.processEnrollment(enrollmentDetails)

        then:
        1 * studyLoadFormatter.format(enrollmentDetails)
    }

    def "ProcessEnrollment should print studyload"() {
        given:
        EnrollmentDto enrollmentDetails = Mock(EnrollmentDto)

        enrollmentDetails.getEnrolledClasses() >> []

        StudyLoad studyLoad = Mock(StudyLoad)
        studyLoadFormatter.format(enrollmentDetails) >> studyLoad

        when: service.processEnrollment(enrollmentDetails)

        then:
        1 * systemService.print(studyLoad)
    }

}
