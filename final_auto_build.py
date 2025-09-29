import os
import subprocess
import sys
import time
import shutil

def main():
    print("🚀 Persian AI Assistant - خودکار تا بیلد نهایی")
    print("=" * 60)
    
    # تنظیم مسیر
    project_dir = r"C:\Users\Admin\Downloads\Compressed\PersianAIAssistantAndroid-main"
    if not os.path.exists(project_dir):
        print(f"❌ پوشه پروژه یافت نشد: {project_dir}")
        return False
    
    os.chdir(project_dir)
    print(f"📁 مسیر کاری: {os.getcwd()}")
    
    # تشخیص Android SDK
    sdk_paths = [
        os.path.expanduser(r"~\AppData\Local\Android\Sdk"),
        r"C:\Android\Sdk",
        os.environ.get('ANDROID_HOME', '')
    ]
    
    android_home = None
    for path in sdk_paths:
        if path and os.path.exists(path):
            android_home = path
            os.environ['ANDROID_HOME'] = android_home
            break
    
    if not android_home:
        print("❌ Android SDK یافت نشد! Android Studio نصب کنید.")
        return False
    
    print(f"✅ Android SDK: {android_home}")
    
    # ایجاد local.properties
    with open('local.properties', 'w', encoding='utf-8') as f:
        f.write(f'sdk.dir={android_home.replace(os.sep, "/")}\n')
    print("✅ local.properties ایجاد شد")
    
    # init.gradle برای دور زدن تحریم
    init_content = '''allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/central' }
        maven { url 'https://repo1.maven.org/maven2/' }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}'''
    
    with open('init.gradle', 'w', encoding='utf-8') as f:
        f.write(init_content)
    print("✅ init.gradle برای ایران")
    
    # gradle.properties بهینه
    gradle_content = '''org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.daemon=true
org.gradle.caching=false
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
android.nonTransitiveRClass=true
org.gradle.configuration-cache=false'''
    
    with open('gradle.properties', 'w', encoding='utf-8') as f:
        f.write(gradle_content)
    print("✅ gradle.properties بهینه شد")
    
    # چک gradlew
    gradlew = "gradlew.bat" if os.name == 'nt' else "./gradlew"
    if not os.path.exists(gradlew):
        print(f"❌ {gradlew} یافت نشد!")
        return False
    
    print(f"✅ {gradlew} موجود است")
    
    def run_cmd(cmd):
        print(f"🔄 {cmd}")
        try:
            result = subprocess.run(cmd, shell=True, text=True, 
                                  capture_output=True, timeout=1200)
            if result.returncode == 0:
                print("✅ موفق")
                return True
            else:
                print(f"❌ خطا: {result.stderr[-200:] if result.stderr else 'ناشناخته'}")
                return False
        except subprocess.TimeoutExpired:
            print("⏰ timeout")
            return False
        except Exception as e:
            print(f"❌ {e}")
            return False
    
    # مرحله 1: پاکسازی
    print("\n🧹 پاکسازی...")
    clean_success = run_cmd(f"{gradlew} clean --init-script=init.gradle --no-daemon")
    if not clean_success:
        print("تلاش دوباره بدون init...")
        run_cmd(f"{gradlew} clean --no-daemon")
    
    # مرحله 2: build کامل
    print("\n🔨 build کامل...")
    build_success = run_cmd(f"{gradlew} assembleDebug --init-script=init.gradle --no-daemon --stacktrace")
    
    if not build_success:
        print("\n🔄 تلاش با build ساده...")
        
        # backup و ساده‌سازی build.gradle
        if os.path.exists('app/build.gradle'):
            shutil.copy('app/build.gradle', 'app/build.gradle.backup')
        
        simple_build = '''plugins {
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
}'''
        
        with open('app/build.gradle', 'w', encoding='utf-8') as f:
            f.write(simple_build)
        print("✅ build.gradle ساده شد")
        
        # پاک و build مجدد
        run_cmd(f"{gradlew} clean --no-daemon")
        build_success = run_cmd(f"{gradlew} assembleDebug --init-script=init.gradle --no-daemon")
        
        if not build_success:
            print("تلاش نهایی بدون init...")
            build_success = run_cmd(f"{gradlew} assembleDebug --no-daemon")
    
    # بررسی APK
    apk_path = "app/build/outputs/apk/debug/app-debug.apk"
    if os.path.exists(apk_path):
        size = os.path.getsize(apk_path) / (1024*1024)
        abs_path = os.path.abspath(apk_path)
        
        print("\n" + "="*60)
        print("🎉 BUILD موفقیت‌آمیز!")
        print("="*60)
        print(f"📱 APK: {abs_path}")
        print(f"📊 حجم: {size:.2f} MB")
        print("✅ Persian AI Assistant آماده!")
        print("="*60)
        
        # باز کردن پوشه
        try:
            if os.name == 'nt':
                subprocess.run(['explorer', os.path.dirname(abs_path)], check=False)
        except:
            pass
        
        return True
    else:
        print("\n❌ APK ایجاد نشد! Build ناموفق")
        return False

if __name__ == "__main__":
    success = main()
    if success:
        print("\n🏆 فرآیند کامل!")
    else:
        print("\n💥 مشکل در build")
