Here’s a cheat sheet of the **100 most commonly used HTTP headers**, 
categorized by their purpose (request, response, or both).  

---

### **General Headers** (Used in both requests & responses)  
1. `Cache-Control` – Directives for caching (e.g., `no-cache`, `max-age`)  
2. `Connection` – Controls whether the network connection stays open  
3. `Date` – Timestamp of the message  
4. `Pragma` – Legacy HTTP/1.0 cache control (e.g., `no-cache`)  
5. `Trailer` – Lists headers present after chunked transfer  
6. `Transfer-Encoding` – Specifies encoding (e.g., `chunked`)  
7. `Upgrade` – Requests protocol upgrade (e.g., HTTP/2, WebSocket)  
8. `Via` – Proxies through which the request passed  
9. `Warning` – Additional warning information  

---

### **Request Headers**  
10. `Accept` – Media types the client can process (e.g., `application/json`)  
11. `Accept-Charset` – Character sets the client supports  
12. `Accept-Encoding` – Compression methods (e.g., `gzip`, `deflate`)  
13. `Accept-Language` – Preferred languages (e.g., `en-US`)  
14. `Authorization` – Credentials for authentication (e.g., `Bearer token`)  
15. `Cookie` – Client-side cookies  
16. `Expect` – Expected server behavior (e.g., `100-continue`)  
17. `Forwarded` – Proxied client info (IP, protocol)  
18. `From` – Email of the user making the request  
19. `Host` – The domain name of the server (required in HTTP/1.1)  
20. `If-Match` – Conditional request (ETag matching)  
21. `If-Modified-Since` – Returns 304 if unchanged since a date  
22. `If-None-Match` – Returns 304 if ETag doesn’t match  
23. `If-Range` – Resumes partial downloads if unchanged  
24. `If-Unmodified-Since` – Fails if modified since a date  
25. `Max-Forwards` – Limits proxy forwarding  
26. `Origin` – Origin of a CORS request  
27. `Proxy-Authorization` – Credentials for proxy auth  
28. `Range` – Requests partial content (e.g., `bytes=0-500`)  
29. `Referer` – URL of the previous page  
30. `TE` – Transfer encodings the client accepts  
31. `User-Agent` – Client software info (browser, OS)  

---

### **Response Headers**  
32. `Accept-Ranges` – Indicates if server supports partial requests (`bytes`/`none`)  
33. `Age` – Time since response was generated (in seconds)  
34. `Allow` – Lists supported HTTP methods (e.g., `GET, POST`)  
35. `Clear-Site-Data` – Clears browsing data (e.g., `"cookies"`)  
36. `Content-Disposition` – Suggests filename for downloads (`attachment; filename="file.txt"`)  
37. `Content-Encoding` – Compression method (e.g., `gzip`)  
38. `Content-Language` – Language of the response  
39. `Content-Length` – Size of the response body (in bytes)  
40. `Content-Location` – Alternate URL for the resource  
41. `Content-Range` – Partial content location (e.g., `bytes 0-100/200`)  
42. `Content-Security-Policy` (CSP) – Security policy for resources  
43. `Content-Type` – Media type (e.g., `text/html; charset=utf-8`)  
44. `ETag` – Entity tag for caching  
45. `Expires` – When the response becomes stale  
46. `Last-Modified` – When the resource was last changed  
47. `Location` – URL for redirection (3xx responses)  
48. `Proxy-Authenticate` – Proxy auth requirements  
49. `Refresh` – Redirect after delay (e.g., `5; url=/new`)  
50. `Retry-After` – Time to wait before retrying (e.g., `120`)  
51. `Server` – Server software info (e.g., `Apache/2.4`)  
52. `Set-Cookie` – Sends cookies to the client  
53. `Strict-Transport-Security` (HSTS) – Enforces HTTPS  
54. `Vary` – Caching key variations (e.g., `Accept-Encoding`)  
55. `WWW-Authenticate` – Authentication method (e.g., `Basic`)  
56. `X-Content-Type-Options` – Prevents MIME sniffing (`nosniff`)  
57. `X-Frame-Options` – Prevents clickjacking (`DENY`, `SAMEORIGIN`)  
58. `X-XSS-Protection` – Enables XSS filter (`1; mode=block`)  

---

### **CORS (Cross-Origin Resource Sharing) Headers**  
59. `Access-Control-Allow-Origin` – Allowed origins (`*` or `example.com`)  
60. `Access-Control-Allow-Credentials` – Allows credentials (`true`)  
61. `Access-Control-Allow-Headers` – Allowed request headers  
62. `Access-Control-Allow-Methods` – Allowed HTTP methods  
63. `Access-Control-Expose-Headers` – Headers exposed to JS  
64. `Access-Control-Max-Age` – Caches CORS preflight (in seconds)  
65. `Access-Control-Request-Headers` – Headers in preflight requests  
66. `Access-Control-Request-Method` – Method in preflight requests  

---

### **Security Headers**  
67. `Cross-Origin-Embedder-Policy` (COEP) – Controls resource embedding  
68. `Cross-Origin-Opener-Policy` (COOP) – Isolates browsing context  
69. `Cross-Origin-Resource-Policy` (CORP) – Blocks cross-origin requests  
70. `Feature-Policy` (Deprecated → Permissions-Policy) – Controls browser features  
71. `Permissions-Policy` – Replaces `Feature-Policy`  
72. `Public-Key-Pins` (HPKP) – Pins TLS certificates (deprecated)  
73. `Referrer-Policy` – Controls `Referer` header (`no-referrer`, `strict-origin`)  
74. `Report-To` – Reporting API endpoint  
75. `Expect-CT` – Certificate Transparency enforcement  
76. `X-Permitted-Cross-Domain-Policies` – Flash/PDF policies  

---

### **Non-Standard / Custom Headers**  
77. `X-Forwarded-For` – Client IP behind proxies  
78. `X-Forwarded-Host` – Original `Host` header  
79. `X-Forwarded-Proto` – Original protocol (`http`/`https`)  
80. `X-Request-ID` – Unique request identifier  
81. `X-Correlation-ID` – Request correlation ID  
82. `X-Powered-By` – Server tech (e.g., `PHP/8.1`)  
83. `X-RateLimit-Limit` – Max allowed requests  
84. `X-RateLimit-Remaining` – Remaining requests  
85. `X-RateLimit-Reset` – When rate limit resets  
86. `X-DNS-Prefetch-Control` – Controls DNS prefetching (`on`/`off`)  
87. `X-UA-Compatible` – IE rendering mode (`IE=edge`)  
88. `X-Content-Duration` – Audio/video duration (deprecated)  
89. `X-Download-Options` – Disables IE download execution  
90. `X-Robots-Tag` – Controls search engine indexing  

---

### **WebSocket Headers**  
91. `Sec-WebSocket-Key` – WebSocket handshake key  
92. `Sec-WebSocket-Accept` – Server’s handshake response  
93. `Sec-WebSocket-Version` – WebSocket protocol version  
94. `Sec-WebSocket-Protocol` – Subprotocol negotiation  
95. `Sec-WebSocket-Extensions` – Extensions support  

---

### **Other Notable Headers**  
96. `Alt-Svc` – Alternative services (HTTP/3, QUIC)  
97. `Large-Allocation` – Warns about large memory allocation  
98. `Link` – Resource hints (`preload`, `preconnect`)  
99. `SourceMap` – Links to source maps for debugging  
100. `Timing-Allow-Origin` – Controls resource timing access  

---


