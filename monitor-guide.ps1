param(
    [string]$GitHubToken = "<SET_VIA_ENV>",
    [string]$CodeMagicToken = "<SET_VIA_ENV>",
    [string]$GitLabToken = "<SET_VIA_ENV>"
)

Write-Host "=== Persian AI Assistant Build Monitor ===" -ForegroundColor Green
Write-Host "Direct monitoring links and commands" -ForegroundColor Yellow
Write-Host ""

$githubRepo = "ghadirb/PersianAIAssistant"

Write-Host "🔗 DIRECT MONITORING LINKS:" -ForegroundColor Cyan
Write-Host "===========================" -ForegroundColor Cyan
Write-Host ""

Write-Host "📊 GitHub Actions:" -ForegroundColor White
Write-Host "https://github.com/$githubRepo/actions" -ForegroundColor Green
Write-Host ""

Write-Host "🔧 CodeMagic Dashboard:" -ForegroundColor White
Write-Host "https://codemagic.io/" -ForegroundColor Green
Write-Host "(Login with token: $CodeMagicToken)" -ForegroundColor Gray
Write-Host ""

Write-Host "🐙 GitLab (Alternative):" -ForegroundColor White
Write-Host "https://gitlab.com" -ForegroundColor Green
Write-Host "(Use token: $GitLabToken)" -ForegroundColor Gray
Write-Host ""

Write-Host "📱 APK Download Links (when builds complete):" -ForegroundColor White
Write-Host "- GitHub: https://github.com/$githubRepo/actions (Artifacts section)" -ForegroundColor Yellow
Write-Host "- CodeMagic: Check email at ghadir.baraty@gmail.com" -ForegroundColor Yellow
Write-Host "- GitLab: Repository page -> CI/CD -> Jobs" -ForegroundColor Yellow
Write-Host ""

Write-Host "🚀 QUICK ACTIONS:" -ForegroundColor Cyan
Write-Host "==================" -ForegroundColor Cyan
Write-Host ""

Write-Host "1. Push to GitHub:" -ForegroundColor White
Write-Host "   git push https://$GitHubToken@github.com/$githubRepo.git main" -ForegroundColor Gray
Write-Host ""

Write-Host "2. Trigger GitHub Workflow:" -ForegroundColor White
Write-Host "   curl -X POST -H `"Authorization: token $GitHubToken`" -H `"Accept: application/vnd.github.v3+json`" https://api.github.com/repos/$githubRepo/actions/workflows/fixed-build.yml/dispatches -d `'{\`"ref\`":\`"main\`"}`'" -ForegroundColor Gray
Write-Host ""

Write-Host "3. Check GitHub Build Status:" -ForegroundColor White
Write-Host "   curl -H `"Authorization: token $GitHubToken`" https://api.github.com/repos/$githubRepo/actions/runs" -ForegroundColor Gray
Write-Host ""

Write-Host "📋 MANUAL STEPS:" -ForegroundColor Cyan
Write-Host "==================" -ForegroundColor Cyan
Write-Host "1. Copy and paste the URLs above into your browser" -ForegroundColor White
Write-Host "2. Login to each platform with your tokens" -ForegroundColor White
Write-Host "3. Monitor build progress in real-time" -ForegroundColor White
Write-Host "4. Download APK files when builds complete" -ForegroundColor White
Write-Host ""
Write-Host "🎯 WHAT TO LOOK FOR:" -ForegroundColor Cyan
Write-Host "- Green checkmarks (✅) for successful builds" -ForegroundColor Green
Write-Host "- Red X marks (❌) for failed builds" -ForegroundColor Red
Write-Host "- Download buttons for APK files" -ForegroundColor Yellow
Write-Host ""

Write-Host "📞 REPORT BACK:" -ForegroundColor Cyan
Write-Host "After testing, tell me:" -ForegroundColor White
Write-Host "- Which platform worked?" -ForegroundColor White
Write-Host "- Any error messages you see?" -ForegroundColor White
Write-Host "- Did you get APK files?" -ForegroundColor White
Write-Host ""

Write-Host "✅ Build monitoring guide ready!" -ForegroundColor Green
Write-Host ""
Write-Host "Open the URLs above in your browser to monitor builds directly." -ForegroundColor Yellow
