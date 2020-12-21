package com.notebook.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A simple IP library
 *
 * @author evan
 * @date 2020/10/23
 * @version 1.0.1
 */
public final class IpUtil {
    public static final String LOCAL_MACHINE_IP = "127.0.0.1";
    public static final String UNKNOWN_FLAG = "unknown";
    public static final String X_FORWARDED_FOR_FLAG = "x-forwarded-for";
    public static final String PROXY_CLIENT_IP_FLAG = "Proxy-Client-IP";
    public static final String WL_PROXY_CLIENT_IP_FLAG = "WL-Proxy-Client-IP";

    public static final Integer MAX_SINGLE_IP_LENGTH = 15;


    /**
     * Get request client ip address
     *
     * @param request Current http request context
     * @return IP address or IP address chain
     * @throws UnknownHostException IP address is illegal, unknown or is not given
     */
    public static String getIpAddress(HttpServletRequest request) throws UnknownHostException {
        String ipAddress;
        ipAddress = request.getHeader(X_FORWARDED_FOR_FLAG);
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN_FLAG.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader(PROXY_CLIENT_IP_FLAG);
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN_FLAG.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader(WL_PROXY_CLIENT_IP_FLAG);
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN_FLAG.equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals(LOCAL_MACHINE_IP)) {
                InetAddress inet = InetAddress.getLocalHost();
                ipAddress = inet.getHostAddress();
            }
        }

        if (ipAddress != null && ipAddress.length() > MAX_SINGLE_IP_LENGTH) {
            String split = ",";
            if (ipAddress.indexOf(split) > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(split));
            }
        }
        return ipAddress;
    }
}
