#!/usr/bin/env python3
# IMMEDIATE EXECUTION - Persian AI Assistant Auto Build

import os
import subprocess
import sys

# Execute the build process immediately
project_dir = r"C:\Users\Admin\Downloads\Compressed\PersianAIAssistantAndroid-main"

print("🚀 EXECUTING BUILD NOW...")
print("Persian AI Assistant - Auto Build Starting...")

# Change to project directory and execute
os.chdir(project_dir)

# Run the final auto build script
exec(open('final_auto_build.py').read())
