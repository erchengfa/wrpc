package com.github.wang.wrpc.common.utils;

/**
 * @author : wang
 * @date : 2019/12/26
 */
public class BitUtil {

    //b为传入的字节，i为第几位（范围0-7），如要获取bit0，则i=0
    public static int getBit(byte b, int i) {
        int bit = (int) ((b >> i) & 0x1);
        return bit;
    }

    //b为传入的字节，start是起始位，length是长度，如要获取bit0-bit4的值，则start为0，length为5
    public static int getBits(byte b, int start, int length) {
        int bit = (int) ((b >> start) & (0xFF >> (8 - length)));
        return bit;
    }
}
