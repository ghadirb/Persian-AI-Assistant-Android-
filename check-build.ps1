# Simple build check for Persian AI Assistant
Write-Host "Checking Persian AI Assistant Build Configuration" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Green

# Check required files
$files = @("build.gradle", "app/build.gradle", "gradle.properties", "codemagic.yaml")
foreach ($file in $files) {
    if (Test-Path $file) {
        Write-Host "OK: $file exists" -ForegroundColor Green
    } else {
        Write-Host "ERROR: $file missing" -ForegroundColor Red
    }
}

# Check gradle.properties variables
Write-Host "`nChecking gradle.properties variables..." -ForegroundColor Yellow
$props = Get-Content "gradle.properties" -ErrorAction SilentlyContinue
if ($props) {
    $vars = @("core_ktx_version", "appcompat_version", "material_version", "hilt_version")
    foreach ($var in $vars) {
        if ($props -match "$var=") {
            Write-Host "OK: $var defined" -ForegroundColor Green
        } else {
            Write-Host "WARNING: $var not found" -ForegroundColor Yellow
        }
    }
}

# Check codemagic.yaml
Write-Host "`nChecking codemagic.yaml..." -ForegroundColor Yellow
$yaml = Get-Content "codemagic.yaml" -ErrorAction SilentlyContinue
if ($yaml) {
    if ($yaml -match "java:\s*17") {
        Write-Host "OK: Java 17 configured" -ForegroundColor Green
    }
    if ($yaml -match "debug-only:") {
        Write-Host "OK: debug-only workflow found" -ForegroundColor Green
    }
    if ($yaml -match "groups:\s*-\s*google_play" -and $yaml -notmatch "#.*groups:") {
        Write-Host "WARNING: google_play groups active - may cause issues" -ForegroundColor Yellow
    }
}

Write-Host "`nRecommendation: Use 'debug-only' workflow in Codemagic" -ForegroundColor Cyan
