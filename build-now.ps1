# Simple Codemagic build starter
$token = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
$appId = "68d2bb0d849df2693dd0a310"

Write-Host "Starting Codemagic build..." -ForegroundColor Green

$headers = @{
    "x-auth-token" = $token
    "Content-Type" = "application/json"
}

$buildData = @{
    appId = $appId
    branch = "main"
} | ConvertTo-Json

try {
    $build = Invoke-RestMethod -Uri "https://api.codemagic.io/builds" -Method POST -Headers $headers -Body $buildData
    Write-Host "Build started! ID: $($build._id)" -ForegroundColor Green
    Write-Host "URL: https://codemagic.io/app/$appId/build/$($build._id)" -ForegroundColor Cyan
    
    # Simple monitoring
    for ($i = 1; $i -le 20; $i++) {
        Start-Sleep 30
        try {
            $status = Invoke-RestMethod -Uri "https://api.codemagic.io/builds/$($build._id)" -Headers @{"x-auth-token" = $token}
            Write-Host "[$i] Status: $($status.status)" -ForegroundColor Yellow
            
            if ($status.status -eq "finished") {
                if ($status.buildStatus -eq "success") {
                    Write-Host "BUILD SUCCESS!" -ForegroundColor Green
                    break
                } else {
                    Write-Host "BUILD FAILED!" -ForegroundColor Red
                    break
                }
            }
        } catch {
            Write-Host "Status check error: $($_.Exception.Message)" -ForegroundColor Red
        }
    }
} catch {
    Write-Host "Build start error: $($_.Exception.Message)" -ForegroundColor Red
}
