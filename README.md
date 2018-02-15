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
