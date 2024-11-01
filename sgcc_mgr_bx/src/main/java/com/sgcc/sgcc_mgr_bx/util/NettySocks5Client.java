package com.sgcc.sgcc_mgr_bx.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.InetSocketAddress;

public class NettySocks5Client {

    public static void main(String[] args) throws Exception {
        String socksProxyHost = "171.115.221.199";
        int socksProxyPort = 57225;
        String remoteHost = "192.168.0.106";
        int remotePort = 32080;

        // 创建Netty客户端
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            // 添加SOCKS5代理的处理器
                            ProxyHandler proxyHandler = new Socks5ProxyHandler(
                                    new InetSocketAddress(socksProxyHost, socksProxyPort));
                            pipeline.addLast(proxyHandler);

                            // 添加HTTP请求处理器（根据你需要可以调整这里）
                            pipeline.addLast(new HttpClientCodec());
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            pipeline.addLast(new ChunkedWriteHandler());

                            // 处理响应
                            pipeline.addLast(new SimpleChannelInboundHandler<HttpObject>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                                    if (msg instanceof FullHttpResponse) {
                                        FullHttpResponse response = (FullHttpResponse) msg;
                                        System.out.println("Response: " + response.content().toString(io.netty.util.CharsetUtil.UTF_8));
                                    }
                                }
                            });
                        }
                    });

            // 连接远程服务器（通过SOCKS代理）
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(remoteHost, remotePort)).sync();

            // 构造HTTP请求（例如：GET请求）
            HttpRequest request = new DefaultFullHttpRequest(
                    HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
            request.headers().set(HttpHeaderNames.HOST, remoteHost);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);

            // 发送请求
            channelFuture.channel().writeAndFlush(request);

            // 等待请求完成
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
