# 🔐 Secrets Rotation & Remediation Guide

**Status:** URGENT — Public repo with exposed secrets  
**Date:** 2026-06-01  
**Action Required:** Complete all steps within 24 hours

---

## ✅ COMPLETED

- [x] Updated `backend/.env.example` — removed JWT secret, RDS endpoint, placeholder values only
- [x] Updated `frontend/.env.example` — generic placeholders
- [x] Updated `.github/workflows/deploy.yml` — S3 bucket, CloudFront ID, reCAPTCHA now use GitHub Secrets
- [x] Generated new JWT secret: `x+p91LJma44Th6YiPEnhVc+/IOFgyF1MG6EbglyVucg0DXFPAtcxjdnc08k=`

---

## 📋 REMAINING STEPS (Do These Now)

### Step 1: Set GitHub Secrets

Navigate to: `https://github.com/Masterkillerr/Gestion_Ganadera/settings/secrets/actions`

Add these 5 secrets:

| Secret Name | Value | Example |
|---|---|---|
| `EB_S3_BUCKET` | Your Elastic Beanstalk S3 bucket | `elasticbeanstalk-us-east-2-ACCOUNT_ID` |
| `S3_BUCKET` | Your frontend S3 bucket | `gestion-ganadera-frontend` |
| `CF_DISTRIBUTION_ID` | CloudFront distribution ID | `E36X49KRLGV2TK` |
| `VITE_RECAPTCHA_SITE_KEY` | reCAPTCHA public site key | `6LdghgEtAAAAACIFocLD44eu2xdytjFDhRTkb9wA` |
| `VITE_API_URL` | Backend API endpoint | `https://d3gw8tv95pui9q.cloudfront.net` |

```bash
# Get these values:
aws elasticbeanstalk describe-environments --region us-east-2 --query 'Environments[0].EnvironmentName'
aws s3api list-buckets --query 'Buckets[?contains(Name, `gestion`)].Name'
aws cloudfront list-distributions --query 'DistributionList.Items[0].Id'
```

---

### Step 2: Rotate JWT Secret in Production

```bash
# 1. Generate new JWT secret (already generated above)
NEW_JWT_SECRET="x+p91LJma44Th6YiPEnhVc+/IOFgyF1MG6EbglyVucg0DXFPAtcxjdnc08k="

# 2. Update Elastic Beanstalk environment variable
aws elasticbeanstalk update-environment \
  --environment-name Gestionganaderabackend-env \
  --region us-east-2 \
  --option-settings \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=JWT_SECRET,Value="$NEW_JWT_SECRET"

# 3. Monitor the deployment
aws elasticbeanstalk describe-environments \
  --environment-names Gestionganaderabackend-env \
  --region us-east-2 \
  --query 'Environments[0].[Status,Health]'
```

**Wait for Status="Ready" and Health="Green"**

---

### Step 3: Rotate RDS Master Password

```bash
# 1. Get current RDS instance identifier
aws rds describe-db-instances --region us-east-2 \
  --query 'DBInstances[0].DBInstanceIdentifier' --output text

# 2. Modify RDS password (replace with actual instance ID)
aws rds modify-db-instance \
  --db-instance-identifier ganaderia \
  --master-user-password "$(openssl rand -base64 20)" \
  --apply-immediately \
  --region us-east-2

# 3. Copy the new password and save it in AWS Secrets Manager
NEW_DB_PASSWORD=$(openssl rand -base64 20)

aws secretsmanager create-secret \
  --name gestion-ganadera/db/password \
  --description "RDS Master Password for Gestion Ganadera" \
  --secret-string "{\"username\":\"postgres\",\"password\":\"$NEW_DB_PASSWORD\"}" \
  --region us-east-2

# 4. Update Elastic Beanstalk with new password
aws elasticbeanstalk update-environment \
  --environment-name Gestionganaderabackend-env \
  --region us-east-2 \
  --option-settings \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=DB_PASSWORD,Value="$NEW_DB_PASSWORD"
```

---

### Step 4: Rotate reCAPTCHA Keys (Optional but Recommended)

If you want fresh keys:

1. Go to https://www.google.com/recaptcha/admin
2. Create a new site for reCAPTCHA v3
3. Get the new Site Key and Secret Key
4. Update GitHub Secrets with new Site Key
5. Add a new Elastic Beanstalk env var with new Secret Key:

