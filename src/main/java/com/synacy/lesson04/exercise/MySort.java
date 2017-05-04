package com.synacy.lesson04.exercise;

import java.util.Arrays;

public class MySort {

	// NOTE: input arrays are already sorted
	public static int[] sort(int[] array1, int[] array2) {
		int[] mergedArray = mergeArrays(array1, array2);
		Arrays.sort(mergedArray);

		return mergedArray;
	}

	private static int[] mergeArrays(int[] array1, int[] array2) {
		int[] result = new int[ array1.length + array2.length ];

		int index = 0;
		for(; index < array1.length; index++)
			result[index] = array1[index];
		for(; index < array1.length + array2.length; index++)
			result[index] = array2[index - array1.length];
		return result;
	}

}
