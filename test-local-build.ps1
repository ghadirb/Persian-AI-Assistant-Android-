# تست محلی بیلد Persian AI Assistant
# این اسکریپت قبل از Codemagic تست می‌کند که آیا پروژه محلی بیلد می‌شود یا نه

Write-Host "🚀 شروع تست محلی بیلد Persian AI Assistant" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green

# بررسی Java version
Write-Host "☕ بررسی Java version..." -ForegroundColor Yellow
java -version
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Java نصب نیست یا در PATH موجود نیست" -ForegroundColor Red
    exit 1
}

# بررسی Android SDK
Write-Host "📱 بررسی Android SDK..." -ForegroundColor Yellow
if (-not $env:ANDROID_HOME) {
    Write-Host "⚠️ ANDROID_HOME تنظیم نشده - ممکن است در Codemagic مشکلی نباشد" -ForegroundColor Yellow
}

# تست Gradle wrapper
Write-Host "🔧 تست Gradle wrapper..." -ForegroundColor Yellow
if (Test-Path ".\gradlew.bat") {
    Write-Host "✅ gradlew.bat موجود است" -ForegroundColor Green
} else {
    Write-Host "❌ gradlew.bat موجود نیست" -ForegroundColor Red
    exit 1
}

# تست dependencies
Write-Host "📦 بررسی dependencies..." -ForegroundColor Yellow
.\gradlew.bat dependencies --configuration debugCompileClasspath | Select-String "hilt"
if ($LASTEXITCODE -ne 0) {
    Write-Host "⚠️ مشکل در dependencies - ادامه می‌دهیم..." -ForegroundColor Yellow
}

# تست clean
Write-Host "🧹 پاک کردن build cache..." -ForegroundColor Yellow
.\gradlew.bat clean
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ مشکل در clean" -ForegroundColor Red
    exit 1
}

# تست build
Write-Host "🔨 تست build debug APK..." -ForegroundColor Yellow
.\gradlew.bat assembleDebug --stacktrace --info
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ بیلد موفق بود!" -ForegroundColor Green
    
    # جستجوی APK
    $apkFiles = Get-ChildItem -Recurse -Filter "*.apk" | Where-Object { $_.Name -like "*debug*" }
    if ($apkFiles) {
        Write-Host "📱 فایل‌های APK پیدا شده:" -ForegroundColor Green
        foreach ($apk in $apkFiles) {
            Write-Host "  - $($apk.FullName) ($(($apk.Length / 1MB).ToString('F2')) MB)" -ForegroundColor Cyan
        }
    } else {
        Write-Host "⚠️ فایل APK پیدا نشد" -ForegroundColor Yellow
    }
} else {
    Write-Host "❌ بیلد ناموفق - بررسی خطاها:" -ForegroundColor Red
    Write-Host "1. مشکل Hilt version" -ForegroundColor Yellow
    Write-Host "2. مشکل dependencies" -ForegroundColor Yellow
    Write-Host "3. مشکل Android SDK" -ForegroundColor Yellow
    exit 1
}

Write-Host "🎉 تست محلی کامل شد - آماده برای Codemagic!" -ForegroundColor Green
