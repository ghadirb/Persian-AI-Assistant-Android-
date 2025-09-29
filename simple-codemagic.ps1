# Simple Codemagic API call
$token = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
$headers = @{"x-auth-token" = $token}

Write-Host "Connecting to Codemagic..." -ForegroundColor Green

try {
    $apps = Invoke-RestMethod -Uri "https://api.codemagic.io/apps" -Headers $headers
    Write-Host "Found $($apps.applications.Count) applications" -ForegroundColor Green
    
    foreach ($app in $apps.applications) {
        Write-Host "App: $($app.appName)" -ForegroundColor Cyan
        if ($app.repository.repositoryUrl -like "*PersianAI*") {
            Write-Host "Found Persian AI app: $($app._id)" -ForegroundColor Green
        }
    }
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}
