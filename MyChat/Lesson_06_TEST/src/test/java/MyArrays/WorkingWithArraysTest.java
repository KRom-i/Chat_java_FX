package MyArrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

public class WorkingWithArraysTest {

    @ParameterizedTest
    @MethodSource("newArrayAfterLastValuesInteger")
    void shouldNewArrayAfterLastValuesInteger(int[] arrayExpected, int[] arrayIntegers, int valuesInteger){
        Assertions.assertArrayEquals(
                arrayExpected,
                WorkingWithArrays.newArrayAfterLastFour(arrayIntegers, valuesInteger));
    }

    private static Stream<Arguments> newArrayAfterLastValuesInteger(){

        return  Stream.of(
                Arguments.arguments(new int[]{5,6},new int[]{1,2,3,4,5,6}, 4),
                Arguments.arguments(new int[]{2,2}, new int[]{1,2,3,4,5,6,4,2,2}, 4),
                Arguments.arguments(new int[]{3,2,1}, new int[]{1,2,3,4,5,6,4,2,2,4,3,2,1}, 4)
        );

    }


    @ParameterizedTest
    @MethodSource("newArrayNullAfterLastValuesInteger")
    void shouldThrowRuntimeExceptionValuesIntegerNoFound(int[] arrayIntegers, int valuesInteger){
        Assertions.assertThrows(
                RuntimeException.class,
                () -> WorkingWithArrays.newArrayAfterLastFour(arrayIntegers, valuesInteger));
    }

    private static Stream<Arguments> newArrayNullAfterLastValuesInteger(){

        return  Stream.of(
                Arguments.arguments(new int[]{1,2,3,5,6}, 4),
                Arguments.arguments(new int[]{4}, 4)
        );

    }


    @ParameterizedTest
    @MethodSource("availabilityIntegersInArrayTrue")
    void shouldBooleanTrueAvailabilityIntegersInArray(int intA, int intB, int[] arrayIntegers){
        Assertions.assertTrue(WorkingWithArrays.availabilityIntegersInArray(intA, intB, arrayIntegers));

    }

    private static Stream<Arguments> availabilityIntegersInArrayTrue(){

        return  Stream.of(
                Arguments.arguments(1,4, new int[]{1,2,3,4}),
                Arguments.arguments(5,8, new int[]{5,6,7,8})
        );

    }

    @ParameterizedTest
    @MethodSource("availabilityIntegersInArrayFalse")
    void shouldBooleanFalseAvailabilityIntegersInArray(int intA, int intB, int[] arrayIntegers){
        Assertions.assertFalse(WorkingWithArrays.availabilityIntegersInArray(intA, intB, arrayIntegers));

    }

    private static Stream<Arguments> availabilityIntegersInArrayFalse(){

        return  Stream.of(
                Arguments.arguments(1,4, new int[]{2,2,2,2}),
                Arguments.arguments(2,3, new int[]{4,5,6,7})
        );

    }

}
