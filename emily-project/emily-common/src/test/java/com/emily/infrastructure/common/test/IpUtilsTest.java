package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.IpUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author :  Emily
 * @since :  2025/8/2 上午10:13
 */
class IpUtilsTest {
    @Test
    void ipToLong() {
        Assertions.assertEquals(3232235777L, IpUtils.ipToLong("192.168.1.1"));
        Assertions.assertEquals(4294967295L, IpUtils.ipToLong("255.255.255.255"));
        Assertions.assertEquals(0L, IpUtils.ipToLong("0.0.0.0"));
        Assertions.assertFalse(IpUtils.validate("256.1.1.1"));
        Assertions.assertFalse(IpUtils.validate("192.168.01.1"));

        Assertions.assertEquals(IpUtils.longToIp(3232235777L), "192.168.1.1");
        Assertions.assertEquals(IpUtils.longToIp(4294967295L), "255.255.255.255");
        Assertions.assertEquals(IpUtils.longToIp(0L), "0.0.0.0");
    }
}
