import subprocess
import sys
import os

print("🚀 Persian AI Assistant - Starting Build Process...")
print("=" * 60)

# Change to project directory
project_dir = "C:\\Users\\Admin\\Downloads\\Compressed\\PersianAIAssistantAndroid-main"
os.chdir(project_dir)

try:
    # Run the build manager
    process = subprocess.Popen(
        [sys.executable, "build_manager.py"], 
        stdout=subprocess.PIPE, 
        stderr=subprocess.PIPE, 
        text=True,
        bufsize=1,
        universal_newlines=True
    )
    
    # Real-time output
    while True:
        output = process.stdout.readline()
        if output == '' and process.poll() is not None:
            break
        if output:
            print(output.strip())
    
    # Get any remaining output
    stdout, stderr = process.communicate()
    
    if stdout:
        print(stdout)
    if stderr:
        print("STDERR:", stderr)
    
    return_code = process.returncode
    print(f"\nProcess completed with return code: {return_code}")
    
    if return_code == 0:
        print("✅ Build process completed successfully!")
    else:
        print("❌ Build process failed!")
        
except Exception as e:
    print(f"❌ Error running build manager: {e}")

print("=" * 60)
print("🏁 Build manager execution finished")
