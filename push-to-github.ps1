$repoPath = "c:\Users\Admin\CascadeProjects\PersianAIAssistant"
$githubToken = "YOUR_GITHUB_TOKEN_HERE"

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
    git commit -m "🔧 Fix build issues: Hilt version consistency, improved CI/CD

- Fixed Hilt version inconsistency (1.1.0 → 2.48)
- Added missing version variables for biometric, junit, test-junit, espresso
- Created fixed-build.yml workflow with better error handling
- Created .gitlab-ci.yml as alternative build option
- Improved CodeMagic configuration for better APK detection
- Enhanced artifact collection in all workflows"

    # Set up remote with token
    Write-Host "Setting up remote with token authentication..." -ForegroundColor Yellow
    git remote set-url origin "https://YOUR_GITHUB_TOKEN_HERE@github.com/ghadirb/Persian-AI-Assistant-Android-.git"

    # Push changes
    Write-Host "Pushing changes to GitHub..." -ForegroundColor Yellow
    try {
        git push origin main
        Write-Host "✅ Changes pushed successfully!" -ForegroundColor Green
    }
    catch {
        Write-Host "❌ Push failed: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "Trying alternative push method..." -ForegroundColor Yellow
        git push https://YOUR_GITHUB_TOKEN_HERE@github.com/ghadirb/Persian-AI-Assistant-Android-.git main
    }
}
else {
    Write-Host "No changes to commit. Repository is up to date." -ForegroundColor Green
}

Write-Host "=== Script completed ===" -ForegroundColor Green
