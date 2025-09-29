# اسکریپت تشخیص مشکلات Build برای Persian AI Assistant
# این اسکریپت مشکلات رایج Codemagic را شناسایی می‌کند

Write-Host "Diagnosing Persian AI Assistant Build Issues" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green

# بررسی فایل‌های ضروری
Write-Host "📁 بررسی فایل‌های ضروری..." -ForegroundColor Yellow

$requiredFiles = @(
    "build.gradle",
    "app/build.gradle", 
    "gradle.properties",
    "settings.gradle",
    "gradlew",
    "gradlew.bat",
    "codemagic.yaml"
)

foreach ($file in $requiredFiles) {
    if (Test-Path $file) {
        Write-Host "✅ $file موجود است" -ForegroundColor Green
    } else {
        Write-Host "❌ $file موجود نیست!" -ForegroundColor Red
    }
}

# بررسی متغیرهای gradle.properties
Write-Host "`n🔧 بررسی متغیرهای gradle.properties..." -ForegroundColor Yellow

$gradleProps = Get-Content "gradle.properties" -ErrorAction SilentlyContinue
if ($gradleProps) {
    $requiredVars = @(
        "core_ktx_version",
        "appcompat_version", 
        "material_version",
        "material3_version",
        "lifecycle_version",
        "activity_compose_version",
        "compose_version",
        "hilt_version",
        "room_version",
        "biometric_version"
    )
    
    foreach ($var in $requiredVars) {
        if ($gradleProps -match "$var=") {
            Write-Host "✅ $var تعریف شده" -ForegroundColor Green
        } else {
            Write-Host "❌ $var تعریف نشده!" -ForegroundColor Red
        }
    }
} else {
    Write-Host "❌ فایل gradle.properties خوانده نشد!" -ForegroundColor Red
}

# بررسی app/build.gradle
Write-Host "`n📱 بررسی app/build.gradle..." -ForegroundColor Yellow

$appBuildGradle = Get-Content "app/build.gradle" -ErrorAction SilentlyContinue
if ($appBuildGradle) {
    # بررسی plugins
    if ($appBuildGradle -match "id 'com.android.application'") {
        Write-Host "✅ Android Application plugin موجود" -ForegroundColor Green
    } else {
        Write-Host "❌ Android Application plugin موجود نیست!" -ForegroundColor Red
    }
    
    if ($appBuildGradle -match "id 'dagger.hilt.android.plugin'") {
        Write-Host "✅ Hilt plugin موجود" -ForegroundColor Green
    } else {
        Write-Host "❌ Hilt plugin موجود نیست!" -ForegroundColor Red
    }
    
    # بررسی compileSdk
    if ($appBuildGradle -match "compileSdk\s+\d+") {
        $compileSdk = ($appBuildGradle | Select-String "compileSdk\s+(\d+)").Matches[0].Groups[1].Value
        Write-Host "✅ compileSdk: $compileSdk" -ForegroundColor Green
    } else {
        Write-Host "❌ compileSdk تعریف نشده!" -ForegroundColor Red
    }
    
    # بررسی Java version
    if ($appBuildGradle -match "JavaVersion.VERSION_17") {
        Write-Host "✅ Java 17 تنظیم شده" -ForegroundColor Green
    } else {
        Write-Host "⚠️ Java version بررسی کنید" -ForegroundColor Yellow
    }
    
} else {
    Write-Host "❌ فایل app/build.gradle خوانده نشد!" -ForegroundColor Red
}

# بررسی codemagic.yaml
Write-Host "`n🚀 بررسی codemagic.yaml..." -ForegroundColor Yellow

$codemagicYaml = Get-Content "codemagic.yaml" -ErrorAction SilentlyContinue
if ($codemagicYaml) {
    # بررسی workflows
    $workflows = ($codemagicYaml | Select-String "^\s*\w+:$" | ForEach-Object { $_.Line.Trim().Replace(":", "") })
    Write-Host "📋 Workflows موجود:" -ForegroundColor Cyan
    foreach ($workflow in $workflows) {
        if ($workflow -ne "workflows") {
            Write-Host "  - $workflow" -ForegroundColor Cyan
        }
    }
    
    # بررسی Java version
    if ($codemagicYaml -match "java:\s*17") {
        Write-Host "✅ Java 17 در Codemagic تنظیم شده" -ForegroundColor Green
    } else {
        Write-Host "⚠️ Java version در Codemagic بررسی کنید" -ForegroundColor Yellow
    }
    
    # بررسی instance type
    if ($codemagicYaml -match "instance_type:\s*mac_mini_m1") {
        Write-Host "✅ Instance type: mac_mini_m1" -ForegroundColor Green
    } else {
        Write-Host "⚠️ Instance type بررسی کنید" -ForegroundColor Yellow
    }
    
} else {
    Write-Host "❌ فایل codemagic.yaml خوانده نشد!" -ForegroundColor Red
}

# بررسی مشکلات رایج
Write-Host "`n🚨 بررسی مشکلات رایج..." -ForegroundColor Yellow

# بررسی Google Play groups
if ($codemagicYaml -match "groups:\s*-\s*google_play" -and $codemagicYaml -notmatch "#.*groups:") {
    Write-Host "⚠️ Google Play groups فعال است - ممکن است مشکل ایجاد کند" -ForegroundColor Yellow
    Write-Host "   راه حل: خط groups را comment کنید" -ForegroundColor Cyan
}

# بررسی signing configuration
if ($codemagicYaml -match "android_signing:" -and $codemagicYaml -notmatch "#.*android_signing:") {
    Write-Host "⚠️ Android signing فعال است بدون keystore" -ForegroundColor Yellow
    Write-Host "   راه حل: signing را comment کنید یا keystore اضافه کنید" -ForegroundColor Cyan
}

Write-Host "`n📋 خلاصه توصیه‌ها:" -ForegroundColor Green
Write-Host "1. از workflow 'debug-only' استفاده کنید (جدید و ساده)" -ForegroundColor Cyan
Write-Host "2. مطمئن شوید تمام متغیرها در gradle.properties تعریف شده‌اند" -ForegroundColor Cyan
Write-Host "3. Google Play groups و signing را comment کنید" -ForegroundColor Cyan
Write-Host "4. Java 17 در همه جا تنظیم شده باشد" -ForegroundColor Cyan

Write-Host "`n🎯 Workflow پیشنهادی: debug-only" -ForegroundColor Green
Write-Host "این workflow جدید اضافه شده و باید بدون مشکل کار کند" -ForegroundColor Cyan
