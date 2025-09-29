# Persian AI Assistant - Iran Network Build Script (PowerShell)
# This script handles Iran network restrictions and builds locally

Write-Host "🇮🇷 Persian AI Assistant - Iran Network Build" -ForegroundColor Green
Write-Host "=" * 60 -ForegroundColor Green

Write-Host "📊 Setting up build environment for Iran network..." -ForegroundColor Cyan

# Set environment variables
$env:GRADLE_OPTS = "-Dorg.gradle.daemon=true -Dorg.gradle.parallel=true -Dorg.gradle.jvmargs=-Xmx4g -Duser.country=IR -Duser.language=fa"

# Function to create init.gradle for Iran mirrors
function Create-InitGradle {
    Write-Host "Creating repository mirrors for Iran..." -ForegroundColor Yellow
    
    $initGradleContent = @"
allprojects {
    repositories {
        // Iranian-friendly mirrors - Aliyun (China) usually works
        maven { 
            url 'https://maven.aliyun.com/repository/google'
            name 'Aliyun Google'
        }
        maven { 
            url 'https://maven.aliyun.com/repository/central'  
            name 'Aliyun Central'
        }
        maven {
            url 'https://repo1.maven.org/maven2/'
            name 'Maven Central Direct'
        }
        maven {
            url 'https://oss.sonatype.org/content/repositories/snapshots/'
            name 'Sonatype Snapshots'
        }
        // Fallback to default (may be blocked)
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
        resolutionStrategy.cacheDynamicVersionsFor 0, 'seconds'
    }
}

gradle.projectsEvaluated {
    tasks.withType(JavaCompile) {
        options.compilerArgs << "-Xlint:unchecked" << "-Xlint:deprecation"
    }
}
"@

    $initGradleContent | Out-File -FilePath "init.gradle" -Encoding UTF8
    Write-Host "✅ Repository mirrors configured" -ForegroundColor Green
}

# Function to create optimized gradle.properties
function Create-GradleProperties {
    Write-Host "Creating optimized gradle.properties..." -ForegroundColor Yellow
    
    $gradlePropsContent = @"
# Persian AI Assistant - Iran Network Optimized
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8 -Duser.country=IR -Duser.language=fa
org.gradle.parallel=true
org.gradle.daemon=true
org.gradle.caching=true
org.gradle.configureondemand=true

# Network optimizations for Iran
systemProp.http.keepAlive=true
systemProp.http.maxConnections=10
systemProp.http.maxRedirects=3

# AndroidX
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
android.nonTransitiveRClass=true

# Performance optimizations  
kapt.use.worker.api=true
kapt.incremental.apt=true
kapt.include.compile.classpath=false

# Build optimizations
org.gradle.unsafe.configuration-cache=false
org.gradle.configuration-cache=false
"@

    $gradlePropsContent | Out-File -FilePath "gradle.properties" -Encoding UTF8
    Write-Host "✅ Gradle properties optimized" -ForegroundColor Green
}

# Function to detect Android SDK
function Find-AndroidSDK {
    Write-Host "🔍 Detecting Android SDK..." -ForegroundColor Yellow
    
    $possiblePaths = @(
        "$env:USERPROFILE\AppData\Local\Android\Sdk",
        "C:\Android\Sdk",
        "$env:ANDROID_HOME"
    )
    
    foreach ($path in $possiblePaths) {
        if (Test-Path $path) {
            $env:ANDROID_HOME = $path
            Write-Host "✅ Found Android SDK at: $path" -ForegroundColor Green
            return $true
        }
    }
    
    Write-Host "❌ Android SDK not found. Please install Android Studio first." -ForegroundColor Red
    return $false
}

# Function to create simplified build.gradle for Iran
function Create-SimpleBuildGradle {
    Write-Host "🔄 Creating simplified build configuration..." -ForegroundColor Yellow
    
    $simpleBuildContent = @"
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
}

android {
    namespace 'com.example.persianaiapp'
    compileSdk 34

    defaultConfig {
        applicationId "com.example.persianaiapp"
        minSdk 26
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
        
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix ".debug"
            debuggable true
            minifyEnabled false
        }
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    
    kotlinOptions {
        jvmTarget = '17'
    }
    
    buildFeatures {
        compose true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion '1.5.8'
    }
    
    packaging {
        resources {
            excludes += '/META-INF/{AL2.0,LGPL2.1}'
        }
    }
}

dependencies {
    // Minimal dependencies for testing
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0' 
    implementation 'androidx.activity:activity-compose:1.8.2'
    
    // Compose BOM
    implementation platform('androidx.compose:compose-bom:2024.02.00')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    
    // Test dependencies
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation platform('androidx.compose:compose-bom:2024.02.00')
    androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
    debugImplementation 'androidx.compose.ui:ui-tooling'
    debugImplementation 'androidx.compose.ui:ui-test-manifest'
}
"@

    $simpleBuildContent | Out-File -FilePath "app\build-simple.gradle" -Encoding UTF8
    Write-Host "✅ Simplified build.gradle created" -ForegroundColor Green
}

