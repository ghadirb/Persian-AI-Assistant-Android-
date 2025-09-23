param(
    [string]$GitHubToken = "<SET_VIA_ENV>"
)

Write-Host "=== GitHub Workflow Trigger ===" -ForegroundColor Green

$repo = "ghadirb/PersianAIAssistant"
$workflow = "fixed-build.yml"

$headers = @{
    "Authorization" = "token $GitHubToken"
    "Accept" = "application/vnd.github.v3+json"
}

$body = @{
    "ref" = "main"
} | ConvertTo-Json

Write-Host "Triggering workflow: $workflow" -ForegroundColor Yellow
Write-Host "Repository: $repo" -ForegroundColor Yellow

try {
    $response = Invoke-RestMethod -Uri "https://api.github.com/repos/$repo/actions/workflows/$workflow/dispatches" -Method POST -Headers $headers -Body $body -ContentType "application/json"
    Write-Host "✅ Workflow triggered successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📊 Next steps:" -ForegroundColor Cyan
    Write-Host "1. Go to: https://github.com/$repo/actions" -ForegroundColor White
    Write-Host "2. Look for 'Persian AI Assistant - Fixed Build' workflow" -ForegroundColor White
    Write-Host "3. Monitor the build progress" -ForegroundColor White
    Write-Host "4. Download APK from Artifacts section when complete" -ForegroundColor White
} catch {
    Write-Host "❌ Failed to trigger workflow:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "🔧 Troubleshooting:" -ForegroundColor Yellow
    Write-Host "- Check if the token is valid" -ForegroundColor White
    Write-Host "- Verify repository name is correct" -ForegroundColor White
    Write-Host "- Ensure workflow file exists" -ForegroundColor White
}

Write-Host ""
Write-Host "📋 Manual trigger method:" -ForegroundColor Cyan
Write-Host "1. Go to https://github.com/$repo/actions" -ForegroundColor White
Write-Host "2. Click 'Persian AI Assistant - Fixed Build'" -ForegroundColor White
Write-Host "3. Click 'Run workflow' -> Select 'debug'" -ForegroundColor White
