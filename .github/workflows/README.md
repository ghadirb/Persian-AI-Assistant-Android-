# 🚀 GitHub Actions Workflows راهنمای

این پوشه شامل workflow های GitHub Actions برای ساخت خودکار اپلیکیشن Persian AI Assistant است.

## 📋 فهرست Workflow ها

### 1. 🤖 Automatic Build (`android-build.yml`)
**هدف**: ساخت خودکار APK هنگام push به branch اصلی

**تریگرها**:
- Push به `main` یا `master`
- Pull Request به `main` یا `master`

**خروجی**: Debug APK + گزارش Lint

**استفاده**: خودکار - نیازی به دخالت کاربر نیست

---

### 2. 📱 Manual Build (`simple-build.yml`)
**هدف**: ساخت دستی APK با انتخاب نوع build

**تریگر**: اجرای دستی (Manual)

**ویژگی‌ها**:
- انتخاب نوع build: Debug یا Release
- امکان ساخت AAB برای Play Store
- گزارش کامل از نتایج

**نحوه استفاده**:
1. به صفحه Actions پروژه بروید
2. روی "Manual Build" کلیک کنید
3. "Run workflow" را انتخاب کنید
4. نوع build و گزینه‌های مورد نظر را انتخاب کنید
5. "Run workflow" را کلیک کنید

---

### 3. 🔍 Quick Test Build (`test-build.yml`)
**هدف**: تست سریع و عیب‌یابی مشکلات build

**تریگرها**:
- اجرای دستی
- Push به `test` یا `develop`

**ویژگی‌ها**:
- بررسی کامل ساختار پروژه
- تشخیص مشکلات احتمالی
- گزارش دقیق از وضعیت build
- زمان اجرای کوتاه (حداکثر 20 دقیقه)

---

### 4. 🏗️ Complete Build (`complete-build.yml`)
**هدف**: ساخت کامل با قابلیت‌های پیشرفته

**تریگرها**:
- Push به `main`/`master`
- Tag های version (مثل `v1.0.0`)
- Pull Request
- اجرای دستی

**ویژگی‌ها**:
- ساخت هم‌زمان APK و AAB
- ایجاد خودکار GitHub Release برای Tag ها
- کش کردن dependencies برای سرعت بیشتر
- اجرای تست‌ها
- گزارش کامل و خلاصه نتایج

## 🛠️ نحوه استفاده

### برای توسعه‌دهندگان:

#### ساخت سریع برای تست:
```bash
# استفاده از Quick Test Build
Actions → Quick Test Build → Run workflow
```

#### ساخت برای استفاده شخصی:
```bash
# استفاده از Manual Build
Actions → Manual Build → Run workflow
# انتخاب Debug برای تست یا Release برای استفاده نهایی
```

#### ساخت برای انتشار:
```bash
# ایجاد Tag برای Release خودکار
git tag v1.0.0
git push origin v1.0.0
# یا استفاده از Complete Build با گزینه Create Release
```

### برای کاربران عادی:

1. **دانلود APK آماده**:
   - به صفحه [Releases](../../releases) بروید
   - آخرین نسخه را دانلود کنید

2. **دانلود از Actions**:
   - به صفحه [Actions](../../actions) بروید
   - روی آخرین run موفق کلیک کنید
   - فایل APK را از بخش Artifacts دانلود کنید

## 🔧 تنظیمات و پیکربندی

### متغیرهای محیطی مورد نیاز:
- `GITHUB_TOKEN`: خودکار توسط GitHub تنظیم می‌شود

### فایل‌های مورد نیاز:
- ✅ `app/debug.keystore`: برای امضای Debug APK
- ✅ `gradle.properties`: تنظیمات Gradle
- ✅ `app/build.gradle`: پیکربندی اپلیکیشن

## 🚨 عیب‌یابی مشکلات رایج

### مشکل 1: Build شکست خورد
**راه‌حل**:
1. ابتدا Quick Test Build را اجرا کنید
2. لاگ‌های خطا را بررسی کنید
3. مطمئن شوید که تمام dependencies موجود است

### مشکل 2: APK ساخته نشد
**راه‌حل**:
1. بررسی کنید که `app/debug.keystore` موجود باشد
2. نسخه‌های Gradle و Android SDK را چک کنید
3. فضای کافی در runner موجود باشد

### مشکل 3: Workflow اجرا نمی‌شود
**راه‌حل**:
1. مطمئن شوید که Actions در repository فعال است
2. دسترسی‌های لازم را بررسی کنید
3. syntax فایل‌های YAML را چک کنید

## 📊 مقایسه Workflow ها

| Workflow | زمان اجرا | خروجی | استفاده |
|----------|-----------|--------|---------|
| Automatic Build | ~15 دقیقه | Debug APK | توسعه روزانه |
| Manual Build | ~20 دقیقه | Debug/Release APK + AAB | ساخت دستی |
| Quick Test Build | ~10 دقیقه | Debug APK + گزارش | عیب‌یابی |
| Complete Build | ~25 دقیقه | همه فرمت‌ها + Release | انتشار رسمی |

## 🔄 بهینه‌سازی‌های اعمال شده

### برای کاربران ایرانی:
- ✅ افزایش timeout برای اتصالات کند
- ✅ کش کردن dependencies
- ✅ استفاده از mirror های سریع‌تر

### برای بهبود عملکرد:
- ✅ Parallel execution
- ✅ Conditional steps
- ✅ Optimized caching
- ✅ Resource cleanup

## 📞 پشتیبانی

اگر با مشکلی مواجه شدید:
1. ابتدا این راهنما را مطالعه کنید
2. لاگ‌های Actions را بررسی کنید
3. در صورت نیاز، Issue جدید ایجاد کنید

---

**نکته**: همیشه از آخرین نسخه workflow ها استفاده کنید تا از بهترین عملکرد برخوردار شوید.
