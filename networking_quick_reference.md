# Networking Quick Reference
## Firewalls, TLS, Certificates, IPs, Ports, DNS, CDNs, and App-to-App Connectivity

This guide is a practical reference for software engineers and networking interviews.

---

## 1. The core mental model

When **Application A** calls **Application B**, think through the path in this order:

```text
Application A
    |
    | 1. DNS lookup
    v
Hostname -> IP address
    |
    | 2. Routing
    v
Firewall / network controls
    |
    | 3. TCP connection to IP:Port
    v
TLS handshake
    |
    | 4. Certificate validation + encryption
    v
Authentication / Authorization
    |
    | 5. API request
    v
Application B
```

A useful memory aid:

```text
DNS          = Where is the server?
IP           = Which network endpoint?
Port         = Which service on that endpoint?
Routing      = How do packets get there?
Firewall     = Is this connection allowed?
TLS          = Is the connection encrypted?
Certificate  = Am I talking to the expected server?
Authentication = Who are you?
Authorization  = What are you allowed to do?
```

---

## 2. IP address

An IP address identifies a network endpoint.

Examples:

```text
10.20.30.40
192.168.1.10
```

Common private IPv4 ranges:

```text
10.0.0.0/8
172.16.0.0/12
192.168.0.0/16
```

A private IP is normally not directly reachable from the public internet unless a gateway, proxy, NAT, VPN, or other route provides access.

---

## 3. Port

A port identifies a service running on a host.

Think:

```text
IP address = building
Port       = door
```

Common examples:

| Port | Typical use |
|---:|---|
| 22 | SSH |
| 53 | DNS |
| 80 | HTTP |
| 443 | HTTPS |
| 5432 | PostgreSQL |
| 3306 | MySQL |
| 9092 | Kafka, commonly |

Example:

```text
10.20.30.40:443
```

means: connect to host `10.20.30.40`, TCP port `443`.

---

## 4. Firewall

A firewall decides which network traffic is allowed.

Example rule:

```text
SOURCE:      10.10.1.25
DESTINATION: 10.20.30.40
PROTOCOL:    TCP
PORT:        443
ACTION:      ALLOW
```

Meaning:

```text
App A (10.10.1.25)
        |
        | TCP 443
        v
App B (10.20.30.40)
```

This is called **least-privilege networking**: only open the path that is actually required.

### Inbound vs outbound

For:

```text
App A -----> App B
```

- App A sees this as outbound traffic.
- App B sees this as inbound traffic.

Some enterprise environments control both.

### Stateful firewall

Most enterprise firewalls are stateful.

If App A initiates an allowed connection:

```text
App A ---- request ----> App B
App A <--- response ---- App B
```

the firewall remembers the session and normally allows the return traffic automatically.

---

## 5. Are you safe just because the app is not internet-facing?

**Safer, yes. Fully safe, no.**

If an app is not exposed to the public internet, that removes a large attack surface.

But an internal app can still be attacked by:

- a compromised internal server;
- another application with network access;
- stolen credentials;
- an insider;
- malware already inside the network;
- insecure code;
- a badly configured proxy or gateway.

Example:

```text
Internet
   X  blocked
   |
Corporate network
   |
   +-- App A
   +-- App B
   +-- Compromised App C ---> attacks App B
```

Moving from one compromised internal system to another is called **lateral movement**.

The right concept is **defense in depth**:

```text
Firewall
+ TLS
+ Authentication
+ Authorization
+ Secure coding
+ Secret vault
+ Patching
+ Monitoring
+ Least privilege
```

A firewall is one protection layer, not the entire security model.

---

## 6. TLS

TLS protects data while it travels across the network.

Without TLS:

```text
App A ---> readable traffic ---> App B
```

With TLS:

```text
App A ---> encrypted traffic ---> App B
```

TLS provides:

1. **Confidentiality** — traffic is encrypted.
2. **Integrity** — tampering can be detected.
3. **Authentication** — usually the client verifies the server identity.

HTTPS means:

```text
HTTP + TLS
```

Typically on TCP port `443`.

---

## 7. TLS handshake

Before HTTPS data is exchanged:

```text
Client                         Server
  |                               |
  | supported TLS versions ------>|
  |<------ certificate + settings |
  |                               |
  | validate certificate          |
  | establish encryption keys     |
  |                               |
  |==== encrypted connection =====|
```

