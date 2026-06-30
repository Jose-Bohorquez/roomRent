# SMTP Configuration Guide for RoomRent Development

## ⚠️ SECURITY FIRST

This guide explains how to safely configure Gmail SMTP for RoomRent development **without exposing credentials in git or logs**.

### Credential Safety Rules

1. **NEVER** commit `.env` files to git
2. **NEVER** hardcode passwords in code or YAML
3. **ALWAYS** use App Passwords, not account passwords
4. **ALWAYS** load credentials from environment variables
5. **ROTATE IMMEDIATELY** if credentials are ever exposed

---

## Setup Steps

### Step 1: Rotate Your Gmail Password (CRITICAL)

If you shared your Gmail password anywhere, you MUST change it immediately:

```
1. Go to: https://myaccount.google.com/security/password
2. Enter your current password
3. Create a strong new password (16+ characters, mixed case, numbers, special chars)
4. Save and confirm
```

### Step 2: Generate an App Password

Gmail App Passwords are safer than account passwords because they're single-use.

```
1. Go to: https://myaccount.google.com/apppasswords
2. Select: Mail > Windows Computer (or your OS)
3. Google will generate a 16-character password
4. Copy it immediately (you'll only see it once)
5. Save it securely (password manager recommended)
```

### Step 3: Create .env File (Local, Not Committed)

```bash
# From RoomRent root directory:
cp .env.example .env

# Edit .env with your credentials:
nano .env
# or
vim .env
# or
code .env
```

**Fill in .env with:**
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=xxxx-xxxx-xxxx-xxxx  # 16-char App Password from Step 2
MAIL_FROM=noreply-room@gmail.com
```

### Step 4: Verify .env is Protected

```bash
# Check .env is in .gitignore
grep "^\.env$" .gitignore
# Output: .env (or similar)

# Verify .env is NOT in git
git status | grep ".env" || echo "✓ .env not in git"

# Make sure .env has restricted permissions
chmod 600 .env
```

### Step 5: Start the Application

```bash
# Make run-dev.sh executable
chmod +x run-dev.sh

# Run the application
./run-dev.sh
# OR manually:
source .env
java -jar target/room-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

---

## Testing SMTP

### Test 1: Activation Email

```bash
# Register a new user (triggers activation email)
curl -X POST http://localhost:8080/api/admin/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "login":"testuser",
    "email":"your-test-email@gmail.com",
    "password":"Test123!",
    "authorities":["ROLE_USER"]
  }'

# Check logs for:
# INFO MailService: Sending activation email to 'your-test-email@gmail.com'
# INFO MailService: Sent email to User 'your-test-email@gmail.com'
```

### Test 2: Password Reset Email

```bash
curl -X POST http://localhost:8080/api/account/reset_password/init \
  -H "Content-Type: application/json" \
  -d '{"mail":"testuser@example.com"}'

# Check logs for:
# INFO MailService: Sending password reset email to 'testuser@example.com'
# INFO MailService: Sent email to User 'testuser@example.com'
```

### Test 3: Check Inbox

- Check your Gmail inbox for incoming emails
- Verify links are correct (activation link, reset link)
- Test the links work

---

## Troubleshooting

### Email Not Sending

```bash
# Check logs
tail -100f /tmp/roomrent.log | grep -i "mail\|email\|smtp"

# Common issues:
# 1. App Password incorrect → regenerate in Step 2
# 2. Account has 2FA enabled → required for App Password
# 3. .env not loaded → verify with `echo $MAIL_USERNAME`
# 4. SMTP connection fails → check port 587 is open
```

### Variables Not Loaded

```bash
# Verify .env is loaded:
source .env
echo "Mail username: $MAIL_USERNAME"
echo "Mail password length: ${#MAIL_PASSWORD}"  # Should show length, not expose password

# If empty, .env file has syntax errors
cat .env  # Review for issues
```

### Credentials Accidentally Exposed

```bash
# If you ever expose your App Password:
# 1. Go to https://myaccount.google.com/apppasswords
# 2. Find and delete the exposed password
# 3. Generate a new one
# 4. Update .env
# 5. Restart app

# Check git history (should be clean):
git log --all -p | grep "MAIL_PASSWORD" || echo "✓ Not in history"
```

---

## Files in This Setup

| File | Purpose | Git Status |
|---|---|---|
| `.env.example` | Template with instructions | ✓ Committed |
| `.env` | Your actual credentials | ✗ In .gitignore |
| `run-dev.sh` | Startup script | ✓ Committed |
| `application-dev.yml` | Config using env vars | ✓ Committed |

---

## Architecture

```
┌─────────────────────────────────────┐
│  Developer Machine                  │
│                                     │
│  .env (local, not in git)          │
│   └─ MAIL_USERNAME                 │
│   └─ MAIL_PASSWORD (App Password)  │
│                                     │
│  run-dev.sh (executable)           │
│   └─ Loads .env                    │
│   └─ Exports vars to environment   │
│   └─ Launches Spring Boot          │
│                                     │
│  Spring Boot (Dev Profile)         │
│   └─ Reads ${MAIL_USERNAME} etc    │
│   └─ Configures JavaMailSender     │
│                                     │
│  Gmail SMTP                        │
│   └─ Authenticates with App Password
│   └─ Sends emails                 │
└─────────────────────────────────────┘
```

---

## Security Checklist

- [ ] Gmail account password rotated (if ever exposed)
- [ ] New App Password generated
- [ ] .env file created locally with App Password
- [ ] .env is in .gitignore and not tracked by git
- [ ] .env has restrictive permissions (chmod 600)
- [ ] No credentials in any source code
- [ ] No credentials in git history
- [ ] run-dev.sh is executable
- [ ] SMTP works (activation email received)

---

## Next Steps

1. Follow Setup Steps 1-5 above
2. Run SMTP tests
3. Verify emails arrive
4. Proceed with auditoría testing
5. Implement plantilla improvements
6. Update favicon

