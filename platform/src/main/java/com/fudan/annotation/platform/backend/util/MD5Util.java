package com.fudan.annotation.platform.backend.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    // md5算法，默认是32位加密,加密字符串  
    public static String md5(String source) {
        byte[] arr = getEncrypByteArr(source, "MD5");
        return byteArrayToHex(arr).toLowerCase();
    }

    // 获取digest byte
    private static byte[] getEncrypByteArr(String source, String algorithm) {
        MessageDigest mDigest = null;
        try {
            mDigest = MessageDigest.getInstance(algorithm);
            return mDigest.digest(source.getBytes());
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block  
            e.printStackTrace();
        }
        return null;
    }

    // 下面这个函数用于将字节数组换成成16进制的字符串  
    private static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符  
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））  
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去  
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回  
        return new String(resultCharArray);
    }
}
