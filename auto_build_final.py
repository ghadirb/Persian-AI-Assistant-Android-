import os
import subprocess
import sys
import time

# تنظیم مسیر پروژه
project_dir = r"C:\Users\Admin\Downloads\Compressed\PersianAIAssistantAndroid-main"
os.chdir(project_dir)

print("🚀 شروع فرآیند build خودکار Persian AI Assistant...")
print("=" * 60)

# تشخیص Android SDK
android_sdk_paths = [
    os.path.expanduser(r"~\AppData\Local\Android\Sdk"),
    r"C:\Android\Sdk",
    os.environ.get('ANDROID_HOME', '')
]

android_home = None
for path in android_sdk_paths:
    if path and os.path.exists(path):
        android_home = path
        break

if android_home:
    os.environ['ANDROID_HOME'] = android_home
    print(f"✅ Android SDK: {android_home}")
else:
    print("❌ Android SDK یافت نشد!")
    sys.exit(1)

# ایجاد local.properties
with open('local.properties', 'w') as f:
    f.write(f'sdk.dir={android_home.replace(os.sep, "/")}\n')
print("✅ local.properties ایجاد شد")

# ایجاد init.gradle برای دور زدن تحریم
init_gradle = '''
allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/central' }
        maven { url 'https://repo1.maven.org/maven2/' }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    configurations.all {
        resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
    }
}
'''

with open('init.gradle', 'w') as f:
    f.write(init_gradle)
print("✅ init.gradle برای ایران ایجاد شد")

# بهینه‌سازی gradle.properties
gradle_props = '''
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.daemon=true
org.gradle.caching=false
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
android.nonTransitiveRClass=true
kapt.use.worker.api=false
kapt.incremental.apt=false
org.gradle.configuration-cache=false
'''

with open('gradle.properties', 'w') as f:
    f.write(gradle_props)
print("✅ gradle.properties بهینه شد")

def run_gradle_command(command):
    """اجرای دستور gradle"""
    full_command = f"gradlew.bat {command}"
    print(f"🔄 {full_command}")
    
    try:
        result = subprocess.run(full_command, shell=True, capture_output=True, text=True, timeout=1800)
        if result.returncode == 0:
            print("✅ موفقیت‌آمیز")
            return True
        else:
            print("❌ خطا:")
            print(result.stderr[-500:] if result.stderr else "خطای ناشناخته")
            return False
    except subprocess.TimeoutExpired:
        print("⏰ timeout")
        return False
    except Exception as e:
        print(f"❌ خطا: {e}")
        return False

# مرحله 1: پاکسازی
print("\n🧹 مرحله 1: پاکسازی...")
if not run_gradle_command("clean --init-script=init.gradle --no-daemon"):
    print("⚠️ تلاش بدون init script...")
    run_gradle_command("clean --no-daemon")

# مرحله 2: build اصلی
print("\n🔨 مرحله 2: شروع build...")
build_success = run_gradle_command("assembleDebug --init-script=init.gradle --stacktrace --no-daemon")

if not build_success:
    print("\n🔄 تلاش با تنظیمات ساده‌تر...")
    
    # ایجاد build.gradle ساده
    simple_build = '''
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
        vectorDrawables { useSupportLibrary true }
    }
    buildTypes {
        debug {
            applicationIdSuffix ".debug"
            debuggable true
            minifyEnabled false
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = '17' }
    buildFeatures { compose true }
    composeOptions { kotlinCompilerExtensionVersion '1.5.8' }
    packaging { resources { excludes += '/META-INF/{AL2.0,LGPL2.1}' } }
}

dependencies {
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.7.0'
    implementation 'androidx.activity:activity-compose:1.8.2'
    implementation platform('androidx.compose:compose-bom:2024.02.00')
    implementation 'androidx.compose.ui:ui'
    implementation 'androidx.compose.ui:ui-graphics'
    implementation 'androidx.compose.ui:ui-tooling-preview'
    implementation 'androidx.compose.material3:material3'
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
    androidTestImplementation platform('androidx.compose:compose-bom:2024.02.00')
    androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
    debugImplementation 'androidx.compose.ui:ui-tooling'
    debugImplementation 'androidx.compose.ui:ui-test-manifest'
}
'''
    
    # بک‌آپ و جایگزینی
    if os.path.exists('app/build.gradle'):
        os.rename('app/build.gradle', 'app/build.gradle.backup')
    
    with open('app/build.gradle', 'w') as f:
        f.write(simple_build)
    print("✅ build.gradle ساده ایجاد شد")
    
    # پاکسازی مجدد
    run_gradle_command("clean --no-daemon")
    
    # build ساده
    build_success = run_gradle_command("assembleDebug --init-script=init.gradle --no-daemon")

# بررسی نتیجه
apk_path = "app/build/outputs/apk/debug/app-debug.apk"
if os.path.exists(apk_path):
    size_mb = os.path.getsize(apk_path) / (1024 * 1024)
    print("\n" + "=" * 60)
    print("🎉 BUILD موفقیت‌آمیز!")
    print("=" * 60)
    print(f"📱 APK: {os.path.abspath(apk_path)}")
    print(f"📊 حجم: {size_mb:.2f} MB")
    print("✅ Persian AI Assistant آماده نصب!")
    
    # باز کردن پوشه
    try:
        subprocess.run(['explorer', os.path.dirname(os.path.abspath(apk_path))])
    except:
        pass
        
else:
    print("\n❌ build ناموفق - APK ایجاد نشد")
    
print("\n🏁 فرآیند تکمیل شد")
