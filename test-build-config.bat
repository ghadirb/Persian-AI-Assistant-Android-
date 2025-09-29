@echo off
echo === Persian AI Assistant Build Test ===
echo Testing the fixed build configuration...
echo.

REM Check if we're in the right directory
if not exist "app\build.gradle" (
    echo ❌ Error: app\build.gradle not found. Please run this script from the project root.
    pause
    exit /b 1
)

REM Check gradlew
if not exist "gradlew.bat" (
    echo ❌ Error: gradlew.bat not found
    pause
    exit /b 1
)

echo ✅ Project structure looks good
echo.

REM Check for Hilt version consistency
echo Checking Hilt version consistency...
findstr "hilt-navigation-compose:1.1.0" app\build.gradle > nul
if %errorlevel% equ 0 (
    echo ❌ Error: Found hardcoded Hilt version 1.1.0
    echo This should be using the variable hilt_version (2.48)
    pause
    exit /b 1
) else (
    echo ✅ Hilt versions are consistent
)
echo.

REM Check for missing version variables
echo Checking for missing version variables...
set MISSING_COUNT=0

findstr "biometric_version" build.gradle > nul
if %errorlevel% neq 0 (
    set /a MISSING_COUNT+=1
    echo - biometric_version
)

findstr "junit_version" build.gradle > nul
if %errorlevel% neq 0 (
    set /a MISSING_COUNT+=1
    echo - junit_version
)

findstr "test_junit_version" build.gradle > nul
if %errorlevel% neq 0 (
    set /a MISSING_COUNT+=1
    echo - test_junit_version
)

findstr "espresso_version" build.gradle > nul
if %errorlevel% neq 0 (
    set /a MISSING_COUNT+=1
    echo - espresso_version
)

if %MISSING_COUNT% gtr 0 (
    echo ❌ Error: Missing %MISSING_COUNT% version variables
    pause
    exit /b 1
) else (
    echo ✅ All version variables are present
)
echo.

echo === Configuration Test Passed! ===
echo The build configuration should now work correctly.
echo.
echo To test with your tokens:
echo 1. Push changes to GitHub (if not already pushed)
echo 2. Go to GitHub Actions and run the 'fixed-build.yml' workflow
echo 3. Check CodeMagic with your token
echo 4. If issues persist, try GitLab as alternative
echo.
echo GitHub token: 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
echo CodeMagic token: YOUR_CODEMAGIC_TOKEN_HERE
echo GitLab token: glpat-dvf4yo4ZV6rDSGXFGExA
echo.
pause
