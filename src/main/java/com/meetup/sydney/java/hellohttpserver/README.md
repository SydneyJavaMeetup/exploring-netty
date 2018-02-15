# Hello HTTP Server

This example is a simple HTTP server. It returns a static HTML page.

Try it out with a browser:
```
http://localhost:8080
```

Or run Apache bench to throw a few requests at it:
```
ab -c 5 -n 10000
```

Or a Python client - boom!
```
boom http://localhost:8080/ -c 5 -n 10000
```