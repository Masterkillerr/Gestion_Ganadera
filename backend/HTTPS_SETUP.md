# HTTPS & Security Setup Guide

## Overview
This guide explains how to set up HTTPS/SSL and enable all security features for production deployment.

---

## 1. HTTPS Configuration (AWS Elastic Beanstalk)

### Option A: Elastic Beanstalk with Application Load Balancer (Recommended)

#### Setup Steps:
1. **In AWS Elastic Beanstalk Console:**
   - Navigate to your environment
   - Click "Configuration" → "Load Balancer"
   - Add HTTPS listener (port 443)
   - Select or create SSL certificate (from AWS Certificate Manager)
   - Set HTTP (port 80) to redirect to HTTPS

2. **In Application Code:**
   - The application already enforces HTTPS via:
     ```java
     http.requiresChannel(channel -> channel.anyRequest().requiresSecure())
     ```
   - All requests are redirected to HTTPS

#### Benefits:
- SSL/TLS termination at load balancer (frees app resources)
- Automatic certificate renewal
- No changes needed to application code

---

## 2. Security Headers Configuration

The application now includes these security headers:

### **HSTS (HTTP Strict-Transport-Security)**
```
Max-Age: 31536000 (1 year)
Include-Subdomains: true
```
Forces all connections to HTTPS for 1 year. Once set, browsers remember this.

### **X-XSS-Protection**
```
1; mode=block
```
Protects against XSS attacks in older browsers.

### **Content-Security-Policy (CSP)**
```
default-src 'self'
```
Restricts content loading to same-origin only. Update if you need external resources:
- Images from CDN: `img-src 'self' https://cdn.example.com`
- Fonts: `font-src 'self' https://fonts.googleapis.com`

---

## 3. Rate Limiting Configuration

### **Auth Endpoints Protected:**
- `POST /auth/login` - 5 requests per minute per IP
- `POST /auth/register` - 5 requests per minute per IP
- `POST /auth/forgot-password` - 5 requests per minute per IP
- `POST /auth/reset-password` - 5 requests per minute per IP

### **Behavior:**
- Requests exceeding limit: Return **429 Too Many Requests**
- Automatically resets after 1 minute
- IP detection: Reads `X-Forwarded-For` header for load balancer scenarios

### **Configuration (if needed):**
Modify `RateLimitingFilter.createNewBucket()`:
```java
Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
```

---

## 4. CORS Configuration

Currently allowed origins (in `application.yml`):
```yaml
app:
  cors:
    allowed-origins: http://localhost:3000, http://localhost:5173, https://d3gw8tv95pui9q.cloudfront.net
```

### **For Production:**
Update to only allow your frontend domain:
```yaml
allowed-origins: https://yourdomain.com
```

### **Allowed Methods:** GET, POST, PUT, PATCH, DELETE, OPTIONS
### **Allowed Headers:** Authorization, Content-Type, X-Requested-With, Accept

---

## 5. Environment Variables for Production

### **Required:**
```bash
# Database
DB_URL=jdbc:postgresql://your-rds-endpoint:5432/gestion_ganadera
DB_USER=postgres
DB_PASSWORD=your-secure-password

# JWT
JWT_SECRET=your-very-long-random-secret-key-at-least-32-chars

# reCAPTCHA
RECAPTCHA_SECRET=your-recaptcha-secret-key
```

### **Optional (for monitoring):**
```bash
# Application Insights / CloudWatch
AWS_CLOUDWATCH_LOG_GROUP=/aws/elasticbeanstalk/gestion-ganadera-backend
```

---

## 6. Certificate Management (AWS Certificate Manager)

### **Get a Certificate:**
1. Open AWS Certificate Manager
2. Request a new public certificate
3. Enter domain name(s):
   - Example: `api.yourdomain.com`
   - Also add wildcard: `*.yourdomain.com` (optional)
4. Choose DNS validation
5. Add CNAME records to your DNS provider

### **Renewal:**
AWS automatically renews certificates 30 days before expiration.

---

## 7. Testing HTTPS Setup

### **Local Testing (before deployment):**
```bash
# Test with SSL protocol
curl -I https://your-domain.com/api/health

# Check HTTPS headers
curl -I https://your-domain.com/api/health
# Should see:
# Strict-Transport-Security: max-age=31536000...
# X-XSS-Protection: 1; mode=block
# Content-Security-Policy: default-src 'self'
```

### **Check Security Score:**
https://www.ssl-labs.com/ssltest/

---

## 8. Production Deployment Checklist

- [ ] SSL certificate installed on load balancer
- [ ] HTTP redirects to HTTPS
- [ ] HSTS header enabled (1 year max-age)
- [ ] CSP header configured for your resources
- [ ] Rate limiting active on auth endpoints
- [ ] CORS origins restricted to frontend domain
- [ ] Environment variables set (JWT_SECRET, DB credentials, reCAPTCHA)
- [ ] Elastic Beanstalk health check passing
- [ ] CloudFront distribution (if using S3 frontend)
- [ ] Database backups configured
- [ ] Application logs monitored

---

## 9. Troubleshooting

### **Mixed Content Warning (Chrome error)**
Problem: HTTPS page loading HTTP resources  
Solution: Update CSP or ensure all external resources use HTTPS

### **SSL Handshake Failure**
Problem: Certificate not matching domain  
Solution: Verify certificate in ACM matches your domain

### **Rate Limiting Too Strict**
Problem: Users getting 429 errors  
Solution: Adjust `Bandwidth.classic()` parameters in RateLimitingFilter

### **CORS Origin Blocked**
Problem: Frontend can't reach API  
Solution: Add frontend domain to `allowed-origins` in application.yml

---

## 10. Monitoring & Alerts

Recommended CloudWatch metrics to monitor:
- **HTTPSRequests**: Percentage of requests using HTTPS (should be 100%)
- **FailedSSLHandshakes**: Should be near 0
- **RateLimitedRequests**: Monitor spike patterns
- **ApplicationErrors**: 4xx and 5xx response counts

Set up alarms for:
- HTTPS < 99%
- SSL handshake failures > 10 in 5 minutes
- Rate limit hits > 100 per minute

---

## References

- [AWS Certificate Manager Docs](https://docs.aws.amazon.com/acm/)
- [OWASP Security Headers](https://cheatsheetseries.owasp.org/cheatsheets/Security_Headers_Cheat_Sheet.html)
- [MDN HTTPS Guide](https://developer.mozilla.org/en-US/docs/Glossary/HTTPS)
- [Bucket4j Rate Limiting](https://github.com/vladimir-bukhtoyarov/bucket4j)

---

**Last Updated:** 2026-06-01  
**Status:** Production Ready
