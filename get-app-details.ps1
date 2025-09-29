# Get Codemagic app details and start build
$token = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
$headers = @{"x-auth-token" = $token; "Content-Type" = "application/json"}

Write-Host "Getting Persian AI Assistant app details..." -ForegroundColor Green

try {
    $apps = Invoke-RestMethod -Uri "https://api.codemagic.io/apps" -Headers $headers
    
    foreach ($app in $apps.applications) {
        if ($app.appName -eq "PersianAIAssistantAndroid") {
            $appId = $app._id
            Write-Host "Found app ID: $appId" -ForegroundColor Green
            
            # Get workflows
            $workflows = Invoke-RestMethod -Uri "https://api.codemagic.io/apps/$appId/workflows" -Headers $headers
            Write-Host "Available workflows:" -ForegroundColor Yellow
            foreach ($workflow in $workflows.workflows) {
                Write-Host "  - $($workflow.name)" -ForegroundColor Cyan
            }
            
            # Start build with minimal-build
            Write-Host "Starting build with minimal-build workflow..." -ForegroundColor Yellow
            $buildData = @{
                appId = $appId
                workflowId = "minimal-build"
                branch = "main"
            } | ConvertTo-Json
            
            $build = Invoke-RestMethod -Uri "https://api.codemagic.io/builds" -Headers $headers -Method POST -Body $buildData
            Write-Host "Build started! ID: $($build._id)" -ForegroundColor Green
            Write-Host "Build URL: https://codemagic.io/app/$appId/build/$($build._id)" -ForegroundColor Cyan
            
            return $build._id
        }
    }
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $responseBody = $reader.ReadToEnd()
        Write-Host "Response: $responseBody" -ForegroundColor Yellow
    }
}
