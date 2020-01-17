package com.github.wang.wrpc.common.utils;

import com.github.wang.wrpc.common.exception.RPCRuntimeException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    /**
     * 换算法？ MD5  SHA-1 MurMurHash???
     *
     * @param value the value
     * @return the byte []
     */
    public static byte[] messageDigest(String value) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(value.getBytes("UTF-8"));
            return md5.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RPCRuntimeException("No such algorithm named md5", e);
        } catch (UnsupportedEncodingException e) {
            throw new RPCRuntimeException("Unsupported encoding of" + value, e);
        }
    }

    /**
     * Hash long.
     *
     * @param digest the digest
     * @param index  the number
     * @return the long
     */
    public static long hash(byte[] digest, int index) {
        long f = ((long) (digest[3 + index * 4] & 0xFF) << 24)
            | ((long) (digest[2 + index * 4] & 0xFF) << 16)
            | ((long) (digest[1 + index * 4] & 0xFF) << 8)
            | (digest[index * 4] & 0xFF);
        return f & 0xFFFFFFFFL;
    }

}