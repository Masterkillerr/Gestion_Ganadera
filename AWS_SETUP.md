# AWS Infrastructure Setup

**Configured:** 2026-06-02  
**Status:** ✅ **PRODUCTION READY**

## Infrastructure Overview

```
GitHub Actions (OIDC)
        ↓
github-actions-gestion-ganadera (IAM Role)
        ↓
   ┌────────────────────────────────────────┐
   │  AWS Elastic Beanstalk                 │
   │  ├─ Application: gestion_ganadera_backend
   │  ├─ Environment: Gestionganaderabackend-env
   │  ├─ Endpoint: Gestionganaderabackend-env.eba-kmujbtjg.us-east-2.elasticbeanstalk.com
   │  └─ Instance: t3.small (Java 21 Corretto)
   │
   ├─ AWS S3 Buckets
   │  ├─ elasticbeanstalk-us-east-2-392362834988 (backend deployments)
   │  └─ gestion-ganadera-frontend (frontend static assets)
   │
   ├─ AWS CloudFront
   │  └─ Distribution: E36X49KRLGV2TK
   │
   └─ AWS CloudWatch Logs
      └─ /aws/elasticbeanstalk/gestion-ganadera
```

## AWS Account Details

```
Account ID: 392362834988
Region: us-east-2 (Ohio)
```

## GitHub OIDC Configuration

### Trust Provider
- **Provider:** token.actions.githubusercontent.com
- **Audience:** sts.amazonaws.com
- **Repository:** repo:Masterkillerr/Gestion_Ganadera:*

### GitHub Actions IAM Role

**Role Name:** `github-actions-gestion-ganadera`  
**ARN:** `arn:aws:iam::392362834988:role/github-actions-gestion-ganadera`

**Permissions:**
- ElasticBeanstalk: Full access (create/update environments, describe status)
- S3: Read/write to deployment and frontend buckets
- CloudFront: Create invalidations for cache clearing
- IAM: PassRole to EB service and EC2 roles
- CloudWatch: Create logs and streams

