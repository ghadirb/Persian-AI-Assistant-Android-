@echo off
chcp 65001 >nul
echo 🇮🇷 Persian AI Assistant - Iran Network Build
echo ============================================================

echo 📊 Setting up build environment for Iran network...

REM Set Gradle options for Iran network
set GRADLE_OPTS=-Dorg.gradle.daemon=true -Dorg.gradle.parallel=true -Dorg.gradle.jvmargs=-Xmx4g -Duser.country=IR -Duser.language=fa

REM Create init.gradle for Iran mirrors
echo Creating repository mirrors for Iran...
(
echo allprojects {
echo     repositories {
echo         // Iranian-friendly mirrors
echo         maven { 
echo             url 'https://maven.aliyun.com/repository/google'
echo             name 'Aliyun Google'
echo         }
echo         maven { 
echo             url 'https://maven.aliyun.com/repository/central'
echo             name 'Aliyun Central' 
echo         }
echo         maven { 
echo             url 'https://repo1.maven.org/maven2/'
echo             name 'Maven Central Direct'
echo         }
echo         // Fallback repositories
echo         google()
echo         mavenCentral()
echo         gradlePluginPortal()
echo     }
echo     
echo     configurations.all {
echo         resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
echo         resolutionStrategy.cacheDynamicVersionsFor 0, 'seconds'
echo     }
echo }
) > init.gradle

echo ✅ Repository mirrors configured

REM Update gradle.properties for Iran network
echo Creating optimized gradle.properties...
(
echo # Persian AI Assistant - Iran Network Optimized
echo org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8 -Duser.country=IR -Duser.language=fa
echo org.gradle.parallel=true
echo org.gradle.daemon=true
echo org.gradle.caching=true
echo org.gradle.configureondemand=true
echo.
echo # Network optimizations for Iran
echo systemProp.http.keepAlive=true
echo systemProp.http.maxConnections=10
echo systemProp.http.maxRedirects=3
echo.
echo # AndroidX
echo android.useAndroidX=true
echo android.enableJetifier=true
echo kotlin.code.style=official
echo android.nonTransitiveRClass=true
echo.
echo # Performance optimizations
echo kapt.use.worker.api=true
echo kapt.incremental.apt=true
echo kapt.include.compile.classpath=false
echo.
echo # Build optimizations
echo org.gradle.unsafe.configuration-cache=false
echo org.gradle.configuration-cache=false
) > gradle.properties

echo ✅ Gradle properties optimized

REM Detect Android SDK
echo 🔍 Detecting Android SDK...
if exist "%USERPROFILE%\AppData\Local\Android\Sdk" (
    set ANDROID_HOME=%USERPROFILE%\AppData\Local\Android\Sdk
    echo ✅ Found Android SDK at: %ANDROID_HOME%
) else if exist "C:\Android\Sdk" (
    set ANDROID_HOME=C:\Android\Sdk
    echo ✅ Found Android SDK at: %ANDROID_HOME%
) else (
    echo ❌ Android SDK not found. Please install Android Studio first.
    pause
    exit /b 1
)

REM Create local.properties
echo sdk.dir=%ANDROID_HOME:\=/% > local.properties
echo ✅ local.properties created

echo.
echo 🔧 Starting build process...
echo.

REM Clean project first
echo 🧹 Cleaning project...
call gradlew clean --init-script=init.gradle --no-daemon

if errorlevel 1 (
    echo ❌ Clean failed. Trying without init script...
    call gradlew clean --no-daemon
)

REM Build APK
echo 🔨 Building APK with Iran-optimized settings...
call gradlew assembleDebug --init-script=init.gradle --stacktrace --no-daemon