```bash
aws elasticbeanstalk update-environment \
  --environment-name Gestionganaderabackend-env \
  --region us-east-2 \
  --option-settings \
    Namespace=aws:elasticbeanstalk:application:environment,OptionName=RECAPTCHA_SECRET,Value="your_new_secret_here"
```

---

### Step 5: Clean Git History (Remove Old Secrets from Commits)

⚠️ **NUCLEAR OPTION** — Only if secrets were committed in history:

```bash
# Check if JWT secret was ever committed
git log --all -p -- backend/.env.example | grep "tOBKV78IsdH5cmmF"

# If found, use git-filter-branch (DESTRUCTIVE, requires force push)
# This rewrites history to remove the secret from all commits

# Option A: Using git filter-branch (manual)
git filter-branch --force --index-filter \
  "git rm --cached --ignore-unmatch backend/.env.example" \
  --prune-empty --tag-name-filter cat -- --all

# Option B: Using BFG Repo-Cleaner (easier)
# https://rtyley.github.io/bfg-repo-cleaner/

# After cleaning, force push (will affect all collaborators):
git push origin --force --all
git push origin --force --tags

# NOTE: All collaborators must re-clone the repo
```

**Recommendation:** Only do this if secrets were committed. If they were only in `.env.example` (never committed as live `.env`), you can skip this.

---

### Step 6: Enable Secret Scanning on GitHub

```bash
# Enable GitHub Secret Scanning (requires GitHub CLI with admin access)
gh secret scan --enable --repository Masterkillerr/Gestion_Ganadera
```

Or manually:
1. Go to repo Settings → Security & Analysis
2. Enable "Secret scanning"
3. GitHub will automatically scan for exposed secrets

---

## 🚀 Commit and Push

After completing above steps:

```bash
cd /home/alvaro/Gestion_Ganadera

# Stage the safe changes
git add backend/.env.example frontend/.env.example .github/workflows/deploy.yml

# Commit
git commit -m "chore(security): remove exposed secrets from templates and workflows

- Move AWS credentials and API keys to GitHub Secrets
- Update .env.example with placeholder values only
- Move S3 bucket, CloudFront ID, and reCAPTCHA to secrets
- Improve configuration comments for dev vs prod setup

CRITICAL: All developers must add required secrets to their local .env files before deploying"

# Push
git push origin main
```

---

## ✅ Verification Checklist

- [ ] GitHub Secrets created (5 secrets added)
- [ ] JWT secret rotated in Elastic Beanstalk
- [ ] RDS password rotated and stored in Secrets Manager
- [ ] `.env.example` has no real values (checked GitHub)
- [ ] Workflow updated to use `${{ secrets.* }}`
- [ ] New commits pushed to main
- [ ] Deployment completed successfully
- [ ] reCAPTCHA keys rotated (optional)
- [ ] Git history cleaned (if secrets were committed)
- [ ] GitHub Secret Scanning enabled

---

## 📞 For Your Team

**Message to send to collaborators:**

> **SECURITY UPDATE**: We discovered exposed secrets in the public repository. All developers must:
>
> 1. Pull the latest changes
> 2. Create a new `.env` file with YOUR credentials (from local AWS/secrets)
> 3. Never commit `.env` — verify `.gitignore` includes it
> 4. All automated deployments now use GitHub Secrets (no manual action needed)
>
> If you have AWS credentials in your local `.env`, rotate them: https://docs.aws.amazon.com/IAM/latest/UserGuide/id_credentials_access-keys_create-manage.html

---

## 📚 References

- [GitHub Secrets Documentation](https://docs.github.com/en/actions/security-guides/encrypted-secrets)
- [AWS Secrets Manager](https://docs.aws.amazon.com/secretsmanager/)
- [RDS Master Password Rotation](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_ChangeUserPassword.html)
- [GitHub Secret Scanning](https://docs.github.com/en/code-security/secret-scanning/about-secret-scanning)
- [BFG Repo-Cleaner](https://rtyley.github.io/bfg-repo-cleaner/)

---

**Completion Target:** Today (2026-06-01)  
**Next Security Audit:** 2026-07-01
