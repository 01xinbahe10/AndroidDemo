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
    private static <T> T jsonFileToObject(Context context, String fileName, TypeToken<T> typeToken) {
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
}
