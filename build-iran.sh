#!/bin/bash

# Persian AI Assistant - Iran Network Build Script
# This script handles Iran network restrictions and downloads dependencies

echo "🇮🇷 Persian AI Assistant - Iran Network Build"
echo "=" * 60

# Set proxy and mirror configurations for Iran
export GRADLE_OPTS="-Dorg.gradle.daemon=true -Dorg.gradle.parallel=true -Dorg.gradle.jvmargs=-Xmx4g"

# Setup proxy if available (common Iranian proxies)
# export http_proxy=http://127.0.0.1:8080
# export https_proxy=http://127.0.0.1:8080

echo "📊 Setting up build environment for Iran..."

# Create init.gradle for repository mirrors
cat > init.gradle << 'EOF'
allprojects {
    repositories {
        // Use Iranian mirrors and proxies
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
        // Fallback to default repos
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    
    // Apply to all configurations
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
EOF

echo "✅ Repository mirrors configured for Iran"

# Update gradle.properties with Iran-optimized settings
cat > gradle.properties << 'EOF'
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
EOF

echo "✅ Gradle properties optimized for Iran network"

# Create a simple build.gradle for testing
cat > app/build-iran.gradle << 'EOF'
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
    // Core Android dependencies - minimal for testing
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0' 
    implementation 'androidx.activity:activity-compose:1.8.2'
    
    // Compose BOM - use specific version to avoid resolution issues
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
EOF

echo "✅ Simplified build.gradle created for Iran network"

# Check if Android SDK is available
if [ ! -d "$ANDROID_HOME" ]; then
    echo "⚠️ ANDROID_HOME not set. Trying to detect..."
    
    # Common Android SDK locations in Windows
    if [ -d "/c/Users/$USERNAME/AppData/Local/Android/Sdk" ]; then
        export ANDROID_HOME="/c/Users/$USERNAME/AppData/Local/Android/Sdk"
        echo "✅ Found Android SDK at: $ANDROID_HOME"
    elif [ -d "/c/Android/Sdk" ]; then
        export ANDROID_HOME="/c/Android/Sdk"
        echo "✅ Found Android SDK at: $ANDROID_HOME"
    else
        echo "❌ Android SDK not found. Please install Android Studio first."
        exit 1
    fi
fi

# Create local.properties
echo "sdk.dir=${ANDROID_HOME}" > local.properties
echo "✅ local.properties created"

echo "🔧 Starting build process..."

# Make gradlew executable
chmod +x ./gradlew

# Clean first
echo "🧹 Cleaning project..."
./gradlew clean --init-script=init.gradle

# Try simple build first
echo "🔨 Building with Iran-optimized settings..."
./gradlew assembleDebug --init-script=init.gradle --stacktrace --info

# Check if APK was created
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo "🎉 SUCCESS! APK built successfully!"
    echo "📱 APK Location: app/build/outputs/apk/debug/app-debug.apk"
    
    # Show APK info
    APK_SIZE=$(du -h "app/build/outputs/apk/debug/app-debug.apk" | cut -f1)
    echo "📊 APK Size: $APK_SIZE"
    echo "✅ Persian AI Assistant APK is ready for installation!"
    
else
    echo "❌ Build failed. Trying alternative approach..."
    
    # Try with alternative build file
    echo "🔄 Trying with simplified build configuration..."
    cp app/build-iran.gradle app/build.gradle
    
    ./gradlew clean --init-script=init.gradle
    ./gradlew assembleDebug --init-script=init.gradle --stacktrace
    
    if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
        echo "🎉 SUCCESS with alternative config!"
        echo "📱 APK ready: app/build/outputs/apk/debug/app-debug.apk"
    else
        echo "❌ Build still failed. Check logs above for details."
        echo "🔍 Most likely cause: Network restrictions or missing dependencies"
        exit 1
    fi
fi

echo "=" * 60
echo "🏁 Persian AI Assistant build completed!"
echo "📱 Install the APK: app/build/outputs/apk/debug/app-debug.apk"
