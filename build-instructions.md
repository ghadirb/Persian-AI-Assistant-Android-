# راهنمای ساخت APK با Codemagic

## وضعیت فعلی برنامه

✅ **برنامه آماده است** - تمام ویژگی‌ها پیاده‌سازی شده
❌ **امضای Play Store** - هنوز تنظیم نشده (نسخه عمومی)

## مراحل ساخت APK

### 1. تنظیم Codemagic

1. به [codemagic.io](https://codemagic.io) بروید و حساب ایجاد کنید
2. پروژه را از GitHub/GitLab متصل کنید
3. فایل `codemagic.yaml` در root پروژه موجود است

### 2. تنظیم متغیرهای محیطی

در Codemagic این متغیرها را تنظیم کنید:

```
KEYSTORE_PATH=release-key.keystore
KEYSTORE_PASSWORD=your_password
KEY_ALIAS=your_alias  
KEY_PASSWORD=your_key_password
```

### 3. انواع Build

#### APK بدون امضا (برای تست):
```bash
./gradlew assembleUnsigned
```

#### APK امضا شده (برای انتشار):
```bash  
./gradlew assembleRelease
```

#### Android App Bundle:
```bash
./gradlew bundleRelease
```

### 4. دریافت فایل

پس از build موفق:
- فایل APK در بخش Artifacts دانلود می‌شود
- یا از طریق ایمیل ارسال می‌شود

## نکات مهم

### امضای برنامه
- فعلاً برنامه امضای Play Store ندارد
- برای تست: از build type `unsigned` استفاده کنید
- برای انتشار: keystore ایجاد کنید

### ایجاد Keystore
```bash
keytool -genkey -v -keystore release-key.keystore -alias persian-ai -keyalg RSA -keysize 2048 -validity 10000
```

### تنظیمات Build
- `minSdk`: 26 (Android 8.0+)
- `targetSdk`: 34 (Android 14)
- `versionCode`: 1
- `versionName`: "1.0"

## خروجی‌های مختلف

1. **Debug APK**: `app-debug.apk` - برای توسعه
2. **Unsigned APK**: `app-unsigned.apk` - برای تست
3. **Release APK**: `app-release.apk` - برای انتشار
4. **AAB**: `app-release.aab` - برای Play Store

## مسیر فایل‌ها
```
android/app/build/outputs/
├── apk/
│   ├── debug/app-debug.apk
│   ├── unsigned/app-unsigned.apk
│   └── release/app-release.apk
└── bundle/
    └── release/app-release.aab
```
