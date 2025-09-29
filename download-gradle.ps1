# Download Gradle manually for Iranian users
$gradleUrl = "https://services.gradle.org/distributions/gradle-7.4.2-bin.zip"
$gradleDir = "$env:USERPROFILE\.gradle\wrapper\dists\gradle-7.4.2-bin"
$gradleZip = "$env:TEMP\gradle-7.4.2-bin.zip"

Write-Host "Creating Gradle directory..."
New-Item -ItemType Directory -Force -Path $gradleDir

Write-Host "Downloading Gradle 7.4.2..."
try {
    Invoke-WebRequest -Uri $gradleUrl -OutFile $gradleZip -UseBasicParsing
    Write-Host "Download completed successfully!"
    
    Write-Host "Extracting Gradle..."
    Expand-Archive -Path $gradleZip -DestinationPath $gradleDir -Force
    
    Write-Host "Cleaning up..."
    Remove-Item $gradleZip -Force
    
    Write-Host "Gradle 7.4.2 installed successfully!"
} catch {
    Write-Host "Download failed: $($_.Exception.Message)"
    Write-Host "Please download manually from: $gradleUrl"
    Write-Host "Extract to: $gradleDir"
}
