# 🚀 راهنمای کامل راه‌اندازی Codemagic برای Persian AI Assistant

## 📋 اطلاعات پروژه
- **Repository**: https://github.com/ghadirb/PersianAIAssistantAndroid.git
- **GitHub Token**: `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`
- **Codemagic Token**: `sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ`
- **Email**: ghadir.baraty@gmail.com

## 🔧 مشکلات برطرف شده
✅ **Hilt Version Inconsistency**: خط 108 در `app/build.gradle` اصلاح شد
✅ **GitHub Push**: تغییرات به repository push شد
✅ **Codemagic Configuration**: فایل `codemagic.yaml` موجود است

## 📱 مراحل راه‌اندازی Codemagic

### مرحله 1: ورود به Codemagic
1. به https://codemagic.io بروید
2. روی "Log in" کلیک کنید
3. GitHub را انتخاب کنید
4. با اکانت GitHub خود وارد شوید

### مرحله 2: اضافه کردن پروژه
1. روی "Add application" کلیک کنید
2. "Connect repository" را انتخاب کنید
3. GitHub را انتخاب کنید
4. Repository `ghadirb/PersianAIAssistantAndroid` را پیدا کنید
5. روی "Select" کلیک کنید

### مرحله 3: تنظیم Workflow
پروژه شما 3 workflow دارد:

#### 🟢 Workflow 1: `simple-apk` (توصیه شده برای شروع)
- **مدت زمان**: 20 دقیقه
- **هدف**: ساخت سریع APK
- **مناسب برای**: تست اولیه

#### 🟡 Workflow 2: `android-workflow` (کامل)
- **مدت زمان**: 60 دقیقه  
- **هدف**: ساخت کامل با امضا
- **مناسب برای**: نسخه نهایی

#### 🔵 Workflow 3: `android-unsigned` (بدون امضا)
- **مدت زمان**: 30 دقیقه
- **هدف**: APK بدون امضا
- **مناسب برای**: تست و توسعه

### مرحله 4: شروع Build
1. Workflow `simple-apk` را انتخاب کنید
2. روی "Start new build" کلیک کنید
3. Branch `main` را انتخاب کنید
4. روی "Start build" کلیک کنید

## 🔍 نظارت بر Build

### علائم موفقیت:
- ✅ Build status: SUCCESS
- ✅ APK file در Artifacts
- ✅ ایمیل تأیید دریافت شده

### علائم مشکل:
- ❌ Build status: FAILED
- ❌ خطاهای Gradle
- ❌ مشکل dependencies

## 🚨 عیب‌یابی مشکلات رایج

### مشکل 1: Gradle Build Failed
**علت**: مشکل در dependencies یا configuration
**راه حل**:
```yaml
# در codemagic.yaml اضافه کنید:
environment:
  java: 17
  vars:
    GRADLE_OPTS: "-Dorg.gradle.daemon=false -Dorg.gradle.workers.max=2"
```

### مشکل 2: Hilt Compilation Error
**علت**: ناسازگاری نسخه Hilt
**راه حل**: ✅ **برطرف شده** - خط 108 در build.gradle اصلاح شد

### مشکل 3: Memory Issues
**علت**: کمبود حافظه در build
**راه حل**:
```yaml
environment:
  vars:
    GRADLE_OPTS: "-Xmx4g -XX:MaxMetaspaceSize=512m"
```

### مشکل 4: Network Issues (تحریم)
**علت**: مشکل دسترسی به repositories
**راه حل**: Codemagic از سرورهای خارجی استفاده می‌کند - مشکلی نیست

## 📧 دریافت APK

### روش 1: دانلود از Codemagic
1. Build موفق شود
2. به صفحه Build بروید
3. بخش "Artifacts" را پیدا کنید
4. فایل APK را دانلود کنید

### روش 2: دریافت از ایمیل
1. ایمیل خود را چک کنید: `ghadir.baraty@gmail.com`
2. ایمیل از Codemagic دریافت کنید
3. لینک دانلود APK را کلیک کنید

## 🔄 Build مجدد

اگر build ناموفق بود:
1. خطاها را بررسی کنید
2. مشکل را در کد اصلاح کنید
3. تغییرات را commit و push کنید
4. Build جدید شروع کنید

## 📊 مانیتورینگ

### لاگ‌های مهم:
- **Gradle logs**: مشکلات build
- **Dependency resolution**: مشکلات کتابخانه‌ها
- **APK generation**: وضعیت ساخت فایل

### زمان‌های معمول:
- **simple-apk**: 5-15 دقیقه
- **android-workflow**: 15-45 دقیقه
- **android-unsigned**: 10-25 دقیقه

## 🎯 نکات مهم

1. **اولین بار**: از `simple-apk` استفاده کنید
2. **تست محلی**: قبل از Codemagic، محلی تست کنید
3. **نظارت**: لاگ‌ها را دنبال کنید
4. **صبر**: build ممکن است کند باشد
5. **ایمیل**: همیشه ایمیل خود را چک کنید

## 🔗 لینک‌های مفید

- **Codemagic Dashboard**: https://codemagic.io/apps
- **GitHub Repository**: https://github.com/ghadirb/PersianAIAssistantAndroid
- **Codemagic Docs**: https://docs.codemagic.io/

---

## 🚀 دستور سریع

```bash
# تست محلی (اختیاری)
powershell -ExecutionPolicy Bypass -File "test-local-build.ps1"

# سپس به Codemagic بروید و build کنید
```

**موفق باشید! 🎉**
