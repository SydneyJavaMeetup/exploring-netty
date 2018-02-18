# exploring-netty
This is a repository for code demonstrated in a talk (or talks) on Netty (https://netty.io).

#### Creating a Netty Project 
When creating a project depending on Netty, create a Maven project and grab the latest version of the Netty Maven dependency from here:
http://netty.io/downloads.html
e.g.
```$xml
<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.1.21.Final</version>
</dependency>
```

Take a look at a few of the examples here to see how to code a Netty server:

Socket Server: [OllehWorld](https://github.com/SydneyJavaMeetup/exploring-netty/tree/master/src/main/java/com/meetup/sydney/java/ollehworld)

HTTP Server: [HelloHttpServer](https://github.com/SydneyJavaMeetup/exploring-netty/tree/master/src/main/java/com/meetup/sydney/java/hellohttpserver)

#### Bootstrapping a Server
When you build a server application in Netty, you'll initialize a ServerBootstrap with the following:
* EventLoopGroup(s)
* Channel Class
* Channel Initializer 

__* What's a Channel?__
A channel represents a single connection to the server, which passes I/O events along a pipeline of channel handlers you define.

##### EventLoopGroup(s)
The first thing you'll do is create a thread pool (a Netty specific extension of ScheduledExecutorService with semantics for processing Channel events). 

Regardless of the threading model, you still need to essentially keep to only non-blocking operations on the threads in these thread pools. The choices here are really about the scalability requirements of your application, and no option allows you to block the calling thread safely. 

Depending on the needs of your application you can use more complex or simpler threading approaches:

**Single-Threaded (like NodeJS!)**: This is the absolute simplest model. Everything in your server will run on a single thread. As long as your I/O channel operations are non-blocking you can still build a very high throughput service this way. It requires discipline to never block, otherwise the server will hang and stop accepting new connections or processing I/O events.
```$java
EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
bootstrap.group(eventLoopGroup);
``` 

**Multi-Threaded (boss/worker groups)**: Create a one thread pool for accepting sockets (boss) and another for passing I/O events to channel pipelines (worker). Typically use a single thread for the boss event loop group and a default (number of processors * 2 threads) event loop group for the worker threads.
```$java
EventLoopGroup bossGroup = new NioEventLoopGroup(1);
EventLoopGroup workerGroup = new NioEventLoopGroup();
bootstrap.group(bossGroup, workerGroup);
``` 

##### Channel Class
The channel class is responsible for creating new channels. It represents a source of new channels, i.e. accepting a socket connection. 

##### Channel Initializer
The channel initializer is invoked when a new channel is being created to construct the pipeline of handlers which I/O events will pass through. Each handler can write data on the channel. It could also just modify the incoming data and pass along a different object to the next handler. A full pipeline might look like this:
data read from socket -> ByteBuf -> HttpDecoder -> Process HTTP request -> HttpEncoder -> ByteBuf -> data written to socket


