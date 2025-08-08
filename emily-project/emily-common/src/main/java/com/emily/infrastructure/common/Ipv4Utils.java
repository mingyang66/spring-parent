package com.emily.infrastructure.common;

import java.util.regex.Pattern;

/**
 * <a href="https://ipnum.bmcx.com/">IP地址转数字</a>
 *
 * @author :  Emily
 * @since :  2025/8/2 下午7:41
 */
public class Ipv4Utils {

    /**
     * IP地址正则验证（IPv4格式）
     * 添加负向前瞻(?!.*\\b0\\d)来排除前导零情况（如"01"、"001"等）
     * private static final Pattern IP_PATTERN1 = Pattern.compile("^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$");
     */
    private static final Pattern IP_PATTERN = Pattern.compile("^(?!.*\\b0\\d)\\b((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)$");

    public static long ipToLong(String ipAddress) {
        if (!validate(ipAddress)) {
            throw new IllegalArgumentException("Invalid IPv4 address: " + ipAddress);
        }
        String[] segments = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            int segment = Integer.parseInt(segments[i]);
            result |= (long) segment << (24 - (8 * i));
        }
        return result & 0xFFFFFFFFL; // 确保无符号
    }

    public static String longToIp(long ipLong) {
        if (ipLong < 0 || ipLong > 4294967295L) {
            throw new IllegalArgumentException("Invalid IP long value: " + ipLong);
        }
        return ((ipLong >> 24) & 0xFF) + "." +
                ((ipLong >> 16) & 0xFF) + "." +
                ((ipLong >> 8) & 0xFF) + "." +
                (ipLong & 0xFF);
    }

    public static boolean validate(String ipAddress) {
        return ipAddress != null && IP_PATTERN.matcher(ipAddress).matches();
    }
}
