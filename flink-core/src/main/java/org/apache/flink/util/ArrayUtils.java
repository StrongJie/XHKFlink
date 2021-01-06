package org.apache.flink.util;

import org.apache.flink.annotation.Internal;

/**
 * Utility class for Java arrays.
 */
@Internal
public final class ArrayUtils {

    public static String[] concat(String[] array1, String[] array2) {
        if (array1.length == 0) {
            return array2;
        }
        if (array2.length == 0) {
            return array2;
        }
        String[] resultArray = new String[array1.length + array2.length];
        System.arraycopy(array1, 0, resultArray, 0, array1.length);
        System.arraycopy(array2, 0, resultArray, array1.length, array2.length);
        return resultArray;
    }
}
