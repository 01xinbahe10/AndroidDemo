package com.example.aac.test.surfaceview.view.utils;

import java.util.Arrays;

/**
 * Created by hxb on  2020/11/12
 */
public final class ArrayUtil<T> {

    /**
     * 数组合并
     * */
    public static <T>Object[] arrayMerge(T[] firstArray, T[] twoArray) {
        int firstLength = firstArray.length;
        int twoLength = twoArray.length;
        T[] newArray = Arrays.copyOf(firstArray, (firstLength + twoLength));
        System.arraycopy(twoArray, 0, newArray, firstLength, twoLength);
        return newArray;
    }
}
