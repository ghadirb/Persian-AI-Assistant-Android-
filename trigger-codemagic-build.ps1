# اسکریپت تریگر Codemagic Build برای Persian AI Assistant
# این اسکریپت از API استفاده می‌کند تا build را شروع کند

param(
    [string]$WorkflowName = "simple-apk",
    [string]$Branch = "main"
)

$CodemagicToken = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
$AppId = "" # این باید از Codemagic dashboard گرفته شود

Write-Host "🚀 تریگر Codemagic Build برای Persian AI Assistant" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green
Write-Host "Workflow: $WorkflowName" -ForegroundColor Cyan
Write-Host "Branch: $Branch" -ForegroundColor Cyan

# بررسی اینکه آیا App ID موجود است
if ([string]::IsNullOrEmpty($AppId)) {
    Write-Host "❌ App ID موجود نیست!" -ForegroundColor Red
    Write-Host "برای گرفتن App ID:" -ForegroundColor Yellow
    Write-Host "1. به https://codemagic.io/apps بروید" -ForegroundColor Yellow
    Write-Host "2. پروژه Persian AI Assistant را انتخاب کنید" -ForegroundColor Yellow
    Write-Host "3. از URL، App ID را کپی کنید" -ForegroundColor Yellow
    Write-Host "4. در این اسکریپت، متغیر AppId را تنظیم کنید" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "🌐 راه جایگزین: استفاده از Web Interface" -ForegroundColor Green
    Write-Host "1. به https://codemagic.io بروید" -ForegroundColor Cyan
    Write-Host "2. وارد شوید با GitHub" -ForegroundColor Cyan
    Write-Host "3. پروژه PersianAIAssistantAndroid را اضافه کنید" -ForegroundColor Cyan
    Write-Host "4. Workflow '$WorkflowName' را انتخاب کنید" -ForegroundColor Cyan
    Write-Host "5. 'Start new build' را کلیک کنید" -ForegroundColor Cyan
    exit 1
}

# تنظیم headers
$headers = @{
    "Content-Type" = "application/json"
    "x-auth-token" = $CodemagicToken
}

# تنظیم body
$body = @{
    appId = $AppId
    workflowId = $WorkflowName
    branch = $Branch
} | ConvertTo-Json

try {
    Write-Host "📡 ارسال درخواست به Codemagic API..." -ForegroundColor Yellow
    
    $response = Invoke-RestMethod -Uri "https://api.codemagic.io/builds" -Method POST -Headers $headers -Body $body
    
    Write-Host "✅ Build شروع شد!" -ForegroundColor Green
    Write-Host "Build ID: $($response.buildId)" -ForegroundColor Cyan
    Write-Host "Status: $($response.status)" -ForegroundColor Cyan
    Write-Host "URL: https://codemagic.io/app/$AppId/build/$($response.buildId)" -ForegroundColor Cyan
    
    Write-Host ""
    Write-Host "🔍 برای نظارت بر build:" -ForegroundColor Yellow
    Write-Host "1. به لینک بالا بروید" -ForegroundColor Cyan
    Write-Host "2. یا ایمیل خود را چک کنید: ghadir.baraty@gmail.com" -ForegroundColor Cyan
    
} catch {
    Write-Host "❌ خطا در تریگر build:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-Host "🔑 مشکل احراز هویت - توکن را بررسی کنید" -ForegroundColor Yellow
    } elseif ($_.Exception.Response.StatusCode -eq 404) {
        Write-Host "🔍 App ID یا Workflow پیدا نشد" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "🌐 راه جایگزین: استفاده از Web Interface" -ForegroundColor Green
    Write-Host "به https://codemagic.io بروید و دستی build کنید" -ForegroundColor Cyan
}

Write-Host ""
Write-Host "📋 Workflows موجود:" -ForegroundColor Yellow
Write-Host "- simple-apk: ساخت سریع APK (20 دقیقه)" -ForegroundColor Cyan
Write-Host "- android-workflow: ساخت کامل (60 دقیقه)" -ForegroundColor Cyan  
Write-Host "- android-unsigned: APK بدون امضا (30 دقیقه)" -ForegroundColor Cyan
