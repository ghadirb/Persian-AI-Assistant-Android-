# دستیار هوش مصنوعی فارسی (Persian AI Assistant)

یک دستیار هوش مصنوعی کامل و پیشرفته برای اندروید با پشتیبانی کامل از زبان فارسی

## ویژگی‌های کلیدی

### 🤖 هوش مصنوعی دوگانه
- **حالت آنلاین**: استفاده از مدل‌های قدرتمند ابری (GPT-4o, Claude, Gemini)
- **حالت آفلاین**: اجرای مدل‌های محلی بدون نیاز به اینترنت

### 🎙️ قابلیت‌های صوتی پیشرفته
- تشخیص گفتار فارسی آفلاین با Vosk
- سنتز گفتار فارسی با Android TTS
- پیام‌های صوتی مشابه تلگرام
- فعال‌سازی صوتی دستیار

### 🔐 امنیت و حریم خصوصی
- رمزگذاری AES-GCM برای کلیدهای API
- احراز هویت بیومتریک (اثر انگشت/تشخیص چهره)
- محافظت با رمز عبور
- ذخیره‌سازی امن داده‌ها

### 💾 مدیریت داده‌ها
- پایگاه داده محلی Room
- پشتیبان‌گیری خودکار و دستی
- همگام‌سازی با Google Drive
- بازیابی کامل اطلاعات

### 🌐 یکپارچگی با اپلیکیشن‌ها
- نقشه‌ها (Google Maps)
- تقویم
- مخاطبین
- پیامک و تماس
- ایمیل و مرورگر

### 🎨 رابط کاربری
- طراحی Material Design 3
- پشتیبانی کامل RTL برای فارسی
- فونت‌های بهینه شده فارسی (Vazir)
- رابط ساده برای سالمندان

## معماری فنی

### 🏗️ Architecture Pattern
- **MVVM** با Repository Pattern
- **Dependency Injection** با Hilt
- **Reactive Programming** با Flow

### 🛠️ تکنولوژی‌های استفاده شده
- **UI**: Jetpack Compose
- **Database**: Room + SQLite
- **Network**: OkHttp + Retrofit
- **Voice**: Vosk + Android TTS
- **Background**: WorkManager
- **Security**: Biometric + DataStore

### 📱 پشتیبانی از Android
- **حداقل SDK**: 26 (Android 8.0)
- **هدف SDK**: 34 (Android 14)
- **Java Compatibility**: 17

## نصب و راه‌اندازی

### پیش‌نیازها
```bash
- Android Studio Hedgehog یا جدیدتر
- JDK 17
- Android SDK 34
- Gradle 8.0+
```

### مراحل نصب
1. کلون کردن پروژه:
```bash
git clone https://github.com/yourusername/PersianAIAssistant.git
cd PersianAIAssistant
```

2. باز کردن در Android Studio

3. همگام‌سازی Gradle:
```bash
./gradlew build
```

4. اجرای برنامه:
```bash
./gradlew installDebug
```

## ساخت APK با Codemagic

### تنظیم Codemagic
1. حساب کاربری در [Codemagic.io](https://codemagic.io) ایجاد کنید
2. پروژه را به Codemagic متصل کنید
3. متغیرهای محیطی زیر را تنظیم کنید:

```yaml
Environment Variables:
- KEYSTORE_PATH: مسیر فایل keystore
- KEYSTORE_PASSWORD: رمز keystore  
- KEY_ALIAS: نام alias کلید
- KEY_PASSWORD: رمز کلید
- GCLOUD_SERVICE_ACCOUNT_CREDENTIALS: اعتبارنامه Google Play
```

### انواع Build
```bash
# APK امضا شده برای Play Store
./gradlew assembleRelease

# APK بدون امضا برای تست
./gradlew assembleUnsigned

# Android App Bundle (AAB)
./gradlew bundleRelease
```

### دانلود APK
پس از اتمام build در Codemagic:
1. به بخش Artifacts بروید
2. فایل APK را دانلود کنید
3. یا از طریق ایمیل دریافت کنید

## پیکربندی

### کلیدهای API
1. فایل `google-services.json` را در پوشه `app/` قرار دهید
2. کلیدهای API را در Google Drive رمزگذاری کرده و آپلود کنید
3. لینک فایل رمزگذاری شده را در `GoogleDriveService.kt` تنظیم کنید

### مدل‌های محلی
1. مدل‌های GGUF، ONNX یا TFLite را دانلود کنید
2. در پوشه `Android/data/com.example.persianaiapp/files/models/` قرار دهید
3. از طریق تنظیمات برنامه مدل را انتخاب کنید

## ساختار پروژه

```
app/
├── src/main/java/com/example/persianaiapp/
│   ├── ai/                 # سرویس‌های هوش مصنوعی
│   ├── data/               # لایه داده (Room, Repository)
│   ├── ui/                 # رابط کاربری (Compose)
│   ├── voice/              # پردازش صوت
│   ├── security/           # امنیت و رمزگذاری
│   ├── integration/        # یکپارچگی با اپ‌ها
│   ├── worker/             # کارهای پس‌زمینه
│   └── util/               # ابزارهای کمکی
├── src/main/res/
│   ├── values/             # منابع فارسی
│   ├── drawable/           # آیکون‌ها
│   └── font/               # فونت‌های فارسی
└── google-services.json    # پیکربندی Google
```

## API های پشتیبانی شده

### مدل‌های آنلاین
- **OpenAI**: GPT-4o, GPT-4o-mini, GPT-4-turbo
- **Anthropic**: Claude-3 Opus, Sonnet, Haiku
- **Google**: Gemini 1.5 Pro, Flash

### مدل‌های آفلاین
- **GGUF**: فرمت llama.cpp
- **ONNX**: مدل‌های ONNX Runtime
- **TFLite**: TensorFlow Lite

## مجوزهای مورد نیاز

### ضروری
- `RECORD_AUDIO`: ضبط صدا
- `INTERNET`: دسترسی به اینترنت
- `READ_EXTERNAL_STORAGE`: خواندن فایل‌ها
- `WRITE_EXTERNAL_STORAGE`: نوشتن فایل‌ها

### اختیاری
- `CALL_PHONE`: تماس گرفتن
- `SEND_SMS`: ارسال پیامک
- `READ_CONTACTS`: دسترسی به مخاطبین
- `ACCESS_FINE_LOCATION`: موقعیت مکانی

## توسعه و مشارکت

### راهنمای مشارکت
1. Fork کردن پروژه
2. ایجاد branch جدید (`git checkout -b feature/AmazingFeature`)
3. Commit تغییرات (`git commit -m 'Add some AmazingFeature'`)
4. Push به branch (`git push origin feature/AmazingFeature`)
5. ایجاد Pull Request

### استانداردهای کد
- استفاده از Kotlin Code Style
- نوشتن تست‌های واحد
- مستندسازی کامل کد
- پیروی از معماری MVVM

## مجوز

این پروژه تحت مجوز MIT منتشر شده است. برای جزئیات بیشتر فایل [LICENSE](LICENSE) را مطالعه کنید.

## تماس و پشتیبانی

- **ایمیل**: support@persianai.app
- **وب‌سایت**: https://persianai.app
- **تلگرام**: @PersianAISupport

## تشکر ویژه

- تیم Vosk برای موتور تشخیص گفتار
- پروژه فونت Vazir
- جامعه توسعه‌دهندگان Android فارسی

---

**نسخه**: 1.0.0  
**آخرین بروزرسانی**: دی ۱۴۰۳  
**وضعیت**: آماده برای تولید
