package com.sgcc.sgcc_mgr_qx.util;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.InetSocketAddress;

//@Component
//public class NettyTcpServer implements CommandLineRunner {
public class NettyTcpServer  {

//    @Override
    public void run(String... args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new ChannelInitializer<NioSocketChannel>() {
                         @Override
                         protected void initChannel(NioSocketChannel ch) throws Exception {
                             ChannelPipeline pipeline = ch.pipeline();

                             // 配置 SOCKS5 代理处理器
                             Socks5ProxyHandler proxyHandler = new Socks5ProxyHandler(
                                     new InetSocketAddress("171.115.221.199", 57225));
                             pipeline.addLast(proxyHandler);

                             // 添加 HTTP 请求处理器
                             pipeline.addLast(new HttpServerCodec());
                             pipeline.addLast(new HttpObjectAggregator(8192));
                             pipeline.addLast(new ChunkedWriteHandler());

                             // 处理来自客户端的请求并通过 SOCKS 代理转发
                             pipeline.addLast(new SimpleChannelInboundHandler<HttpObject>() {
                                 @Override
                                 protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                                     if (msg instanceof FullHttpRequest) {
                                         FullHttpRequest request = (FullHttpRequest) msg;
                                         System.out.println("Request received from client: " + request.uri());

                                         // 构造并返回响应
                                         FullHttpResponse response = new DefaultFullHttpResponse(
                                                 HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                                         response.content().writeBytes("Request processed via SOCKS proxy".getBytes());
                                         ctx.writeAndFlush(response);
                                     }
                                 }
                             });
                         }
                     });

            // 绑定本地端口
            ChannelFuture future = bootstrap.bind(8769).sync();
            System.out.println("Server is running on port 8769");

            // 等待服务器通道关闭
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
