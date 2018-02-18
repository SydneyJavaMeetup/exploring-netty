package com.meetup.sydney.java.ollehworld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import static io.netty.buffer.Unpooled.wrappedBuffer;

/**
 * A simple Netty service - return hello world - reversed!
 */
public class OllehWorld {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap b = new ServerBootstrap()
                    .group(eventLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>(){
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
                                    int length = msg.readableBytes();
                                    if (length > 2) {
                                        byte[] reversed = new byte[length];
                                        for (int i = reversed.length - 3; i >= 0; i--) {
                                            reversed[i] = msg.readByte();
                                        }
                                        reversed[reversed.length - 2] = '\r';
                                        reversed[reversed.length - 1] = '\n';
                                        ctx.writeAndFlush(wrappedBuffer(reversed));
                                    }
                                }
                            });
                        }
                    });

            ChannelFuture f = b.bind(8023).sync();
            System.err.println("Server running! To connect use telnet:\ntelnet localhost 8023");
            f.channel().closeFuture().sync();

        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

}
