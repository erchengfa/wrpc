package com.github.wang.wrpc.context.test.ext;


import com.github.wang.wrpc.common.ext.Spi;

/**
 * @author : wang
 * @date : 2019/12/31
 */
@Spi
public interface Hello {

    String getHello();
}
