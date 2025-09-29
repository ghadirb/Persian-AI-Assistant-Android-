# Codemagic API Script for Persian AI Assistant
$token = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
$headers = @{
    "x-auth-token" = $token
    "Content-Type" = "application/json"
}

Write-Host "🚀 Connecting to Codemagic API..." -ForegroundColor Green

try {
    # Get list of apps
    Write-Host "📱 Getting list of apps..." -ForegroundColor Yellow
    $apps = Invoke-RestMethod -Uri "https://api.codemagic.io/apps" -Headers $headers -Method GET
    
    Write-Host "Found $($apps.applications.Count) applications:" -ForegroundColor Green
    foreach ($app in $apps.applications) {
        Write-Host "  - $($app.appName) (ID: $($app._id))" -ForegroundColor Cyan
        if ($app.appName -like "*Persian*" -or $app.appName -like "*AI*" -or $app.repository.repositoryUrl -like "*PersianAI*") {
            $appId = $app._id
            Write-Host "    ✅ Found Persian AI Assistant app!" -ForegroundColor Green
            
            # Get workflows for this app
            Write-Host "🔧 Getting workflows..." -ForegroundColor Yellow
            $workflows = Invoke-RestMethod -Uri "https://api.codemagic.io/apps/$appId/workflows" -Headers $headers -Method GET
            
            Write-Host "Available workflows:" -ForegroundColor Green
            foreach ($workflow in $workflows.workflows) {
                Write-Host "  - $($workflow.name) (ID: $($workflow._id))" -ForegroundColor Cyan
            }
            
            # Try to start a build with minimal-build workflow
            $workflowId = "minimal-build"
            Write-Host "🚀 Starting build with workflow: $workflowId" -ForegroundColor Yellow
            
            $buildBody = @{
                appId = $appId
                workflowId = $workflowId
                branch = "main"
            } | ConvertTo-Json
            
            $build = Invoke-RestMethod -Uri "https://api.codemagic.io/builds" -Headers $headers -Method POST -Body $buildBody
            
            Write-Host "✅ Build started successfully!" -ForegroundColor Green
            Write-Host "Build ID: $($build._id)" -ForegroundColor Cyan
            Write-Host "Status: $($build.status)" -ForegroundColor Cyan
            Write-Host "URL: https://codemagic.io/app/$appId/build/$($build._id)" -ForegroundColor Cyan
            
            # Monitor build status
            Write-Host "🔍 Monitoring build status..." -ForegroundColor Yellow
            $buildId = $build._id
            
            do {
                Start-Sleep -Seconds 30
                $buildStatus = Invoke-RestMethod -Uri "https://api.codemagic.io/builds/$buildId" -Headers $headers -Method GET
                Write-Host "Build status: $($buildStatus.status)" -ForegroundColor Cyan
                
                if ($buildStatus.status -eq "finished") {
                    if ($buildStatus.buildStatus -eq "success") {
                        Write-Host "🎉 Build completed successfully!" -ForegroundColor Green
                        Write-Host "Artifacts:" -ForegroundColor Green
                        foreach ($artifact in $buildStatus.artifacts) {
                            Write-Host "  - $($artifact.name): $($artifact.url)" -ForegroundColor Cyan
                        }
                    } else {
                        Write-Host "❌ Build failed!" -ForegroundColor Red
                        Write-Host "Build logs URL: https://codemagic.io/app/$appId/build/$buildId" -ForegroundColor Yellow
                    }
                    break
                }
            } while ($buildStatus.status -eq "running" -or $buildStatus.status -eq "queued")
            
            break
        }
    }
    
    if (-not $appId) {
        Write-Host "❌ Persian AI Assistant app not found. Please add the repository to Codemagic first." -ForegroundColor Red
        Write-Host "Go to https://codemagic.io and add: https://github.com/ghadirb/PersianAIAssistantAndroid.git" -ForegroundColor Yellow
    }
} catch {
    Write-Host "❌ Error connecting to Codemagic API:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    
    if ($_.Exception.Response.StatusCode -eq 401) {
        Write-Host "🔑 Authentication failed. Please check your Codemagic token." -ForegroundColor Yellow
    }
}
