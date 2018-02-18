package com.meetup.sydney.java.hellohttpserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.buffer.Unpooled.wrappedBuffer;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HelloHttpServer {
    static final byte[] PAGE = "<html><head><link rel=\"icon\" href=\"data:;base64,iVBORw0KGgo=\"></head><body>Hi there!</body></html>".getBytes(CharsetUtil.UTF_8);

    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("codec", new HttpServerCodec());
                            p.addLast("aggregator", new HttpObjectAggregator(512 * 1024));
                            p.addLast(new SimpleChannelInboundHandler<FullHttpRequest>(){

                                @Override
                                public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
                                    FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, wrappedBuffer(PAGE));
                                    response.headers().set("Content-Type", "text/html; charset=utf-8");
                                    response.headers().setInt("Content-Length", response.content().readableBytes());

                                    if (!isKeepAlive(httpRequest)) {
                                        ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                                    } else {
                                        response.headers().set("Connection", "keep-alive");
                                        ctx.write(response);
                                    }
                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) {
                                    ctx.flush();
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
            System.err.println("Open your web browser and navigate to http://127.0.0.1:8080/");
            ch.closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
