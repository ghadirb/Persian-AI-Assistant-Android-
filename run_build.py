import subprocess
import sys
import os

# Change to project directory
os.chdir("C:\\Users\\Admin\\Downloads\\Compressed\\PersianAIAssistantAndroid-main")

# Run the automated build script
try:
    print("🚀 Starting automated build process...")
    result = subprocess.run([sys.executable, "automated_build.py"], 
                          capture_output=True, text=True, timeout=2000)
    
    print("STDOUT:")
    print(result.stdout)
    
    if result.stderr:
        print("STDERR:")
        print(result.stderr)
        
    print(f"Return code: {result.returncode}")
    
except subprocess.TimeoutExpired:
    print("⏰ Process timed out")
except Exception as e:
    print(f"❌ Error: {e}")
