### **Comprehensive Email Infrastructure Cheat Sheet**

_Covering SMTP, IMAP, DNS Records, Security Protocols, and More_

---

## **1. Core Protocols**

### **SMTP (Simple Mail Transfer Protocol)**

- **Purpose**: Send emails between servers (outbound).
- **Ports**:
  - `25` (Unencrypted, server-to-server).
  - `587` (Encrypted/TLS, client-to-server).
  - `465` (Deprecated but used for implicit SSL).
- **Key Servers**: Postfix, Exim, Sendmail, Maddy.

### **IMAP (Internet Message Access Protocol)**

- **Purpose**: Retrieve emails from a server (inbound, syncs across devices).
- **Ports**:
  - `143` (Unencrypted).
  - `993` (Encrypted/TLS).
- **Key Servers**: Dovecot, Cyrus.

### **POP3 (Post Office Protocol v3)**

- **Purpose**: Download emails to a single device (deletes from server by default).
- **Ports**:
  - `110` (Unencrypted).
  - `995` (Encrypted/TLS).

---

## **2. DNS Records for Email**

### **MX (Mail Exchange)**

- **Purpose**: Directs emails to your mail server.
- **Example**:
  ```dns
  @   MX  10  mail.yourdomain.com.
  ```
  - Priority `10` (lower = higher priority).

### **A/AAAA Record**

- **Purpose**: Points `mail.yourdomain.com` to your server’s IP.
- **Example**:
  ```dns
  mail   A   192.0.2.1
  ```

### **PTR (Reverse DNS)**

- **Purpose**: Maps IP → domain (critical for avoiding spam filters).
- **Example**:
  ```dns
  1.2.0.192.in-addr.arpa.  PTR  mail.yourdomain.com.
  ```

---

## **3. Email Authentication**

### **SPF (Sender Policy Framework)**

- **Purpose**: Lists servers allowed to send emails for your domain.
- **Example**:
  ```dns
  @   TXT  "v=spf1 mx a:mail.yourdomain.com -all"
  ```
  - `mx`: Allow MX hosts.
  - `-all`: Reject all others.

### **DKIM (DomainKeys Identified Mail)**

- **Purpose**: Adds a digital signature to verify email integrity.
- **Steps**:
  1. Generate a DKIM key pair:
     ```bash
     opendkim-genkey -s default -d yourdomain.com
     ```
  2. Publish the public key in DNS:
     ```dns
     default._domainkey   TXT  "v=DKIM1; k=rsa; p=MIGfMA0GCSq..."
     ```

### **DMARC (Domain-based Message Authentication)**

- **Purpose**: Tells receivers how to handle SPF/DKIM failures.
- **Example**:
  ```dns
  _dmarc   TXT  "v=DMARC1; p=quarantine; rua=mailto:dmarc@yourdomain.com"
  ```
  - `p=none|quarantine|reject`.
  - `rua`: Aggregate reports email.

---

## **4. Security & Anti-Spam**

### **TLS (Transport Layer Security)**

- **Purpose**: Encrypts email in transit.
- **Ports**: `587` (STARTTLS), `465` (SMTPS).

### **Greylisting**

- **Purpose**: Temporarily rejects emails from unknown senders (reduces spam).
- **Tools**: Postgrey, Rspamd.

### **Spam Filtering**

- **Tools**:
  - **Rspamd** (Modern, lightweight).
  - **SpamAssassin** (Rule-based).
  - **ClamAV** (Virus scanning).

---

## **5. Email Headers & Metadata**

### **Common Headers**

- **From/To/CC**: Sender/recipient addresses.
- **Subject**: Email topic.
- **Message-ID**: Unique email identifier.
- **Received**: Shows email path (useful for debugging).

### **MIME (Multipurpose Internet Mail Extensions)**

- **Purpose**: Supports attachments (e.g., images, PDFs).
- **Headers**:
  - `Content-Type`: `text/html` or `multipart/mixed`.
  - `Content-Disposition`: `attachment; filename="doc.pdf"`.

---

## **6. Email Delivery Terms**

### **Bounce**

- **Definition**: Undeliverable email (e.g., invalid address).
- **Types**:
  - **Hard Bounce**: Permanent (e.g., nonexistent domain).
  - **Soft Bounce**: Temporary (e.g., full mailbox).

### **Backscatter**

- **Definition**: Spam sent to forged "From" addresses (your domain).
- **Fix**: Enable SPF/DKIM/DMARC.

### **MTA (Mail Transfer Agent)**

- **Examples**: Postfix, Exim, Sendmail.

### **MUA (Mail User Agent)**

- **Examples**: Outlook, Thunderbird, Apple Mail.

---

## **7. Self-Hosted Email Servers**

| **Tool**              | **Best For**         | **Language** | **Notes**                   |
| --------------------- | -------------------- | ------------ | --------------------------- |
| **Maddy**             | Minimalist SMTP      | Go           | Fast, no IMAP.              |
| **docker-mailserver** | Full mail server     | Bash         | Postfix + Dovecot + Rspamd. |
| **Postal**            | SendGrid alternative | Ruby         | API + tracking.             |
| **Haraka**            | Programmatic SMTP    | Node.js      | Plugin-based.               |

---

## **8. Troubleshooting Commands**

### **Test SMTP**

```bash
telnet mail.yourdomain.com 25  # Check if SMTP is listening
openssl s_client -connect mail.yourdomain.com:587 -starttls smtp  # Test TLS
```

### **Check DNS Records**

```bash
dig MX yourdomain.com
dig TXT yourdomain.com
nslookup -type=PTR 192.0.2.1
```

### **Analyze Headers**

```bash
curl -v smtp://mail.yourdomain.com  # Raw SMTP debug
```

---

## **9. Best Practices**

1. **Always use TLS** for SMTP/IMAP.
2. **Monitor bounces** to maintain sender reputation.
3. **Keep software updated** (e.g., Postfix, Dovecot).
4. **Avoid open relays** (configure SPF/DKIM strictly).

---

### **Summary Cheat Table**

| **Term** | **Purpose**                 | **Key Tools/Records**    |
| -------- | --------------------------- | ------------------------ |
| SMTP     | Send emails                 | Postfix, Maddy           |
| IMAP     | Sync emails across devices  | Dovecot                  |
| MX       | Route emails to your server | `mail.yourdomain.com`    |
| SPF      | Prevent spoofing            | `v=spf1 mx -all`         |
| DKIM     | Verify email integrity      | OpenDKIM                 |
| DMARC    | Enforce SPF/DKIM policies   | `p=quarantine; rua=...`  |
| Rspamd   | Filter spam                 | Machine learning + rules |

This cheat sheet covers **90% of email infrastructure concepts**.
