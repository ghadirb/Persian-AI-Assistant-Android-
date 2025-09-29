#!/usr/bin/env python3
"""
Persian AI Assistant - Direct Local Build
This script will build the app locally right now using system's Gradle
"""

import os
import subprocess
import sys
import time

def run_command(command, cwd=None):
    """Run a command and return the result"""
    print(f"🔄 Running: {command}")
    try:
        process = subprocess.Popen(
            command,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            cwd=cwd,
            universal_newlines=True
        )
        
        # Print output in real-time
        while True:
            output = process.stdout.readline()
            if output == '' and process.poll() is not None:
                break
            if output:
                print(output.strip())
                
        return process.returncode == 0
        
    except Exception as e:
        print(f"❌ Error running command: {e}")
        return False

def main():
    print("🚀 Persian AI Assistant - Direct Local Build")
    print("=" * 60)
    
    # Set working directory
    project_dir = r"C:\Users\Admin\Downloads\Compressed\PersianAIAssistantAndroid-main"
    
    if not os.path.exists(project_dir):
        print(f"❌ Project directory not found: {project_dir}")
        return False
    
    os.chdir(project_dir)
    print(f"📁 Working in: {os.getcwd()}")
    
    # Check for gradlew
    gradlew_path = "gradlew.bat" if os.name == 'nt' else "./gradlew"
    if not os.path.exists(gradlew_path):
        print(f"❌ {gradlew_path} not found!")
        return False
    
    print(f"✅ Found {gradlew_path}")
    
    # Set up environment
    android_home = None
    possible_sdk_paths = [
        os.path.expanduser(r"~\AppData\Local\Android\Sdk"),
        r"C:\Android\Sdk",
        os.environ.get('ANDROID_HOME', '')
    ]
    
    for path in possible_sdk_paths:
        if path and os.path.exists(path):
            android_home = path
            os.environ['ANDROID_HOME'] = android_home
            print(f"✅ Using Android SDK: {android_home}")
            break
    
    if not android_home:
        print("❌ Android SDK not found!")
        return False
    
    # Create local.properties
    with open('local.properties', 'w') as f:
        f.write(f'sdk.dir={android_home.replace(os.sep, "/")}\n')
    print("✅ Created local.properties")
    
    # Create Iran-optimized init.gradle
    init_gradle_content = """
allprojects {
    repositories {
        maven { url 'https://maven.aliyun.com/repository/google' }
        maven { url 'https://maven.aliyun.com/repository/central' }
        maven { url 'https://repo1.maven.org/maven2/' }
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
"""
    
    with open('init.gradle', 'w') as f:
        f.write(init_gradle_content)
    print("✅ Created init.gradle with Iran-friendly mirrors")
    
    # Update gradle.properties for Iran
    gradle_props_content = """
# Persian AI Assistant - Iran Network Optimized
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.daemon=true
org.gradle.caching=false

# AndroidX
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
android.nonTransitiveRClass=true

# Performance optimizations
kapt.use.worker.api=false
kapt.incremental.apt=false
org.gradle.configuration-cache=false
"""
    
    with open('gradle.properties', 'w') as f:
        f.write(gradle_props_content)
    print("✅ Updated gradle.properties")
    
    print("\n🧹 Step 1: Cleaning project...")
    if run_command(f"{gradlew_path} clean --init-script=init.gradle --no-daemon"):
        print("✅ Clean successful")
    else:
        print("⚠️ Clean failed, trying without init script...")
        if not run_command(f"{gradlew_path} clean --no-daemon"):
            print("❌ Clean failed completely")
            return False
    
    print("\n🔨 Step 2: Building APK...")
    print("This may take several minutes...")
    
    # Try with init script first
    if run_command(f"{gradlew_path} assembleDebug --init-script=init.gradle --stacktrace --no-daemon"):
        print("✅ Build with init script successful!")
    else:
        print("⚠️ Build with init script failed, trying direct...")
        if not run_command(f"{gradlew_path} assembleDebug --stacktrace --no-daemon"):
            print("❌ Build failed!")
            return False
    
    # Check if APK exists
    apk_path = r"app\build\outputs\apk\debug\app-debug.apk"
    if os.path.exists(apk_path):
        apk_size = os.path.getsize(apk_path) / (1024 * 1024)  # Size in MB
        
        print("\n" + "=" * 60)
        print("🎉 BUILD SUCCESSFUL!")
        print("=" * 60)
        print(f"📱 APK Location: {os.path.abspath(apk_path)}")
        print(f"📊 APK Size: {apk_size:.2f} MB")
        print("\n✅ Persian AI Assistant APK is ready!")
        print("\n🚀 Next Steps:")
        print("   1. Copy the APK to your Android device")
        print("   2. Enable 'Unknown Sources' in Android settings")
        print("   3. Install and enjoy your Persian AI Assistant!")
        print("=" * 60)
        
        # Try to open the folder
        try:
            if os.name == 'nt':  # Windows
                subprocess.run(['explorer', os.path.dirname(os.path.abspath(apk_path))])
        except:
            pass
            
        return True
    else:
        print("\n❌ APK not found! Build may have failed.")
        return False

if __name__ == "__main__":
    success = main()
    
    if success:
        print("\n🏆 Persian AI Assistant build completed successfully!")
    else:
        print("\n💥 Build failed. Check the error messages above.")
        print("💡 Common solutions:")
        print("   - Make sure Android Studio is installed")
        print("   - Try connecting to VPN if in Iran")
        print("   - Check internet connection")
    
    input("\nPress Enter to exit...")
