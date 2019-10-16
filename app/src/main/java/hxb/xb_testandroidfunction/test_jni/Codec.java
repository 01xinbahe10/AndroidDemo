package hxb.xb_testandroidfunction.test_jni;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by zhangbo on 2017/7/19.
 * 字符串简易编解码
 */

public final class Codec {
    private Codec() {
    }
    /**
     * MD5算法
     *
     * @param data
     * @param offset
     * @param count
     * @return 转换后的byte[]
     */
    public static byte[] MD5Bytes(byte[] data, int offset, int count) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data, offset, count);
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytes2string(byte[] data) {
        char[] buff = new char[data.length << 1];
        for (int i = 0; i < data.length; i++) {
            int v = (data[i] & 0xF0) >> 4;
            if (v < 10)
                buff[i << 1] = (char) ('0' + v);
            else
                buff[i << 1] = (char) ('a' + (v - 10));
            v = data[i] & 0x0F;
            if (v < 10)
                buff[(i << 1) + 1] = (char) (char) ('0' + v);
            else
                buff[(i << 1) + 1] = (char) ('a' + (v - 10));
        }
        return new String(buff);
    }

    /**
     * MD5算法
     *
     * @param data
     * @param offset
     * @param count
     * @return 转换后的字符串
     */
    public static final String MD5(byte[] data, int offset, int count) {
        data = MD5Bytes(data, offset, count);
        if (null == data)
            return null;
        //转换为字符串
        return bytes2string(data);
    }

    /**
     * 字符串的MD5算法
     *
     * @param value
     * @return 转换后的字符串
     */
    public static String MD5(String value) {
        byte[] data;
        try {
            data = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return MD5(data, 0, data.length);
    }

    /**
     * 编码
     *
     * @param value
     * @return 编码后的字符串
     */
    public static String en(String value) {
        if (null == value)
            return null;
        if (value.length() == 0)
            return value;
        byte[] data;
        try {
            data = value.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        int len = data.length;
        int outLen = ((len + 2) / 3) << 2;
        int index = (int) (Math.random() * len);
        int cc = data[index] & 0x3F;
        char[] buff = new char[outLen + 1];
        buff[0] = v2c(cc, 0);
        int i = 0;
        for (int j = 0; j < len; j++) {
            int v = data[j] & 0x3F;
            buff[++i] = v2c(v ^ cc, i);

            v = (data[j] & 0xC0) >> 6;
            if (++j < len)
                v |= (data[j] & 0x0F) << 2;
            buff[++i] = v2c(v ^ cc, i);
            if (j >= len)
                break;

            v = (data[j] & 0xF0) >> 4;
            if (++j < len)
                v |= (data[j] & 0x03) << 4;
            buff[++i] = v2c(v ^ cc, i);
            if (j >= len)
                break;

            v = (data[j] & 0xFC) >> 2;
            buff[++i] = v2c(v ^ cc, i);
        }
        i++;
        return String.valueOf(buff, 0, i);
    }

    /**
     * 解码
     *
     * @param value
     * @return 解码后的字符串
     */
    public static String de(String value) {
        if (null == value)
            return null;
        int len = value.length();
        if (len == 0)
            return value;
        int outLen = ((len - 1 + 3) >> 2) * 3;
        byte[] buff = new byte[outLen];

        int cc = c2v(value.charAt(0), 0);
        int i = 0;
        for (int j = 1; j < len; j++) {
            int vv;
            int v = c2v(value.charAt(j), j) ^ cc;
            vv = v;
            if (++j >= len)
                break;

            v = c2v(value.charAt(j), j) ^ cc;
            buff[i++] = (byte) (vv | ((v & 0x03) << 6));
            vv = (v & 0x3C) >> 2;
            if (++j >= len)
                break;

            v = c2v(value.charAt(j), j) ^ cc;
            buff[i++] = (byte) (vv | ((v & 0x0F) << 4));
            vv = (v & 0x30) >> 4;
            if (++j >= len)
                break;

            v = c2v(value.charAt(j), j) ^ cc;
            buff[i++] = (byte) (vv | (v << 2));
        }
        try {
            return new String(buff, 0, i, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static char v2c(int v, int i) {
        if ((i & 0x01) == 0) {
            if (v == 26)
                return (char) 0x2B;
            if (v == 27)
                return (char) 0x2A;
            if (v < 26)
                return (char) (0x61 + v);
            if (v < 38)
                return (char) (0x30 - 28 + v);
            return (char) (0x5A + 38 - v);
        } else {
            if (v == 26)
                return (char) 0x2A;
            if (v == 27)
                return (char) 0x2B;
            if (v < 26)
                return (char) (0x7A - v);
            if (v < 38)
                return (char) (0x39 + 28 - v);
            return (char) (0x41 - 38 + v);
        }
    }

    private static int c2v(char c, int i) {
        int v = c & 0xFF;
        if ((i & 0x01) == 0) {
            if (v == 0x2B)
                return 26;
            if (v == 0x2A)
                return 27;
            if (v >= 0x61)
                return v - 0x61;
            if (v < 0x3A)
                return 28 + v - 0x30;
            return 38 + 0x5A - v;
        } else {
            if (v == 0x2A)
                return 26;
            if (v == 0x2B)
                return 27;
            if (v >= 0x61)
                return 0x7A - v;
            if (v < 0x3A)
                return 0x39 + 28 - v;
            return 38 + v - 0x41;
        }
    }

    /**
     * 压缩
     *
     * @param value
     * @return
     */
    public static byte[] compress(byte[] value) {
        if (null == value || value.length == 0)
            return value;
        Deflater compresser = new Deflater();
        compresser.setInput(value);
        compresser.finish();
        int pos = 0;
        byte[] out = new byte[value.length + 10 + ((value.length + 4) >> 2)];
        while (!compresser.finished()) {
            int count = out.length - pos;
            if (count == 0) {
                count = 32;
                out = Arrays.copyOf(out, out.length + count);
            }
            count = compresser.deflate(out, pos, count);
            pos += count;
        }
        compresser.end();
        if (pos <= 0)
            return null;
        if (pos != out.length)
            out = Arrays.copyOf(out, pos);
        return out;
    }

    /**
     * 解压缩
     *
     * @param value
     * @return
     */
    public static byte[] decompress(byte[] value) {
        if (null == value || value.length == 0)
            return value;
        Inflater decompresser = new Inflater();
        decompresser.setInput(value);

        int pos = 0;
        byte[] out = new byte[value.length];
        try {
            while (!decompresser.finished()) {
                int count = out.length - pos;
                if (count == 0) {
                    count = 32;
                    out = Arrays.copyOf(out, out.length + count);
                }
                count = decompresser.inflate(out, pos, count);
                pos += count;
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
            return null;
        } finally {
            decompresser.end();
        }

        if (pos <= 0)
            return null;
        if (pos != out.length)
            out = Arrays.copyOf(out, pos);
        return out;
    }

    /**
     * 压缩字符串
     *
     * @param value
     * @return
     */
    public static byte[] compressRaw(String value) {
        if (value.length() == 0)
            return new byte[]{0};
        byte[] bytes;
        try {
            bytes = value.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        return compress(bytes);
    }

    /**
     * 解压缩字符串
     *
     * @param value
     * @return
     */
    public static String decompressRaw(byte[] value) {
        if (value.length == 1 && value[0] == 0)
            return "";
        Inflater decompresser = new Inflater();
        decompresser.setInput(value);

        int pos = 0;
        byte[] out = new byte[value.length];
        try {
            while (!decompresser.finished()) {
                int count = out.length - pos;
                if (count == 0) {
                    count = 32;
                    out = Arrays.copyOf(out, out.length + count);
                }
                count = decompresser.inflate(out, pos, count);
                pos += count;
            }
        } catch (DataFormatException e) {
            e.printStackTrace();
            return null;
        } finally {
            decompresser.end();
        }
        if (pos > 0) {
            try {
                return new String(out, 0, pos, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 压缩字符串
     *
     * @param value
     * @return
     */
    public static String compress(String value) {
        if (null == value || value.length() == 0)
            return value;
        byte[] bytes;
        try {
            bytes = value.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        Deflater compresser = new Deflater();
        compresser.setInput(bytes);
        compresser.finish();
        int pos = 0;
        byte[] out = new byte[bytes.length + 10 + ((bytes.length + 4) >> 2)];
        while (!compresser.finished()) {
            int count = out.length - pos;
            if (count == 0) {
                count = 32;
                out = Arrays.copyOf(out, out.length + count);
            }
            count = compresser.deflate(out, pos, count);
            pos += count;
        }
        compresser.end();
        if (pos <= 0)
            return null;
        return Base64.encodeToString(out, 0, pos, Base64.NO_WRAP);
    }

    /**
     * 解压缩字符串
     *
     * @param value
     * @return
     */
    public static String decompress(String value) {
        if (value.length() == 0)
            return value;
        byte[] in = Base64.decode(value, Base64.NO_WRAP);
        return decompressRaw(in);
    }
}