# Main build function
function Start-Build {
    param([switch]$UseSimple)
    
    Write-Host "🔧 Starting build process..." -ForegroundColor Cyan
    
    # Make sure gradlew is executable
    if (Test-Path ".\gradlew.bat") {
        Write-Host "✅ Found gradlew.bat" -ForegroundColor Green
    } else {
        Write-Host "❌ gradlew.bat not found!" -ForegroundColor Red
        return $false
    }
    
    # Clean first
    Write-Host "🧹 Cleaning project..." -ForegroundColor Yellow
    $cleanResult = & .\gradlew.bat clean --init-script=init.gradle --no-daemon 2>&1
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "⚠️ Clean with init script failed, trying without..." -ForegroundColor Yellow
        & .\gradlew.bat clean --no-daemon
    }
    
    # Build APK
    Write-Host "🔨 Building APK..." -ForegroundColor Yellow
    if ($UseSimple) {
        Write-Host "Using simplified configuration..." -ForegroundColor Cyan
        $buildResult = & .\gradlew.bat assembleDebug --init-script=init.gradle --stacktrace --no-daemon -Pandroid.injected.build.model.only.versioned=3 2>&1
    } else {
        $buildResult = & .\gradlew.bat assembleDebug --init-script=init.gradle --stacktrace --no-daemon 2>&1
    }
    
    # Check if APK was created
    $apkPath = "app\build\outputs\apk\debug\app-debug.apk"
    if (Test-Path $apkPath) {
        Write-Host "🎉 SUCCESS! APK built successfully!" -ForegroundColor Green
        Write-Host "📱 APK Location: $apkPath" -ForegroundColor Cyan
        
        # Show APK size
        $apkSize = (Get-Item $apkPath).Length / 1MB
        Write-Host "📊 APK Size: $($apkSize.ToString('F2')) MB" -ForegroundColor Cyan
        Write-Host "✅ Persian AI Assistant APK is ready for installation!" -ForegroundColor Green
        
        return $true
    } else {
        Write-Host "❌ APK not found at expected location" -ForegroundColor Red
        return $false
    }
}

# Main execution
try {
    # Setup
    Create-InitGradle
    Create-GradleProperties
    
    if (-not (Find-AndroidSDK)) {
        Read-Host "Press Enter to exit..."
        exit 1
    }
    
    # Create local.properties
    "sdk.dir=$($env:ANDROID_HOME -replace '\\', '/')" | Out-File -FilePath "local.properties" -Encoding UTF8
    Write-Host "✅ local.properties created" -ForegroundColor Green
    
    # Try normal build first
    Write-Host "`n🚀 Attempting normal build..." -ForegroundColor Green
    if (Start-Build) {
        Write-Host "`n🎉 Normal build succeeded!" -ForegroundColor Green
    } else {
        # Try with simplified build
        Write-Host "`n🔄 Normal build failed, trying simplified version..." -ForegroundColor Yellow
        Create-SimpleBuildGradle
        
        # Backup original build.gradle
        if (Test-Path "app\build.gradle") {
            Copy-Item "app\build.gradle" "app\build.gradle.backup"
        }
        Copy-Item "app\build-simple.gradle" "app\build.gradle"
        
        if (Start-Build -UseSimple) {
            Write-Host "`n🎉 Simplified build succeeded!" -ForegroundColor Green
        } else {
            Write-Host "`n❌ Both build attempts failed" -ForegroundColor Red
            Write-Host "🔍 This is likely due to:" -ForegroundColor Yellow
            Write-Host "   - Network restrictions (Iran filtering)" -ForegroundColor Yellow  
            Write-Host "   - Missing dependencies" -ForegroundColor Yellow
            Write-Host "   - Proxy/VPN needed" -ForegroundColor Yellow
            Read-Host "Press Enter to exit..."
            exit 1
        }
    }
    
    # Success message
    Write-Host "`n" + "=" * 60 -ForegroundColor Green
    Write-Host "🏁 Persian AI Assistant build completed successfully!" -ForegroundColor Green
    Write-Host "📱 Your APK is ready: app\build\outputs\apk\debug\app-debug.apk" -ForegroundColor Cyan
    Write-Host "" -ForegroundColor Green
    Write-Host "🚀 Next steps:" -ForegroundColor Cyan
    Write-Host "   1. Copy the APK to your Android device" -ForegroundColor White
    Write-Host "   2. Enable 'Install from Unknown Sources'" -ForegroundColor White
    Write-Host "   3. Install and enjoy your Persian AI Assistant!" -ForegroundColor White
    Write-Host "=" * 60 -ForegroundColor Green
    
} catch {
    Write-Host "`n❌ Unexpected error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host $_.ScriptStackTrace -ForegroundColor Red
    Read-Host "Press Enter to exit..."
    exit 1
}

Read-Host "`nPress Enter to exit..."
