package com.sgcc.sgcc_mgr_qx.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;

public class LocalTcpToSocksProxyForRedis {

    public static void main(String[] args) throws IOException {
        int localPort = 6379;  // 本地监听的端口，用于接收 Redis 请求
        String proxyHost = "171.115.221.199";  // SOCKS 代理地址
        int proxyPort = 57225;  // SOCKS 代理端口

        String targetHost = "192.168.0.106";  // 目标服务器 IP 地址
        int targetPort = 6379;  // 目标服务器的端口（Redis 的 6379 端口）

        // 首先测试是否可以通过 SOCKS 代理连接到目标 Redis 服务器
        if (canConnectThroughSocksProxy(proxyHost, proxyPort, targetHost, targetPort)) {
            System.out.println("Connection to " + targetHost + ":" + targetPort + " through SOCKS proxy is successful.");

            // 创建一个 ServerSocket 来监听本地的 6379 端口
            try (ServerSocket serverSocket = new ServerSocket(localPort)) {
                System.out.println("Local TCP proxy for Redis is running on port " + localPort + "...");

                while (true) {
                    // 接收来自 Redis 客户端的连接
                    Socket clientSocket = serverSocket.accept();

                    // 处理客户端的请求
                    new Thread(() -> handleClient(clientSocket, proxyHost, proxyPort, targetHost, targetPort)).start();
                }
            }
        } else {
            System.out.println("Failed to connect to " + targetHost + ":" + targetPort + " through SOCKS proxy.");
        }
    }

    // 通过 SOCKS 代理测试连接目标服务器
    private static boolean canConnectThroughSocksProxy(String proxyHost, int proxyPort, String targetHost, int targetPort) {
        try {
            // 创建 SOCKS 代理对象
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));

            // 通过 SOCKS 代理连接到目标 Redis 服务器
            try (Socket socket = new Socket(proxy)) {
                socket.connect(new InetSocketAddress(targetHost, targetPort), 5000);  // 连接超时时间设为 5 秒
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 处理客户端请求，将其通过 SOCKS 代理转发到目标 Redis 服务器
    private static void handleClient(Socket clientSocket, String proxyHost, int proxyPort, String targetHost, int targetPort) {
        try {
            // 创建 SOCKS 代理对象
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));

            // 通过 SOCKS 代理连接到目标 Redis 服务器
            Socket proxySocket = new Socket(proxy);
            proxySocket.connect(new InetSocketAddress(targetHost, targetPort));

            // 创建两个线程，用于在客户端和目标服务器之间双向转发数据
            new Thread(() -> forwardData(clientSocket, proxySocket)).start();
            new Thread(() -> forwardData(proxySocket, clientSocket)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将数据从输入流转发到输出流
    private static void forwardData(Socket inputSocket, Socket outputSocket) {
        try (InputStream inputStream = inputSocket.getInputStream();
             OutputStream outputStream = outputSocket.getOutputStream()) {

            // 设置超时和 KeepAlive
            inputSocket.setSoTimeout(10000);  // 10秒读取超时
            inputSocket.setKeepAlive(true);

            outputSocket.setSoTimeout(10000);  // 10秒读取超时
            outputSocket.setKeepAlive(true);

            byte[] buffer = new byte[8192];  // 调整缓冲区大小
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                outputStream.flush();
            }
        } catch (SocketException e) {
            // 处理连接重置异常
            System.out.println("Connection reset: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
