#!/usr/bin/env python3
import subprocess
import sys
import os

print("🚀 Persian AI Assistant - Starting Direct Build Process...")
print("=" * 60)

# Change to project directory
project_dir = r"C:\Users\Admin\Downloads\Compressed\PersianAIAssistantAndroid-main"

try:
    os.chdir(project_dir)
    print(f"📁 Working directory: {os.getcwd()}")
    
    # Execute the direct build script
    result = subprocess.run([sys.executable, "direct_build.py"], 
                           capture_output=False, 
                           text=True)
    
    print(f"\n🏁 Build process completed with exit code: {result.returncode}")
    
    if result.returncode == 0:
        print("✅ SUCCESS: Persian AI Assistant build process completed!")
        print("📱 Your APK should be ready for download from CodeMagic")
    else:
        print("❌ Build process encountered some issues")
        print("🔍 Check the output above for details")
        
except Exception as e:
    print(f"❌ Error executing build process: {e}")

print("=" * 60)
print("🔚 Persian AI Assistant Build Manager finished")
