package com.synacy.lesson04.exercise

import spock.lang.Specification
import spock.lang.Unroll

class MySortTest extends Specification {

	@Unroll("#param1 + #param2 = #expectedResult")
	def "sort should output sorted numbers from both given sorted arrays"() {
		given:
		int[] array1 = param1.toArray()
		int[] array2 = param2.toArray()

		expect: "when + then"
		expectedResult.toArray() == MySort.sort(array1, array2)

		where:
		param1      | param2    | expectedResult
		[1, 6, 8]   | [2, 4, 5] | [1, 2, 4, 5, 6, 8]
		[1, 6, 8]   | [2, 4]    | [1, 2, 4, 6, 8]
		[1, 6, 7, 8]| [2, 4, 5] | [1, 2, 4, 5, 6, 7, 8]
		[]          | []        | []
	}


}