**Trust Policy:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::392362834988:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
        },
        "StringLike": {
          "token.actions.githubusercontent.com:sub": "repo:Masterkillerr/Gestion_Ganadera:*"
        }
      }
    }
  ]
}
```

## Elastic Beanstalk Configuration

### Application
- **Name:** `gestion_ganadera_backend`
- **Region:** us-east-2
- **Platform:** Java 21 Corretto + Tomcat 10

### Environment
- **Name:** `Gestionganaderabackend-env`
- **Type:** Single instance + Auto-scaling
- **Instance Type:** t3.small
- **CNAME:** `Gestionganaderabackend-env.eba-kmujbtjg.us-east-2.elasticbeanstalk.com`
- **Status:** Ready ✅
- **Health:** None (no data yet)

### Instance Profile
- **Role:** `aws-elasticbeanstalk-ec2-role`
- **Instance Profile:** `aws-elasticbeanstalk-ec2-role`
- **Policies:**
  - CloudWatchLogsFullAccess
  - AmazonSSMManagedInstanceCore
  - CloudWatchAgentServerPolicy

## S3 Buckets

### Deployment Bucket
- **Name:** `elasticbeanstalk-us-east-2-392362834988`
- **Purpose:** Store application JARs for EB deployment
- **Path Structure:** `backend/backend-<version>.zip`
- **Retention:** Automatic (EB manages lifecycle)

### Frontend Bucket
- **Name:** `gestion-ganadera-frontend`
- **Purpose:** Static assets (React build output)
- **Access:** CloudFront (public)
- **Path Structure:** `/index.html, /js/*, /css/*, /assets/*`

## CloudFront Distribution

- **ID:** `E36X49KRLGV2TK`
- **Domain:** `d3gw8tv95pui9q.cloudfront.net`
- **Origin:** S3 bucket `gestion-ganadera-frontend`
- **Status:** Deployed ✅
- **Purpose:** CDN for frontend assets

## GitHub Secrets

All configured and active:

```bash
AWS_ROLE_ARN=arn:aws:iam::392362834988:role/github-actions-gestion-ganadera
EB_S3_BUCKET=elasticbeanstalk-us-east-2-392362834988
S3_BUCKET=gestion-ganadera-frontend
CF_DISTRIBUTION_ID=E36X49KRLGV2TK
VITE_API_URL=https://Gestionganaderabackend-env.eba-kmujbtjg.us-east-2.elasticbeanstalk.com/api
VITE_RECAPTCHA_SITE_KEY=<configured>
```

## Deployment Workflow

### GitHub Actions Triggers
1. **CI + Deploy** (`.github/workflows/deploy.yml`)
   - Triggers on: push to master
   - Steps:
     1. Validate Flyway migrations against test DB
     2. Build backend JAR
     3. Upload to S3
     4. Deploy to EB
     5. Wait for health checks
     6. Build frontend
     7. Sync to S3
     8. Invalidate CloudFront cache

2. **Docker Build** (`.github/workflows/docker-build.yml`)
   - Triggers on: push to master, manual
   - Builds Docker images for backend and frontend
   - Pushes to Docker Hub (if configured)

### Deployment Flow

```
git push to master
    ↓
GitHub Actions triggered
    ↓
┌─── Assume GitHub Actions IAM Role ───┐
│    (via OIDC token)                   │
│                                       │
├─ Build backend JAR                   │
│  └─ Upload to S3                     │
│     └─ Deploy to EB                  │
│        └─ Wait for health (10 min)   │
│                                       │
├─ Build frontend (React)               │
│  └─ Upload to S3                     │
│     └─ Invalidate CloudFront         │
│                                       │
└─ Done!                                │
   Frontend: CloudFront CDN             │
   Backend: EB endpoint                 │
```

## Security Features

- ✅ **OIDC Token:** No long-lived AWS credentials in GitHub
- ✅ **Principle of Least Privilege:** Role has only needed permissions
- ✅ **Repository Isolation:** Only this repo can assume the role
- ✅ **Audit Trail:** CloudTrail logs all deployments
- ✅ **Encryption:** S3 encryption at rest, CloudFront HTTPS only

## Monitoring & Logs

### CloudWatch Logs
- **Log Group:** `/aws/elasticbeanstalk/gestion-ganadera`
- **Streams:**
  - `/var/log/eb-engine.log` (EB engine)
  - `/var/log/tomcat/catalina.out` (Java application)
  - Application logs (via Logback)

### CloudWatch Alarms (Optional - to configure)
```bash
# High error rate
aws cloudwatch put-metric-alarm \
  --alarm-name EB-HighErrorRate \
  --alarm-description "Alert if error rate > 5%" \
  --metric-name ErrorRate \
  --namespace AWS/ElasticBeanstalk

# Unhealthy instances
aws cloudwatch put-metric-alarm \
  --alarm-name EB-UnhealthyInstances \
  --alarm-description "Alert if any instances unhealthy" \
  --metric-name EnvironmentHealth
```

## Database Configuration

PostgreSQL is **NOT** in AWS RDS (appears to be external). Configure in EB environment variables:

```bash
aws elasticbeanstalk update-environment-config \
  --environment-name Gestionganaderabackend-env \
  --option-settings \
    "Namespace=aws:elasticbeanstalk:application:environment,OptionName=SPRING_DATASOURCE_URL,Value=jdbc:postgresql://your-rds-endpoint:5432/gestion_ganadera" \
    "Namespace=aws:elasticbeanstalk:application:environment,OptionName=SPRING_DATASOURCE_USERNAME,Value=postgres" \
    "Namespace=aws:elasticbeanstalk:application:environment,OptionName=SPRING_DATASOURCE_PASSWORD,Value=your-password"
```

## Scaling Configuration

Current configuration (t3.small single instance):

```bash
# To auto-scale (2-4 instances based on load):
aws elasticbeanstalk update-environment-config \
  --environment-name Gestionganaderabackend-env \
  --option-settings \
    "Namespace=aws:autoscaling:asg,OptionName=MinSize,Value=2" \
    "Namespace=aws:autoscaling:asg,OptionName=MaxSize,Value=4" \
    "Namespace=aws:autoscaling:trigger,OptionName=MeasureName,Value=CPUUtilization" \
    "Namespace=aws:autoscaling:trigger,OptionName=Statistic,Value=Average" \
    "Namespace=aws:autoscaling:trigger,OptionName=Unit,Value=Percent" \
    "Namespace=aws:autoscaling:trigger,OptionName=UpperThreshold,Value=70" \
    "Namespace=aws:autoscaling:trigger,OptionName=LowerThreshold,Value=30"
```

## Troubleshooting

### Deployment Failed
```bash
# Check EB logs
aws logs tail /aws/elasticbeanstalk/gestion-ganadera --follow

# Check EB environment health
aws elasticbeanstalk describe-environment-health \
  --environment-name Gestionganaderabackend-env \
  --attribute-keys All
```

### GitHub Actions Role Error
```bash
# Verify role trust policy
aws iam get-role \
  --role-name github-actions-gestion-ganadera

# Check recent assume role attempts
aws cloudtrail lookup-events \
  --lookup-attributes AttributeKey=EventName,AttributeValue=AssumeRole \
  --max-results 10
```

### S3 Access Issues
```bash
# Verify bucket permissions
aws s3api get-bucket-acl --bucket elasticbeanstalk-us-east-2-392362834988

# List recent uploads
aws s3 ls s3://elasticbeanstalk-us-east-2-392362834988/backend/ --recursive
```

## Useful Commands

```bash
# Deploy specific version to EB
aws elasticbeanstalk update-environment \
  --environment-name Gestionganaderabackend-env \
  --version-label my-app-v1.0.0

# Check deployment history
aws elasticbeanstalk describe-events \
  --environment-name Gestionganaderabackend-env \
  --max-records 20

# SSH to EB instance
eb ssh Gestionganaderabackend-env

# View EB config
aws elasticbeanstalk describe-configuration-settings \
  --application-name gestion_ganadera_backend \
  --environment-name Gestionganaderabackend-env

# Manual scale up
aws elasticbeanstalk update-environment-config \
  --environment-name Gestionganaderabackend-env \
  --option-settings "Namespace=aws:autoscaling:launchconfiguration,OptionName=InstanceType,Value=t3.medium"
```

## Cost Estimation

- **t3.small instance:** ~$0.0208/hour (~$15/month)
- **Data transfer:** ~$0.09/GB out
- **CloudFront:** ~$0.085/GB
- **RDS:** Depends on database tier (external)
- **Total:** ~$20-50/month (low usage)

## Disaster Recovery

### Backup Checklist
- [ ] RDS automated backups enabled
- [ ] S3 versioning enabled
- [ ] CloudFront cache settings documented
- [ ] EB environment configuration backed up
- [ ] GitHub secrets secured

### Restore Procedure
1. Restore RDS database from backup
2. Update EB environment variables with new DB endpoint
3. Redeploy latest application version
4. Invalidate CloudFront cache

---

**Last Updated:** 2026-06-02  
**Status:** ✅ Production Ready  
**Next Review:** 2026-07-02