After this handshake, normal HTTP requests are sent through the encrypted connection.

---

## 8. Certificate

A certificate helps the client verify that the server really owns the hostname it is claiming.

Suppose the client connects to:

```text
https://api.company.com
```

The server presents a certificate containing things such as:

```text
Hostname / SAN: api.company.com
Issuer: Corporate CA
Expiration date
Public key
```

The client checks:

```text
Does the hostname match?
Is the certificate still valid?
Is the issuer trusted?
Is the certificate chain valid?
```

If Java does not trust the certificate chain, a common error is:

```text
PKIX path building failed
```

---

## 9. Certificate Authority (CA)

A Certificate Authority signs certificates.

Typical trust chain:

```text
Trusted Root CA
      |
Intermediate CA
      |
api.company.com certificate
```

The operating system or JVM has a trust store containing trusted CAs.

Large companies often use their own internal corporate CA.

---

## 10. TLS vs authentication

They solve different problems:

```text
TLS            = secure the connection
Authentication = identify the caller
Authorization  = decide what the caller may do
```

Example:

```text
HTTPS + Basic Authentication
```

TLS encrypts the traffic.

Basic Auth tells the server who the client is.

Important:

```text
Basic Auth over HTTP  = unsafe
Basic Auth over HTTPS = commonly acceptable
```

Base64 is encoding, not encryption.

---

## 11. mTLS

Normal TLS commonly verifies only the server:

```text
Client ---> verifies certificate ---> Server
```

Mutual TLS verifies both sides:

```text
Client <--- certificates ---> Server
```

Both applications have certificates.

mTLS is common in high-security service-to-service environments.

---

## 12. DNS

DNS converts a hostname into an IP address.

```text
service.company.com
        |
        v
10.20.30.40
```

Using a hostname is usually better than hardcoding an IP because the infrastructure can change IP addresses while the DNS name remains stable.

### DNS is not a firewall

DNS answers:

```text
Where is service.company.com?
```

A firewall answers:

```text
Are you allowed to connect to it?
```

DNS can work perfectly while the firewall still blocks the connection.

---

## 13. Routing

Routing decides how packets reach another network.

Example:

```text
Application Server
      |
      v
Router
      |
      v
Corporate Network
      |
      v
Destination Server
```

A firewall rule can be correct and the connection can still fail if there is no route to the destination.

---

## 14. Subnet

A subnet is a logical network range.

Example:

```text
10.20.30.0/24
```

This is commonly a range containing addresses such as:

```text
10.20.30.1
...
10.20.30.254
```

Subnets help organize networks and apply routing and security policies.

---

## 15. TCP

TCP is a connection-oriented transport protocol.

Used by many protocols:

```text
HTTPS
SSH
PostgreSQL
Kafka
```

TCP provides:

- reliable delivery;
- ordering;
- retransmission;
- connection state.

### TCP three-way handshake

```text
Client            Server
  |                  |
  | SYN ------------>|
  |<--------- SYN-ACK|
  | ACK ------------>|
  |                  |
Connection established
```

---

## 16. UDP

UDP is connectionless.

It does not guarantee:

- delivery;
- ordering;
- retransmission.

It has lower overhead and is used for workloads where speed matters more than guaranteed delivery.

Some DNS traffic and real-time protocols use UDP.

---

## 17. Load balancer

Suppose an application runs on three servers:

```text
Server 1
Server 2
Server 3
```

A load balancer gives clients one stable endpoint:

```text
Client
   |
   v
Load Balancer
   |
   +--> Server 1
   +--> Server 2
   +--> Server 3
```

It can:

- distribute requests;
- perform health checks;
- stop routing to unhealthy servers;
- provide one stable DNS name or virtual IP.

---

## 18. Reverse proxy

A reverse proxy sits in front of backend applications:

```text
Client
   |
   v
Reverse Proxy
   |
   v
Application
```

It may handle:

- TLS termination;
- authentication;
- routing;
- logging;
- rate limiting;
- load balancing;
- header management.

Examples include NGINX, HAProxy, and Apache HTTP Server.

---

## 19. CDN

CDN means **Content Delivery Network**.

