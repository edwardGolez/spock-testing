package com.synacy.lesson03.exercise

import com.synacy.lesson03.exercise.domain.CourseClass
import com.synacy.lesson03.exercise.domain.EnrollmentDto
import com.synacy.lesson03.exercise.domain.Printable
import com.synacy.lesson03.exercise.domain.PrinterFormattable
import com.synacy.lesson03.exercise.domain.Student
import com.synacy.lesson03.exercise.domain.StudentEmailType
import com.synacy.lesson03.exercise.domain.StudentProfile
import com.synacy.lesson03.exercise.domain.StudyLoad
import spock.lang.Specification

class EnrollmentServiceSpec extends Specification {

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

    def "ProcessEnrollment should update student profile service using enrollment details"() {
        given:
            EnrollmentDto enrollmentDetails = Mock()
            StudentProfile studentProfile = Mock()

            enrollmentDetails.getEnrolledClasses() >> []
            enrollmentDetails.getStudentProfile() >> studentProfile
        when:
            service.processEnrollment(enrollmentDetails)
        then:
            1 * studentProfileService.updateStudentProfile(studentProfile)
    }

    def "ProcessEnrollment should enroll student with courseClass into courseClassService using EnrollmentDTO"(){
        given:
            EnrollmentDto enrollmentDetails = Mock()
            Student student = Mock()
            CourseClass courseClass = Mock()

            Set<CourseClass> courseClassSet = new HashSet<>()
            courseClassSet.add(courseClass)

            enrollmentDetails.getStudent() >> student
            enrollmentDetails.getEnrolledClasses() >> courseClassSet
        when:
            service.processEnrollment(enrollmentDetails)
        then:
            1 * courseClassService.enrollStudentToClass(student,courseClass)
    }

    def "ProcessEnrollment should notify the student with email about enrollmentDetails status"(){
        given:
            EnrollmentDto enrollmentDetails = Mock()

            enrollmentDetails.getEnrolledClasses() >> []
        when:
            service.processEnrollment(enrollmentDetails)
        then:
            1 * enrollmentNotificationService.emailStudent(StudentEmailType.ENROLLMENT,enrollmentDetails)
    }

    def "ProcessEnrollment should print the enrollmentDetails using systemService"(){
        given:
            EnrollmentDto enrollmentDetails = Mock()
            StudyLoad studyLoad = Mock()

            enrollmentDetails.getEnrolledClasses() >> []
            studyLoadFormatter.format(enrollmentDetails) >> studyLoad
        when:
            service.processEnrollment(enrollmentDetails)
        then:
            1 * systemService.print(studyLoad)
    }
}
