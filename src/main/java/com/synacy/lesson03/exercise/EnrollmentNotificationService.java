package com.synacy.lesson03.exercise;

import com.synacy.lesson03.exercise.domain.EmailAttachable;
import com.synacy.lesson03.exercise.domain.StudentEmailType;

public interface EnrollmentNotificationService {
	void emailStudent(StudentEmailType enrollment, EmailAttachable... attachments);
}