A CDN is mainly used for public/internet-facing applications.

```text
User
  |
  v
Nearby CDN Edge
  |
  v
Origin Server
```

A CDN can provide:

- caching;
- lower latency;
- DDoS protection;
- TLS termination;
- WAF integration;
- global traffic distribution.

Examples include Cloudflare, Akamai, Fastly, and AWS CloudFront.

For normal internal App A -> App B communication, a CDN is usually not involved.

---

## 20. NAT

NAT translates one IP address into another.

Example:

```text
Internal VM
10.1.2.3
    |
    v
NAT Gateway
    |
    v
Public IP
    |
    v
Internet
```

This lets internal private-IP systems access external networks without exposing their private address directly.

---

## 21. VPN

A VPN creates an encrypted network connection.

Example:

```text
Company A Network
       |
       | encrypted VPN tunnel
       |
Company B Network
```

The networks may then communicate using private IP addresses.

---

## 22. Traditional firewall vs WAF

A traditional network firewall typically works with:

```text
Source IP
Destination IP
Port
Protocol
Connection state
```

A Web Application Firewall (WAF) understands HTTP/API traffic and can detect things such as:

- SQL injection;
- cross-site scripting;
- suspicious URLs;
- malicious HTTP payloads;
- bots.

Think:

```text
Network Firewall = controls network access
WAF              = inspects web/API traffic
```

---

## 23. Real app-to-app example

Assume:

```text
App A:
10.10.1.25

App B hostname:
service-b.company.com

App B:
10.20.30.40

Protocol:
HTTPS

Port:
443
```

The full flow is:

```text
1. App A asks DNS:
   service-b.company.com?

2. DNS replies:
   10.20.30.40

3. Routing determines a path to 10.20.30.40.

4. App A opens:
   TCP 10.20.30.40:443

5. Firewall checks:
   source      = 10.10.1.25
   destination = 10.20.30.40
   port        = 443
   protocol    = TCP

6. Firewall allows the connection.

7. TLS handshake starts.

8. App A validates App B's certificate.

9. TLS encryption is established.

10. App A authenticates to App B.

11. App A sends the HTTP request.

12. App B returns the response over the same stateful connection.
```

---

## 24. Troubleshooting app-to-app connectivity

A useful order is:

```text
DNS
 ↓
Routing
 ↓
Firewall
 ↓
TCP port
 ↓
TLS
 ↓
Certificate trust
 ↓
Authentication
 ↓
Authorization
 ↓
Application/API
```

Useful Linux commands:

```bash
# DNS
nslookup service.company.com

# More detailed DNS
dig service.company.com

# TCP / firewall connectivity
nc -vz service.company.com 443

# TLS handshake and certificate
openssl s_client   -connect service.company.com:443   -servername service.company.com

# HTTPS test
curl -v https://service.company.com/

# Local routes
ip route

# Network interfaces
ip addr

# Local listening ports
ss -lntp

# Test certificate from Java's perspective
keytool -printcert   -sslserver service.company.com:443
```

### Why HTTP 401 can be good during testing

If you intentionally call without credentials and receive:

```text
401 Unauthorized
```

that often proves:

```text
DNS       OK
Routing   OK
Firewall  OK
TCP       OK
TLS       OK
API       reachable
Auth      missing/invalid
```

That helps isolate the problem.

---

## 25. Common failures

### DNS problem

```text
Could not resolve host
```

Investigate DNS.

### Firewall or routing problem

```text
Connection timed out
```

Investigate route/firewall.

### No service on the port

```text
Connection refused
```

The host may be reachable, but nothing is listening there or it is actively rejecting the connection.

### TLS trust issue

```text
PKIX path building failed
```

The JVM does not trust the server certificate chain.

### Certificate hostname mismatch

```text
certificate does not match hostname
```

The certificate was issued for another DNS name.

### Authentication problem

```text
401 Unauthorized
```

Credentials are missing or invalid.

### Authorization problem

```text
403 Forbidden
```

The identity may be valid but not permitted to perform that operation.

---

## 26. Why secure coding still matters behind a firewall

Suppose an internal API has a vulnerability.

Even if the internet cannot reach it, a compromised internal application may still exploit it.

Therefore:

```text
Internal-only != trusted
```

