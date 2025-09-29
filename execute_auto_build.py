import subprocess
import sys
import os

# اجرای فوری اسکریپت build
project_dir = r"C:\Users\Admin\Downloads\Compressed\PersianAIAssistantAndroid-main"

print("🚀 شروع build خودکار...")
print("لطفاً منتظر بمانید...")

try:
    os.chdir(project_dir)
    result = subprocess.run([sys.executable, "auto_build_final.py"], 
                          capture_output=False, text=True)
    
    if result.returncode == 0:
        print("\n✅ فرآیند build با موفقیت تکمیل شد!")
    else:
        print(f"\n⚠️ فرآیند با کد {result.returncode} تکمیل شد")
        
except Exception as e:
    print(f"❌ خطا: {e}")

print("فرآیند build اتمام یافت.")
