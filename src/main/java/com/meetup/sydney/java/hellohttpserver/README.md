# Hello HTTP Server

This example is a simple HTTP server. It returns a static HTML page.

Try it out with a browser:
```
http://localhost:8080
```

Or run Apache bench to throw a few requests at it:
```
ab -k -c 3 -t 30 -n 500000 http://localhost:8080/
```

* Note, the -k (keep alive) is important! It's likely your infrastructure will be re-using connections with keep-alive so more realistic. Also if you're running a test locally you'll quickly run out of ports with sockets in TIME_WAIT if you create a new connection for every request. 

Example apache bench output localhost -> localhost:
```
Concurrency Level:      3
Time taken for tests:   8.413 seconds
Complete requests:      500000
Failed requests:        0
Keep-Alive requests:    500000
Total transferred:      100500000 bytes
HTML transferred:       49000000 bytes
Requests per second:    59432.93 [#/sec] (mean)
Time per request:       0.050 [ms] (mean)
Time per request:       0.017 [ms] (mean, across all concurrent requests)
Transfer rate:          11666.03 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.0      0       0
Processing:     0    0   0.0      0       2
Waiting:        0    0   0.0      0       2
Total:          0    0   0.0      0       2
```