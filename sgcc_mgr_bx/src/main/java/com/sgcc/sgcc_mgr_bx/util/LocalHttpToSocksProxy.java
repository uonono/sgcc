package com.sgcc.sgcc_mgr_bx.util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class LocalHttpToSocksProxy {

    public static void main(String[] args) throws Exception {
        // 创建一个简单的本地 HTTP 服务器
        HttpServer server = HttpServer.create(new InetSocketAddress(32080), 0); // 本地服务监听 8080 端口
        server.createContext("/", new ProxyHandler());
        server.setExecutor(null); // 使用默认的线程池
        server.start();
        System.out.println("Local proxy server is running on http://localhost:8085/");
    }

    // 处理来自浏览器的 HTTP 请求
    static class ProxyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // 获取浏览器请求的目标 URL
            String targetUrl = "http://192.168.0.106:32080" + exchange.getRequestURI().toString();
            System.out.println("Forwarding request to: " + targetUrl);

            // SOCKS 代理服务器的 IP 和端口
            String proxyHost = "l2.ttut.cc";
            int proxyPort = 57225;

            try {
                // 通过 SOCKS 代理连接到目标服务器
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));
                URL url = new URL(targetUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);

                // 设置 HTTP 请求方法（GET、POST等）
                connection.setRequestMethod(exchange.getRequestMethod());

                // 发送浏览器的 HTTP 请求头
                for (String headerKey : exchange.getRequestHeaders().keySet()) {
                    connection.setRequestProperty(headerKey, exchange.getRequestHeaders().getFirst(headerKey));
                }

                connection.setDoOutput(true);

                // 如果是 POST 请求，转发请求体
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    try (OutputStream os = connection.getOutputStream()) {
                        InputStream requestBody = exchange.getRequestBody();
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = requestBody.read(buffer)) != -1) {
                            os.write(buffer, 0, bytesRead);
                        }
                    }
                }

                // 读取目标服务器的响应
                int responseCode = connection.getResponseCode();
                InputStream responseStream = (responseCode >= 400)
                        ? connection.getErrorStream() : connection.getInputStream();

                // 将响应返回给浏览器
                exchange.getResponseHeaders().set("Content-Type", connection.getContentType());
                exchange.sendResponseHeaders(responseCode, 0);
                OutputStream responseBody = exchange.getResponseBody();

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = responseStream.read(buffer)) != -1) {
                    responseBody.write(buffer, 0, bytesRead);
                }

                responseBody.close();
                responseStream.close();
                connection.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                String errorResponse = "Error while forwarding request: " + e.getMessage();
                exchange.sendResponseHeaders(500, errorResponse.length());
                exchange.getResponseBody().write(errorResponse.getBytes());
                exchange.getResponseBody().close();
            }
        }
    }
}
