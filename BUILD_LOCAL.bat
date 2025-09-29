@echo off
title Persian AI Assistant - Iran Local Build

echo.
echo ==============================================
echo 🇮🇷 Persian AI Assistant - Iran Local Build
echo ==============================================
echo.

cd /d "C:\Users\Admin\Downloads\Compressed\PersianAIAssistantAndroid-main"

echo 📍 Current directory: %CD%
echo.

echo 🔧 Starting local build process...
echo.

REM Check if gradlew.bat exists
if not exist "gradlew.bat" (
    echo ❌ gradlew.bat not found!
    echo Please make sure you're in the correct project directory.
    pause
    exit /b 1
)

echo ✅ Found gradlew.bat
echo.

REM Set Android SDK path
if exist "%USERPROFILE%\AppData\Local\Android\Sdk" (
    set ANDROID_HOME=%USERPROFILE%\AppData\Local\Android\Sdk
    echo ✅ Android SDK found: %ANDROID_HOME%
) else if exist "C:\Android\Sdk" (
    set ANDROID_HOME=C:\Android\Sdk
    echo ✅ Android SDK found: %ANDROID_HOME%
) else (
    echo ❌ Android SDK not found. Please install Android Studio.
    pause
    exit /b 1
)

REM Create local.properties
echo sdk.dir=%ANDROID_HOME:\=/% > local.properties
echo ✅ local.properties created
echo.

echo 🧹 Cleaning project...
call gradlew.bat clean --no-daemon --offline

if errorlevel 1 (
    echo ⚠️ Clean failed, trying online...
    call gradlew.bat clean --no-daemon
)

echo.
echo 🔨 Building APK (this may take several minutes)...
echo Please wait...
echo.

REM Try building with offline mode first (uses cached dependencies)
call gradlew.bat assembleDebug --no-daemon --offline --stacktrace

if errorlevel 1 (
    echo.
    echo ⚠️ Offline build failed, trying online build...
    echo This will download dependencies from internet...
    echo.
    
    call gradlew.bat assembleDebug --no-daemon --stacktrace
)

echo.
echo 📱 Checking for APK...

if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo ========================================
    echo 🎉 SUCCESS! APK Built Successfully!
    echo ========================================
    echo.
    echo 📱 APK Location: app\build\outputs\apk\debug\app-debug.apk
    
    for %%I in ("app\build\outputs\apk\debug\app-debug.apk") do (
        set /a size=%%~zI/1048576
        echo 📊 APK Size: !size! MB
    )
    
    echo.
    echo ✅ Persian AI Assistant is ready for installation!
    echo.
    echo 🚀 Next Steps:
    echo    1. Copy app-debug.apk to your Android phone
    echo    2. Enable "Install from Unknown Sources" in Android Settings
    echo    3. Install the APK
    echo    4. Enjoy your Persian AI Assistant!
    echo.
    echo 📂 Opening APK folder...
    explorer "app\build\outputs\apk\debug"
    
) else (
    echo.
    echo ❌ Build failed! APK not found.
    echo.
    echo 🔍 Possible causes:
    echo    - Network connectivity issues (Iran filtering)
    echo    - Missing Android SDK components
    echo    - Dependency download problems
    echo.
    echo 💡 Try:
    echo    - Connect to VPN
    echo    - Check Android Studio SDK Manager
    echo    - Run Android Studio sync first
)

echo.
echo ========================================
echo 🏁 Build process completed
echo ========================================
pause
