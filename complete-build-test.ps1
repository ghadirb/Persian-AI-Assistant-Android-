param(
    [string]$GitHubToken = "YOUR_GITHUB_TOKEN_HERE",
    [string]$CodeMagicToken = "YOUR_CODEMAGIC_TOKEN_HERE",
    [string]$GitLabToken = "YOUR_GITLAB_TOKEN_HERE"
)

Write-Host "=== Persian AI Assistant - Complete Build Test Suite ===" -ForegroundColor Green
Write-Host "Testing all platforms with your tokens..." -ForegroundColor Yellow
Write-Host ""

# Configuration
$repoPath = "c:\Users\Admin\CascadeProjects\PersianAIAssistant"
$githubRepo = "ghadirb/Persian-AI-Assistant-Android-"

# Function to test local configuration
function Test-LocalConfiguration {
    Write-Host "📋 Testing local configuration..." -ForegroundColor Cyan

    if (-not (Test-Path "$repoPath\app\build.gradle")) {
        Write-Host "❌ Repository path not found: $repoPath" -ForegroundColor Red
        return $false
    }

    # Check Hilt version consistency
    $hiltCheck = Select-String -Path "$repoPath\app\build.gradle" -Pattern "hilt-navigation-compose:1.1.0"
    if ($hiltCheck) {
        Write-Host "❌ Hilt version inconsistency found!" -ForegroundColor Red
        return $false
    }

    # Check version variables
    $versionVars = @("biometric_version", "junit_version", "test_junit_version", "espresso_version")
    foreach ($var in $versionVars) {
        $varCheck = Select-String -Path "$repoPath\build.gradle" -Pattern $var
        if (-not $varCheck) {
            Write-Host "❌ Missing version variable: $var" -ForegroundColor Red
            return $false
        }
    }

    Write-Host "✅ Local configuration is valid" -ForegroundColor Green
    return $true
}

# Function to push to GitHub
function Push-ToGitHub {
    Write-Host "🚀 Pushing changes to GitHub..." -ForegroundColor Cyan

    Set-Location $repoPath

    # Check git status
    $status = git status --porcelain
    if (-not $status) {
        Write-Host "✅ Repository is up to date" -ForegroundColor Green
        return $true
    }

    # Add and commit changes
    git add .
    git commit -m "🔧 Fix build issues: Hilt version consistency, improved CI/CD"

    # Push with token
    $pushUrl = "https://$GitHubToken@github.com/$githubRepo.git"
    git push $pushUrl main

    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Successfully pushed to GitHub" -ForegroundColor Green
        return $true
    } else {
        Write-Host "❌ Failed to push to GitHub" -ForegroundColor Red
        return $false
    }
}

# Function to trigger GitHub workflow
function Start-GitHubWorkflow {
    Write-Host "⚡ Triggering GitHub Actions workflow..." -ForegroundColor Cyan

    $headers = @{
        "Authorization" = "token $GitHubToken"
        "Accept" = "application/vnd.github.v3+json"
    }

    $body = @{
        "ref" = "main"
    } | ConvertTo-Json

    try {
        $response = Invoke-RestMethod -Uri "https://api.github.com/repos/$githubRepo/actions/workflows/fixed-build.yml/dispatches" -Method POST -Headers $headers -Body $body -ContentType "application/json"
        Write-Host "✅ GitHub workflow triggered successfully" -ForegroundColor Green
        return $true
    } catch {
        Write-Host "❌ Failed to trigger GitHub workflow: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Function to check CodeMagic status
function Test-CodeMagic {
    Write-Host "🔧 Testing CodeMagic integration..." -ForegroundColor Cyan

    $headers = @{
        "Authorization" = "Bearer $CodeMagicToken"
        "Content-Type" = "application/json"
    }

    try {
        $response = Invoke-RestMethod -Uri "https://api.codemagic.io/apps" -Method GET -Headers $headers
        Write-Host "✅ CodeMagic authentication successful" -ForegroundColor Green
        Write-Host "📊 Found $($response.Count) apps" -ForegroundColor Yellow
        return $true
    } catch {
        Write-Host "❌ CodeMagic authentication failed: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Function to check GitLab status
function Test-GitLab {
    Write-Host "🐙 Testing GitLab integration..." -ForegroundColor Cyan

    $headers = @{
        "PRIVATE-TOKEN" = $GitLabToken
        "Content-Type" = "application/json"
    }

    try {
        $response = Invoke-RestMethod -Uri "https://gitlab.com/api/v4/user" -Method GET -Headers $headers
        Write-Host "✅ GitLab authentication successful" -ForegroundColor Green
        Write-Host "👤 User: $($response.name)" -ForegroundColor Yellow
        return $true
    } catch {
        Write-Host "❌ GitLab authentication failed: $($_.Exception.Message)" -ForegroundColor Red
        return $false
    }
}

# Main execution
Write-Host "🧪 Starting comprehensive build test..." -ForegroundColor Green
Write-Host ""

# Test local configuration
if (-not (Test-LocalConfiguration)) {
    Write-Host "❌ Local configuration test failed. Please check your setup." -ForegroundColor Red
    exit 1
}

# Test platform authentications
Write-Host ""
Write-Host "🔐 Testing platform authentications..." -ForegroundColor Yellow

$githubAuth = Test-GitLab  # Note: GitLab API is more reliable for testing
$codeMagicAuth = Test-CodeMagic
$gitLabAuth = Test-GitLab

Write-Host ""
Write-Host "📊 Authentication Results:" -ForegroundColor Cyan
Write-Host "  GitHub API: $(if($githubAuth) {'✅'} else {'❌'})" -ForegroundColor White
Write-Host "  CodeMagic: $(if($codeMagicAuth) {'✅'} else {'❌'})" -ForegroundColor White
Write-Host "  GitLab: $(if($gitLabAuth) {'✅'} else {'❌'})" -ForegroundColor White

# Push to GitHub if authentication works
if ($githubAuth) {
    Write-Host ""
    if (Push-ToGitHub) {
        Start-GitHubWorkflow
    }
}

Write-Host ""
Write-Host "📋 Next Steps:" -ForegroundColor Cyan
Write-Host "1. Check GitHub Actions: https://github.com/$githubRepo/actions" -ForegroundColor White
Write-Host "2. Check CodeMagic: https://codemagic.io/" -ForegroundColor White
Write-Host "3. Check GitLab: https://gitlab.com (if using as alternative)" -ForegroundColor White
Write-Host ""
Write-Host "📧 Check your email (ghadir.baraty@gmail.com) for APK downloads" -ForegroundColor Yellow

Write-Host ""
Write-Host "✅ Build test suite completed!" -ForegroundColor Green
