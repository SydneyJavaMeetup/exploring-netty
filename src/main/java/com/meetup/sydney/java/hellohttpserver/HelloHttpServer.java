package com.meetup.sydney.java.hellohttpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HelloHttpServer {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(10);
        try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>(){
                            @Override
                            protected void initChannel(SocketChannel ch) {
                                ChannelPipeline p = ch.pipeline();
                                p.addLast(new HttpServerCodec());
                                p.addLast(new HttpServerExpectContinueHandler());
                                p.addLast(new ChannelInboundHandlerAdapter(){

                                    @Override
                                    public void channelReadComplete(ChannelHandlerContext ctx) {
                                        ctx.flush();
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                        if (msg instanceof HttpRequest) {
                                            HttpRequest req = (HttpRequest) msg;

                                            String page = "<html><body>Hi there!</body></html>";

                                            boolean keepAlive = HttpUtil.isKeepAlive(req);
                                            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(page.getBytes(CharsetUtil.UTF_8)));
                                            response.headers().set("Content-Type", "text/html; charset=utf-8");
                                            response.headers().setInt("Content-Length", response.content().readableBytes());

                                            if (!keepAlive) {
                                                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                                            } else {
                                                response.headers().set("Connection", "keep-alive");
                                                ctx.write(response);
                                            }
                                        }
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                        cause.printStackTrace();
                                        ctx.close();
                                    }
                                });
                            }
                        });

                Channel ch = b.bind(8080).sync().channel();

                System.out.println("Open your web browser and navigate to http://127.0.0.1:8080/");

                ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
