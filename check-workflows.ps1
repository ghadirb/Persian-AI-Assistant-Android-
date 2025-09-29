# Check available workflows
$token = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
$headers = @{"x-auth-token" = $token}
$appId = "68d2bb0d849df2693dd0a310"

Write-Host "Checking workflows for app: $appId" -ForegroundColor Green

try {
    # Get app details
    $app = Invoke-RestMethod -Uri "https://api.codemagic.io/apps/$appId" -Headers $headers
    Write-Host "App name: $($app.appName)" -ForegroundColor Cyan
    Write-Host "Repository: $($app.repository.repositoryUrl)" -ForegroundColor Cyan
    
    # Get workflows - try different endpoint
    try {
        $workflows = Invoke-RestMethod -Uri "https://api.codemagic.io/apps/$appId/workflows" -Headers $headers
        Write-Host "Workflows found:" -ForegroundColor Green
        foreach ($workflow in $workflows.workflows) {
            Write-Host "  - Name: $($workflow.name), ID: $($workflow._id)" -ForegroundColor Yellow
        }
    } catch {
        Write-Host "Could not get workflows. Trying to start build with default workflow..." -ForegroundColor Yellow
        
        # Try to start build without specific workflow
        $buildData = @{
            appId = $appId
            branch = "main"
        } | ConvertTo-Json
        
        $build = Invoke-RestMethod -Uri "https://api.codemagic.io/builds" -Headers $headers -Method POST -Body $buildData
        Write-Host "Build started! ID: $($build._id)" -ForegroundColor Green
        Write-Host "Build URL: https://codemagic.io/app/$appId/build/$($build._id)" -ForegroundColor Cyan
    }
    
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $statusCode = $_.Exception.Response.StatusCode
        Write-Host "Status Code: $statusCode" -ForegroundColor Yellow
    }
}
