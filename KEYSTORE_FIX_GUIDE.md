# 🔐 راهنمای حل مشکل Keystore در Codemagic

## 🚨 مشکل شناسایی شده:
```
No suitable keystores found matching reference "keystore_reference". Available options are: .
```

## 🔍 علت مشکل:
- در Codemagic web interface، تنظیمات signing هنوز فعال است
- Reference به keystore_reference وجود دارد اما keystore آپلود نشده

## ✅ راه‌حل‌های موثر:

### راه‌حل 1: استفاده از Workflows جدید (توصیه اول)

#### 🎯 Workflows جدید بدون مشکل signing:

1. **`clean-build`** (بهترین گزینه)
   - کاملاً تمیز و بدون signing
   - مدت زمان: 40 دقیقه
   - خروجی: `persian-ai-clean.apk`

2. **`no-signing-build`** (گزینه دوم)
   - طراحی شده برای حل مشکل keystore
   - مدت زمان: 45 دقیقه
   - خروجی: `persian-ai-no-signing.apk`

3. **`simple-apk`** (گزینه سوم)
   - اصلاح شده و تست شده
   - مدت زمان: 30 دقیقه

### راه‌حل 2: غیرفعال کردن Signing در Web Interface

#### مراحل:
1. به Codemagic بروید: https://codemagic.io
2. پروژه `PersianAIAssistantAndroid` را انتخاب کنید
3. به تب "Configuration" بروید
4. بخش "Code signing" را پیدا کنید
5. تمام تنظیمات signing را غیرفعال کنید
6. "Save" کلیک کنید

### راه‌حل 3: حذف Reference از Workflow

اگر همچنان مشکل دارید:

#### در Codemagic Web Interface:
1. Workflow مشکل‌دار را انتخاب کنید
2. به بخش "Environment" بروید
3. "Code signing" را پیدا کنید
4. تمام references را حذف کنید
5. فقط "Build unsigned APK" را فعال کنید

## 🎯 توصیه فوری:

### استفاده از `clean-build`:
1. به Codemagic بروید
2. Workflow `clean-build` را انتخاب کنید
3. Branch `main` را انتخاب کنید
4. "Start new build" کلیک کنید

### اگر `clean-build` موجود نیست:
1. از `simple-apk` استفاده کنید
2. یا GitHub Actions را امتحان کنید

## 🚨 نکات مهم:

### مشکل Cache:
- Codemagic ممکن است تنظیمات قدیمی را cache کرده باشد
- Workflow جدید انتخاب کنید
- یا چند دقیقه صبر کنید تا cache پاک شود

### مشکل Web Interface:
- گاهی web interface با فایل YAML همگام نیست
- از workflows جدید استفاده کنید
- یا تنظیمات را دستی تغییر دهید

## 📋 خلاصه اقدامات:

### فوری (5 دقیقه):
1. Workflow `clean-build` را امتحان کنید
2. اگر نبود، `simple-apk` را امتحان کنید

### میان‌مدت (10 دقیقه):
1. تنظیمات signing را در web interface غیرفعال کنید
2. Workflow مشکل‌دار را اصلاح کنید

### طولانی‌مدت (20 دقیقه):
1. GitHub Actions را به عنوان جایگزین امتحان کنید
2. یا پروژه را دوباره به Codemagic اضافه کنید

## 🎉 نتیجه انتظاری:

پس از اجرای موفق:
- ✅ فایل APK در Artifacts
- ✅ ایمیل تأیید به ghadir.baraty@gmail.com
- ✅ بدون خطای keystore

---

**الان می‌توانید `clean-build` را در Codemagic امتحان کنید! 🚀**
