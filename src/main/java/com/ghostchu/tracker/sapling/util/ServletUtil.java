package com.ghostchu.tracker.sapling.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServletUtil {

    public static String ip(HttpServletRequest request) {
        String ip = request.getHeader("X-Rewrite-Peer-IP");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("CF-Connecting-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    @SneakyThrows(UnknownHostException.class)
    public static InetAddress inet(HttpServletRequest request) {
        return InetAddress.getByName(ip(request));
    }
}
