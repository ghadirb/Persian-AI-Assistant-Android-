# Start Codemagic build
$token = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
$appId = "68d2bb0d849df2693dd0a310"

Write-Host "🚀 Starting Codemagic build for Persian AI Assistant..." -ForegroundColor Green

try {
    # Prepare build request
    $buildData = @{
        appId = $appId
        branch = "main"
    }
    
    $json = $buildData | ConvertTo-Json
    $headers = @{
        "x-auth-token" = $token
        "Content-Type" = "application/json"
    }
    
    Write-Host "Sending build request..." -ForegroundColor Yellow
    Write-Host "App ID: $appId" -ForegroundColor Cyan
    Write-Host "Branch: main" -ForegroundColor Cyan
    
    $response = Invoke-RestMethod -Uri "https://api.codemagic.io/builds" -Method POST -Headers $headers -Body $json
    
    Write-Host "✅ Build started successfully!" -ForegroundColor Green
    Write-Host "Build ID: $($response._id)" -ForegroundColor Cyan
    Write-Host "Status: $($response.status)" -ForegroundColor Cyan
    Write-Host "Build URL: https://codemagic.io/app/$appId/build/$($response._id)" -ForegroundColor Yellow
    
    # Monitor build status
    $buildId = $response._id
    Write-Host "`n🔍 Monitoring build progress..." -ForegroundColor Yellow
    
    $maxAttempts = 40  # 20 minutes max
    $attempt = 0
    
    do {
        Start-Sleep -Seconds 30
        $attempt++
        
        try {
            $buildStatus = Invoke-RestMethod -Uri "https://api.codemagic.io/builds/$buildId" -Headers @{"x-auth-token" = $token}
            $status = $buildStatus.status
            $progress = if ($buildStatus.buildStatus) { $buildStatus.buildStatus } else { "in-progress" }
            
            Write-Host "[$attempt/$maxAttempts] Status: $status | Progress: $progress" -ForegroundColor Cyan
            
            if ($status -eq "finished") {
                if ($buildStatus.buildStatus -eq "success") {
                    Write-Host "`n🎉 BUILD SUCCESSFUL!" -ForegroundColor Green
                    Write-Host "Build completed successfully!" -ForegroundColor Green
                    
                    if ($buildStatus.artifacts -and $buildStatus.artifacts.Count -gt 0) {
                        Write-Host "`n📦 Artifacts available:" -ForegroundColor Green
                        foreach ($artifact in $buildStatus.artifacts) {
                            Write-Host "  - $($artifact.name)" -ForegroundColor Cyan
                            Write-Host "    Download: $($artifact.url)" -ForegroundColor Yellow
                        }
                    }
                    
                    Write-Host "`n📧 Check your email: ghadir.baraty@gmail.com" -ForegroundColor Green
                    return $true
                    
                } elseif ($buildStatus.buildStatus -eq "failed") {
                    Write-Host "`n❌ BUILD FAILED!" -ForegroundColor Red
                    Write-Host "Build failed. Check logs at: https://codemagic.io/app/$appId/build/$buildId" -ForegroundColor Yellow
                    
                    # Try to get build logs
                    try {
                        $logs = Invoke-RestMethod -Uri "https://api.codemagic.io/builds/$buildId/logs" -Headers @{"x-auth-token" = $token}
                        Write-Host "`nLast few log lines:" -ForegroundColor Yellow
                        $logs | Select-Object -Last 10 | ForEach-Object { Write-Host "  $_" -ForegroundColor Gray }
                    } catch {
                        Write-Host "Could not retrieve build logs via API" -ForegroundColor Yellow
                    }
                    
                    return $false
                } else {
                    Write-Host "`n⚠️ BUILD COMPLETED WITH UNKNOWN STATUS: $($buildStatus.buildStatus)" -ForegroundColor Yellow
                    return $false
                }
            }
            
        } catch {
            Write-Host "Error checking build status: $($_.Exception.Message)" -ForegroundColor Red
        }
        
    } while ($status -ne "finished" -and $attempt -lt $maxAttempts)
    
    if ($attempt -ge $maxAttempts) {
        Write-Host "`n⏰ Build monitoring timeout. Check manually at:" -ForegroundColor Yellow
        Write-Host "https://codemagic.io/app/$appId/build/$buildId" -ForegroundColor Cyan
    }
    
} catch {
    Write-Host "❌ Error starting build:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $statusCode = $_.Exception.Response.StatusCode.value__
        Write-Host "HTTP Status Code: $statusCode" -ForegroundColor Yellow
        
        if ($statusCode -eq 401) {
            Write-Host "🔑 Authentication failed. Check your Codemagic token." -ForegroundColor Yellow
        } elseif ($statusCode -eq 404) {
            Write-Host "🔍 App not found. Make sure the repository is added to Codemagic." -ForegroundColor Yellow
        } elseif ($statusCode -eq 400) {
            Write-Host "📝 Bad request. Check the build parameters." -ForegroundColor Yellow
        }
    }
    
    return $false
}
