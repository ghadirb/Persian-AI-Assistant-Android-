# راهنمای رفع مشکلات شبکه Gradle برای کاربران ایرانی

## مشکل
به دلیل تحریم‌ها و فیلترینگ، دسترسی به مخازن Maven و Gradle در ایران با مشکل مواجه است که باعث خطا در دانلود وابستگی‌ها و بیلد نشدن پروژه می‌شود.

## راه‌حل‌های اعمال شده

### 1. مخازن جایگزین ایرانی
در فایل‌های زیر مخازن معتبر ایرانی اضافه شده‌اند:

- **settings.gradle**: مخازن Aliyun (چین) که دسترسی بهتری از ایران دارند
- **build.gradle**: مخازن جایگزین برای buildscript
- **init.gradle**: اسکریپت پیشرفته برای مدیریت مخازن

### 2. بهینه‌سازی شبکه
در فایل `gradle.properties` تنظیمات زیر اضافه شده‌اند:
- افزایش تایم‌اوت برای اتصالات کند
- بهینه‌سازی حافظه و پردازش
- قابلیت کار آفلاین (در صورت نیاز)

## نحوه استفاده

### مرحله 1: پاک کردن کش Gradle
```bash
./gradlew clean
./gradlew --refresh-dependencies
```

### مرحله 2: اگر مشکل ادامه داشت، از حالت آفلاین استفاده کنید
در فایل `gradle.properties` خط زیر را از حالت کامنت خارج کنید:
```properties
org.gradle.offline=true
```

### مرحله 3: استفاده از VPN (در صورت نیاز)
اگر با تنظیمات بالا مشکل حل نشد:
1. از VPN با سرور امارات یا ترکیه استفاده کنید
2. دستور زیر را اجرا کنید:
```bash
./gradlew clean build --refresh-dependencies
```

### مرحله 4: تنظیمات پروکسی (اختیاری)
اگر نیاز به پروکسی دارید، در فایل `gradle.properties` اضافه کنید:
```properties
systemProp.http.proxyHost=your.proxy.host
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=your.proxy.host
systemProp.https.proxyPort=8080
```

## مخازن اضافه شده

### مخازن اصلی (اولویت بالا)
- `https://maven.aliyun.com/repository/google` - مخزن Google میرور شده
- `https://maven.aliyun.com/repository/central` - مخزن Maven Central میرور شده
- `https://maven.aliyun.com/repository/gradle-plugin` - پلاگین‌های Gradle

### مخازن پشتیبان
- `https://repo.maven.apache.org/maven2` - مخزن رسمی Apache
- `https://dl.google.com/dl/android/maven2` - مخزن مستقیم Google

## عیب‌یابی

### خطاهای رایج و راه‌حل‌ها

1. **SSL Handshake Failed**
   - از JDK 8 به بالا استفاده کنید
   - یا از VPN استفاده کنید

2. **Connection Timeout**
   - تایم‌اوت را در `gradle.properties` افزایش دهید
   - از حالت آفلاین استفاده کنید

3. **Dependency Not Found**
   - کش Gradle را پاک کنید: `./gradlew cleanBuildCache`
   - مخازن را ریفرش کنید: `./gradlew --refresh-dependencies`

4. **Gradle Sync Failed**
   - اندروید استودیو را ری‌استارت کنید
   - فایل‌های .gradle و .idea را پاک کنید و پروژه را دوباره import کنید

## نکات مهم

1. **اولویت مخازن**: مخازن ایرانی در اولویت اول قرار دارند و اگر در دسترس نباشند، به مخازن اصلی سوئیچ می‌کنند
2. **کش کردن**: وابستگی‌های دانلود شده کش می‌شوند تا در بیلدهای بعدی دوباره دانلود نشوند
3. **کار آفلاین**: با فعال کردن حالت آفلاین، Gradle فقط از کش استفاده می‌کند
4. **بهینه‌سازی**: تنظیمات JVM و شبکه برای ارتباطات کند بهینه شده‌اند

## تست نهایی

برای تست اینکه تنظیمات به درستی کار می‌کنند:
```bash
./gradlew build --info
```

این دستور اطلاعات دقیقی از فرآیند دانلود وابستگی‌ها نمایش می‌دهد.
