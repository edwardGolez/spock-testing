package com.synacy.lesson03.exercise;

import com.synacy.lesson03.exercise.domain.CourseClass;
import com.synacy.lesson03.exercise.domain.Student;

public interface CourseClassService {
	void enrollStudentToClass(Student student, CourseClass courseClass);
}
