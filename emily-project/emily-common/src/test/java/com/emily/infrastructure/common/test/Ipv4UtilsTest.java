package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.Ipv4Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author :  Emily
 * @since :  2025/8/2 上午10:13
 */
class Ipv4UtilsTest {
    @Test
    void ipToLong() {
        Assertions.assertEquals(3232235777L, Ipv4Utils.ipToLong("192.168.1.1"));
        Assertions.assertEquals(4294967295L, Ipv4Utils.ipToLong("255.255.255.255"));
        Assertions.assertEquals(0L, Ipv4Utils.ipToLong("0.0.0.0"));
        Assertions.assertFalse(Ipv4Utils.validate("256.1.1.1"));
        Assertions.assertFalse(Ipv4Utils.validate("192.168.01.1"));

        Assertions.assertEquals(Ipv4Utils.longToIp(3232235777L), "192.168.1.1");
        Assertions.assertEquals(Ipv4Utils.longToIp(4294967295L), "255.255.255.255");
        Assertions.assertEquals(Ipv4Utils.longToIp(0L), "0.0.0.0");
    }
}
