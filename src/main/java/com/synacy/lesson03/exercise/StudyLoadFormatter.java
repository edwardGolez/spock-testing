package com.synacy.lesson03.exercise;

import com.synacy.lesson03.exercise.domain.PrinterFormattable;
import com.synacy.lesson03.exercise.domain.StudyLoad;

public interface StudyLoadFormatter {
	StudyLoad format(PrinterFormattable printerFormattable);
}
