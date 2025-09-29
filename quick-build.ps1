# Simple API build
Write-Host "Starting build..." -ForegroundColor Green

$token = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
$appId = "68d2bb0d849df2693dd0a310"

$headers = @{
    "x-auth-token" = $token
    "Content-Type" = "application/json"
}

$buildData = @{
    appId = $appId
    workflowId = "clean-build"
    branch = "main"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri "https://api.codemagic.io/builds" -Method POST -Headers $headers -Body $buildData
    Write-Host "SUCCESS!" -ForegroundColor Green
    Write-Host "Build URL: https://codemagic.io/app/$appId/build/$($response._id)" -ForegroundColor Cyan
} catch {
    Write-Host "FAILED: $($_.Exception.Message)" -ForegroundColor Red
}
