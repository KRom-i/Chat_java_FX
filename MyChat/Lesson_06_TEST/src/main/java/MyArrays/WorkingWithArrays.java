package MyArrays;

import java.util.Arrays;
import java.util.function.Supplier;

public class WorkingWithArrays {

    public WorkingWithArrays(){
    }

    public static int[] newArrayAfterLastFour(int[] arrayIntegers, int valuesInteger){

        int index = -1;

        for (int i = 0; i < arrayIntegers.length - 1; i++) {
            if (arrayIntegers[i] == valuesInteger) index = i + 1;
        }

        if (index > -1 && arrayIntegers != null && arrayIntegers.length > 1){
            return Arrays.copyOfRange (arrayIntegers, index, arrayIntegers.length);
        } else {
            throw new RuntimeException();
        }

    }

    public static boolean availabilityIntegersInArray(int intA, int intB, int[] arrayIntegers){
        return Arrays.binarySearch(arrayIntegers, intA) > -1 && Arrays.binarySearch(arrayIntegers, intB) > -1;
    }


}