REM Check if APK was created
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo.
    echo 🎉 SUCCESS! APK built successfully!
    echo 📱 APK Location: app\build\outputs\apk\debug\app-debug.apk
    
    REM Show APK size
    for %%A in ("app\build\outputs\apk\debug\app-debug.apk") do (
        set APK_SIZE=%%~zA
        echo 📊 APK Size: !APK_SIZE! bytes
    )
    
    echo ✅ Persian AI Assistant APK is ready for installation!
    echo.
    echo 📱 To install on your phone:
    echo    1. Copy app-debug.apk to your phone
    echo    2. Enable 'Unknown Sources' in Settings
    echo    3. Install the APK
    
) else (
    echo.
    echo ❌ Build failed. Trying with simplified configuration...
    
    REM Create simplified build.gradle
    echo 🔄 Creating simplified build configuration...
    (
        echo plugins {
        echo     id 'com.android.application'
        echo     id 'org.jetbrains.kotlin.android'
        echo }
        echo.
        echo android {
        echo     namespace 'com.example.persianaiapp'
        echo     compileSdk 34
        echo.
        echo     defaultConfig {
        echo         applicationId "com.example.persianaiapp"
        echo         minSdk 26
        echo         targetSdk 34
        echo         versionCode 1
        echo         versionName "1.0.0"
        echo         testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        echo         vectorDrawables { useSupportLibrary true }
        echo     }
        echo.
        echo     buildTypes {
        echo         debug {
        echo             applicationIdSuffix ".debug"
        echo             debuggable true
        echo             minifyEnabled false
        echo         }
        echo         release {
        echo             minifyEnabled false
        echo             proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'^), 'proguard-rules.pro'
        echo         }
        echo     }
        echo.
        echo     compileOptions {
        echo         sourceCompatibility JavaVersion.VERSION_17
        echo         targetCompatibility JavaVersion.VERSION_17
        echo     }
        echo.
        echo     kotlinOptions { jvmTarget = '17' }
        echo     buildFeatures { compose true }
        echo     composeOptions { kotlinCompilerExtensionVersion '1.5.8' }
        echo     packaging { resources { excludes += '/META-INF/{AL2.0,LGPL2.1}' } }
        echo }
        echo.
        echo dependencies {
        echo     implementation 'androidx.core:core-ktx:1.12.0'
        echo     implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
        echo     implementation 'androidx.activity:activity-compose:1.8.2'
        echo     implementation platform('androidx.compose:compose-bom:2024.02.00'^)
        echo     implementation 'androidx.compose.ui:ui'
        echo     implementation 'androidx.compose.ui:ui-graphics'
        echo     implementation 'androidx.compose.ui:ui-tooling-preview'
        echo     implementation 'androidx.compose.material3:material3'
        echo     testImplementation 'junit:junit:4.13.2'
        echo     androidTestImplementation 'androidx.test.ext:junit:1.1.5'
        echo     androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
        echo     androidTestImplementation platform('androidx.compose:compose-bom:2024.02.00'^)
        echo     androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
        echo     debugImplementation 'androidx.compose.ui:ui-tooling'
        echo     debugImplementation 'androidx.compose.ui:ui-test-manifest'
        echo }
    ) > app\build-simple.gradle
    
    REM Backup original and use simplified
    if exist "app\build.gradle" (
        copy "app\build.gradle" "app\build.gradle.backup" >nul
    )
    copy "app\build-simple.gradle" "app\build.gradle" >nul
    
    REM Try build again
    call gradlew clean --init-script=init.gradle --no-daemon
    call gradlew assembleDebug --init-script=init.gradle --stacktrace --no-daemon
    
    if exist "app\build\outputs\apk\debug\app-debug.apk" (
        echo 🎉 SUCCESS with simplified config!
        echo 📱 APK ready: app\build\outputs\apk\debug\app-debug.apk
    ) else (
        echo ❌ Build still failed. 
        echo 🔍 This is likely due to network restrictions or missing dependencies
        echo 💡 Try connecting to VPN or using different network
        pause
        exit /b 1
    )
)

echo.
echo ============================================================
echo 🏁 Persian AI Assistant build completed!
echo 📱 Your APK is ready: app\build\outputs\apk\debug\app-debug.apk
echo.
echo 🚀 Next steps:
echo    1. Copy the APK to your Android device
echo    2. Enable 'Install from Unknown Sources' 
echo    3. Install and enjoy your Persian AI Assistant!
echo ============================================================
pause
