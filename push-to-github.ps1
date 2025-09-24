$repoPath = "c:\Users\Admin\CascadeProjects\PersianAIAssistantAndroid"
$githubToken = "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"

Write-Host "=== Persian AI Assistant Git Push Script ===" -ForegroundColor Green

# Change to repository directory
Set-Location $repoPath

# Check git status
Write-Host "Checking git status..." -ForegroundColor Yellow
$status = git status --porcelain

if ($status) {
    Write-Host "Found changes to commit:" -ForegroundColor Yellow
    Write-Host $status -ForegroundColor Gray

    # Add all changes
    Write-Host "Adding changes..." -ForegroundColor Yellow
    git add .

    # Commit changes
    Write-Host "Committing changes..." -ForegroundColor Yellow
    git commit -m "🚀 Add minimal-build workflow and comprehensive fixes"

    # Set up remote with token
    Write-Host "Setting up remote with token authentication..." -ForegroundColor Yellow
    git remote set-url origin "https://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@github.com/ghadirb/PersianAIAssistantAndroid.git"

    # Push changes
    Write-Host "Pushing changes to GitHub..." -ForegroundColor Yellow
    try {
        git push origin main
        Write-Host "✅ Changes pushed successfully!" -ForegroundColor Green
    }
    catch {
        Write-Host "❌ Push failed: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "Trying alternative push method..." -ForegroundColor Yellow
        git push https://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@github.com/ghadirb/PersianAIAssistantAndroid.git main
    }
}
else {
    Write-Host "No changes to commit. Repository is up to date." -ForegroundColor Green
}

Write-Host "=== Script completed ===" -ForegroundColor Green
