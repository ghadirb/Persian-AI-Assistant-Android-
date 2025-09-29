# 🚨 حل مشکل اخطار در Codemagic Web Interface

## 🔍 مشکل شناسایی شده:
همه workflowها از طریق API کار می‌کنن، اما در web interface اخطار می‌گیرید.

## ✅ راه‌حل: پاک کردن Cache و تنظیمات قدیمی

### مرحله 1: ورود به Codemagic
1. به https://codemagic.io بروید
2. با GitHub وارد شوید
3. پروژه `PersianAIAssistantAndroid` را پیدا کنید

### مرحله 2: پاک کردن Cache
1. **Force refresh** صفحه را انجام دهید:
   - `Ctrl + F5` (Windows/Linux)
   - `Cmd + Shift + R` (Mac)

2. **Hard refresh** انجام دهید:
   - Developer Tools باز کنید (`F12`)
   - تب Network را انتخاب کنید
   - چک‌باکس "Disable cache" را فعال کنید
   - صفحه را refresh کنید

### مرحله 3: بررسی تنظیمات Signing
1. به تب **Configuration** بروید
2. بخش **Code signing** را پیدا کنید
3. مطمئن شوید که **هیچ** keystore reference تنظیم نشده
4. اگر چیزی تنظیم شده، آن را حذف کنید
5. **Save** کلیک کنید

### مرحله 4: بررسی Workflowهای موجود
1. به تب **Workflows** بروید
2. workflowهای زیر باید موجود باشند:
   - ✅ `clean-build` (جدید)
   - ✅ `simple-apk` (اصلاح شده)
   - ✅ `no-signing-build` (جدید)
   - ✅ `android-unsigned` (اصلاح شده)

### مرحله 5: تست Workflowها
**اول `clean-build` را امتحان کنید:**
1. Workflow `clean-build` را انتخاب کنید
2. Branch `main` را انتخاب کنید
3. "Start new build" کلیک کنید

**اگر `clean-build` موجود نبود:**
1. `simple-apk` را امتحان کنید
2. بعد `no-signing-build` را امتحان کنید

## 🔧 اگر همچنان اخطار می‌گیرید:

### گزینه 1: حذف و اضافه مجدد پروژه
1. پروژه را از Codemagic حذف کنید
2. چند دقیقه صبر کنید
3. دوباره repository را اضافه کنید:
   - Repository: `https://github.com/ghadirb/PersianAIAssistantAndroid.git`
   - Branch: `main`

### گزینه 2: استفاده از API مستقیم
اگر web interface مشکل داشت، از API استفاده کنید:

```bash
# Build with clean-build workflow
curl -X POST "https://api.codemagic.io/builds" \
  -H "x-auth-token: sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ" \
  -H "Content-Type: application/json" \
  -d '{
    "appId": "68d2bb0d849df2693dd0a310",
    "workflowId": "clean-build",
    "branch": "main"
  }'
```

### گزینه 3: استفاده از GitHub Actions
اگر Codemagic مشکل داشت، از GitHub Actions استفاده کنید:
1. به https://github.com/ghadirb/PersianAIAssistantAndroid بروید
2. Actions → "Complete Build" → "Run workflow"
3. "debug" را انتخاب کنید

## 📊 وضعیت فعلی Workflowها:

| Workflow | وضعیت API | وضعیت Web | توصیه |
|----------|------------|------------|--------|
| `clean-build` | ✅ کار می‌کند | ❓ نیاز به sync | اول امتحان کنید |
| `simple-apk` | ✅ کار می‌کند | ✅ کار می‌کند | گزینه مطمئن |
| `no-signing-build` | ✅ کار می‌کند | ❓ نیاز به sync | گزینه جایگزین |
| `android-unsigned` | ✅ کار می‌کند | ✅ کار می‌کند | گزینه سوم |

## 🎯 خلاصه:

1. **اول**: صفحه Codemagic را hard refresh کنید
2. **دوم**: تنظیمات signing را چک کنید
3. **سوم**: `clean-build` را امتحان کنید
4. **چهارم**: اگر کار نکرد، `simple-apk` را امتحان کنید

**اگر همه کار نکردند، از GitHub Actions استفاده کنید! 🚀**
