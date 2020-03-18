package hxb.xb_testandroidfunction.test_util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by hxb on  2020/2/25
 * 数据工具(处理文件，对象)
 */
public class DataUtil {

    /**
     * 克隆对象(前提是需要 类型 需要序列化)
     * */
    public static <T> T cloneObject(T obj) {
        T result = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectOutputStream outputStream = null;
        ObjectInputStream inputStream = null;
        try {
            //对象写到内存中
            byteArrayOutputStream = new ByteArrayOutputStream();
            outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(obj);

            //从内存中再读出来
            byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            inputStream = new ObjectInputStream(byteArrayInputStream);
            result = (T) inputStream.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null)
                    outputStream.close();
                if (inputStream != null)
                    inputStream.close();
                if (byteArrayOutputStream != null)
                    byteArrayOutputStream.close();
                if (byteArrayInputStream != null)
                    byteArrayInputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * json文件转对象
     *
     * @param typeToken 使用方法： TypeToken<T> typeToken = new TypeToken<T>(){};
     * */
    public static <T> T jsonFileToObject(Context context, String fileName, TypeToken<T> typeToken) {
        if (TextUtils.isEmpty(fileName)){
            return null;
        }

        try {
            //taipingyang.json文件名称
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open(fileName);
            //格式转换
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            String s = null;
            if (scanner.hasNext()) {
                s = scanner.next();
            }
            is.close();
            Gson gson = new Gson();
            return gson.fromJson(s, typeToken.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


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


    /*****
     * 字符串匹配（2020-03-18未验证）
     * Java代码实现KMP模式匹配
     *
     * @param stringS 主串S
     * @param stringT 模式串T
     */
    public static boolean match(String stringS, String stringT) {
        char[] charsS = stringS.toCharArray();
        char[] charsT = stringT.toCharArray();
        int[] next = getNextPlus(stringT);
        int j = 0;
        for (int i = 0, sizeI = charsS.length; i < sizeI; i ++) {
            // 此处0 > j其实就是针对next[j]=-1的情况，跳过主串当前字符的对比
            if (0 > j || charsT[j] == charsS[i]) {
                j ++;
            } else {
                j = next[j];
                i --; // 实现继续对比主串的同一个位置的字符
            }
            if (charsT.length == j) {
                return true;
            }
        }
        return false;
    }

    /**
     * Java代码实现KMP模式匹配算法next[]推导过程
     * 针对next[]推荐对比字符相同问题优化
     *
     * @param stringT 模式串
     * */
    private static int[] getNextPlus(String stringT) {
        char[] chars = stringT.toCharArray();
        int[] next = new int[chars.length];
        next[0] = -1; // 这是一个必然的结果，不管是对什么模式串，以此为突破点往后推导
        for (int i = 1; i < chars.length; i ++) {
            int j = next[i - 1];
            while (true) {
                if (-1 == j || chars[i - 1] == chars[j]) {
                    next[i] = j + 1;
                    break;
                } else {
                    j = next[j];
                }
            }
        }

        // 优化版增添代码
        for (int i = 0; i < next.length; i ++) {
            if (0 <= next[i] && chars[next[i]] == chars[i]) {
                next[i] = next[next[i]];
            }
        }
        return next;
    }
}
