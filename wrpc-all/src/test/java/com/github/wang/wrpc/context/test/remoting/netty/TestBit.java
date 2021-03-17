package com.github.wang.wrpc.context.test.remoting.netty;

import com.github.wang.wrpc.common.utils.BitUtil;


public class TestBit {
    protected static final byte FLAG_REQUEST = (byte) 0x80;
    protected static final byte FLAG_TWOWAY = (byte) 0x40;
    protected static final byte FLAG_EVENT = (byte) 0x20;

    public static void main(String[] args) {

        byte type = 6;

        System.out.println( "FLAG_REQUEST:" + Integer.toBinaryString(FLAG_REQUEST));
        System.out.println( "FLAG_TWOWAY:" + Integer.toBinaryString(FLAG_TWOWAY));
        System.out.println( "FLAG_EVENT:" + Integer.toBinaryString(FLAG_EVENT));
//        System.out.println(Integer.toBinaryString(1));
        byte a = (byte) (FLAG_REQUEST | type);
        System.out.println(Integer.toBinaryString(a));
        a |= FLAG_TWOWAY;
        System.out.println(Integer.toBinaryString(a));
        a |= FLAG_EVENT;
        System.out.println(Integer.toBinaryString(a));

//        System.out.println(BitUtil.getBit(a,7));
//
//        System.out.println(BitUtil.getBit(a,6));
//
//        System.out.println(BitUtil.getBits(a,0,5));


    }


}