Modern security often uses a **Zero Trust** principle:

> Do not trust something only because it is inside the corporate network.

Use authentication and authorization between services where appropriate.

---

## 27. Defense in depth

A strong enterprise service-to-service design often combines:

```text
Private network
       +
Firewall allowlist
       +
TLS
       +
Certificate validation
       +
Service authentication
       +
Authorization
       +
Secret vault
       +
Secure coding
       +
Patching
       +
Logging / monitoring
```

If one layer fails, another layer still provides protection.

---

## 28. Interview questions and short answers

### What is a firewall?

A firewall controls network traffic using rules such as source IP, destination IP, protocol, port, and connection state.

### What is the difference between IP and port?

An IP identifies a network endpoint. A port identifies a specific service on that endpoint.

### What does DNS do?

DNS converts hostnames such as `api.company.com` into IP addresses.

### What does TLS provide?

TLS provides encryption, integrity, and identity verification using certificates.

### What is HTTPS?

HTTP running over TLS, normally using TCP port 443.

### What is a certificate?

A certificate binds an identity such as a hostname to a public key and is signed by a trusted Certificate Authority.

### What is mTLS?

Mutual TLS means both the client and server authenticate using certificates.

### What is a stateful firewall?

A stateful firewall tracks active connections and normally allows return traffic for a connection that was permitted.

### What is the difference between 401 and 403?

- `401` generally means authentication is missing or invalid.
- `403` generally means the identity is known but lacks permission.

### What is a load balancer?

A load balancer distributes traffic across multiple backend servers and can stop sending traffic to unhealthy servers.

### What is a reverse proxy?

A reverse proxy receives client requests and forwards them to backend services, often handling TLS, routing, authentication, logging, or rate limiting.

### What is a CDN?

A CDN serves content and traffic through geographically distributed edge locations to improve performance and security.

### What is NAT?

NAT translates IP addresses, commonly letting private internal hosts communicate externally through another address.

### What is the difference between TCP and UDP?

TCP is connection-oriented and provides reliable ordered delivery. UDP is connectionless and has lower overhead but does not guarantee delivery.

### What happens when you type an HTTPS URL?

Simplified:

```text
DNS lookup
    ↓
Routing
    ↓
TCP connection
    ↓
TLS handshake
    ↓
Certificate validation
    ↓
HTTP request
    ↓
HTTP response
```

### Is a service secure if it is behind a firewall?

No. A firewall reduces exposure but security also requires TLS, authentication, authorization, secure code, secrets management, patching, and monitoring.

---

## 29. Interview troubleshooting mental model

When someone says:

> "App A cannot connect to App B."

Work downward:

```text
1. NAME
   Does DNS resolve?

2. LOCATION
   Is the IP correct?

3. PATH
   Is there a route?

4. PERMISSION
   Does the firewall allow source -> destination:port?

5. SERVICE
   Is anything listening on the destination port?

6. SECURITY
   Does the TLS handshake work?

7. TRUST
   Is the certificate trusted and hostname valid?

8. IDENTITY
   Does authentication work?

9. ACCESS
   Is the caller authorized?

10. APPLICATION
    Is the API itself healthy?
```

This is a very strong interview answer because it separates the problem by layer.

---

## 30. One-minute cheat sheet

```text
DNS
Name -> IP

IP
Network endpoint

Port
Service on the endpoint

Routing
Path packets take

Firewall
Controls allowed network connections

TCP
Reliable connection-oriented transport

TLS
Encryption + integrity + identity verification

Certificate
Proves identity through a trusted CA

HTTPS
HTTP over TLS, normally port 443

mTLS
Both client and server use certificates

Load Balancer
Distributes traffic across backend servers

Reverse Proxy
Front door for backend applications

CDN
Distributed internet edge / caching / protection

NAT
Translates IP addresses

VPN
Encrypted network tunnel

WAF
Protects HTTP/API traffic
```

---

## 31. Final security rule

Remember these three lines:

```text
"Internal" does not mean "trusted."

"Behind a firewall" does not mean "secure."

"TLS enabled" does not mean "authorized."
```

The strongest design combines:

```text
Network controls
+
Encryption
+
Identity
+
Least privilege
+
Secure code
+
Monitoring
```

That is the foundation of modern enterprise networking and application security.
