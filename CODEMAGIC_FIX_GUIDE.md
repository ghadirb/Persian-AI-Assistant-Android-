# 🔧 راهنمای حل مشکلات Codemagic - Persian AI Assistant

## 🚨 مشکلات شناسایی شده و راه‌حل‌ها

### مشکل 1: Google Play Groups Error
**خطا**: `google_play groups not configured`
**راه‌حل**: در فایل `codemagic.yaml` خطوط زیر را comment کنید:

```yaml
# groups:
#   - google_play
```

### مشکل 2: Android Signing Error  
**خطا**: `keystore not found` یا `signing configuration missing`
**راه‌حل**: signing را comment کنید:

```yaml
# android_signing:
#   - keystore_reference
```

### مشکل 3: Build Script Error
**خطا**: `build-apk.sh not found` یا `permission denied`
**راه‌حل**: از workflow جدید `debug-only` استفاده کنید

## ✅ راه‌حل نهایی: استفاده از Workflow جدید

### Workflow پیشنهادی: `debug-only`

این workflow جدید اضافه شده و مشکلات رایج را حل می‌کند:

```yaml
debug-only:
  name: Persian AI Assistant - Debug APK Only
  max_build_duration: 25
  instance_type: mac_mini_m1
  environment:
    java: 17
    vars:
      GRADLE_OPTS: "-Dorg.gradle.daemon=false -Dorg.gradle.workers.max=2 -Xmx4g"
```

## 📋 مراحل عملی

### مرحله 1: ورود به Codemagic
1. به https://codemagic.io بروید
2. با GitHub وارد شوید
3. پروژه `PersianAIAssistantAndroid` را انتخاب کنید

### مرحله 2: انتخاب Workflow صحیح
**❌ استفاده نکنید:**
- `simple-apk` (مشکل در اسکریپت)
- `android-workflow` (مشکل Google Play)

**✅ استفاده کنید:**
- `debug-only` (جدید و بدون مشکل)

### مرحله 3: شروع Build
1. Workflow `debug-only` را انتخاب کنید
2. Branch `main` را انتخاب کنید  
3. "Start new build" را کلیک کنید

## 🔍 نظارت بر Build

### علائم موفقیت:
- ✅ "Setup environment" کامل شود
- ✅ "Build debug APK" کامل شود
- ✅ "APK created successfully!" نمایش داده شود
- ✅ فایل `persian-ai-assistant-debug.apk` در Artifacts

### علائم مشکل:
- ❌ Java version error
- ❌ Gradle daemon error
- ❌ Dependencies resolution error

## 🚨 عیب‌یابی مشکلات جدید

### اگر همچنان خطا دارید:

#### خطای Java:
```
Solution: Java 17 در codemagic.yaml تنظیم شده
```

#### خطای Gradle:
```
Solution: GRADLE_OPTS در environment vars تنظیم شده
```

#### خطای Dependencies:
```
Solution: تمام متغیرها در gradle.properties تعریف شده
```

#### خطای Memory:
```
Solution: -Xmx4g در GRADLE_OPTS اضافه شده
```

## 📧 دریافت نتیجه

پس از موفقیت build:
1. **Artifacts**: فایل `persian-ai-assistant-debug.apk` دانلود کنید
2. **Email**: به `ghadir.baraty@gmail.com` ارسال می‌شود
3. **مدت زمان**: 10-25 دقیقه

## 🔄 اگر باز هم مشکل داشتید

### گزینه 1: Workflow ساده‌تر
اگر `debug-only` هم کار نکرد، این workflow خیلی ساده را امتحان کنید:

```yaml
minimal-build:
  name: Minimal Build
  max_build_duration: 20
  instance_type: mac_mini_m1
  environment:
    java: 17
  scripts:
    - name: Simple build
      script: |
        echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties
        chmod +x ./gradlew
        ./gradlew assembleDebug --no-daemon
        cp app/build/outputs/apk/debug/app-debug.apk minimal-build.apk
  artifacts:
    - "minimal-build.apk"
```

### گزینه 2: GitHub Actions
اگر Codemagic کار نکرد، GitHub Actions را امتحان کنید:
1. به https://github.com/ghadirb/PersianAIAssistantAndroid بروید
2. Actions → "Complete Build" → "Run workflow"

## 📞 خلاصه

**بهترین راه‌حل فعلی:**
1. Workflow: `debug-only`
2. Branch: `main`  
3. انتظار: 10-25 دقیقه
4. نتیجه: فایل APK در ایمیل

**اگر مشکل داشتید، این اطلاعات را بفرستید:**
- نام workflow استفاده شده
- متن کامل خطا از Codemagic
- مرحله‌ای که خطا رخ داده

موفق باشید! 🚀
