#!/usr/bin/env python3
import requests
import json
import base64
import time

# Configuration
GITHUB_TOKEN = "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"
CODEMAGIC_TOKEN = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ" 
REPO_OWNER = "ghadirb"
REPO_NAME = "PersianAIAssistantAndroid"
APP_ID = "68d2bb0d849df2693dd0a310"

# Headers
github_headers = {
    "Authorization": f"token {GITHUB_TOKEN}",
    "Accept": "application/vnd.github.v3+json",
    "User-Agent": "PersianAI-BuildBot/1.0"
}

codemagic_headers = {
    "X-Auth-Token": CODEMAGIC_TOKEN,
    "Content-Type": "application/json"
}

def print_status(message, status="INFO"):
    symbols = {"INFO": "ℹ️", "SUCCESS": "✅", "ERROR": "❌", "WARNING": "⚠️"}
    print(f"{symbols.get(status, 'ℹ️')} {message}")

def update_github_file(file_path, content, commit_message):
    """Update a file on GitHub repository"""
    url = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}/contents/{file_path}"
    
    try:
        # Get current file info
        response = requests.get(url, headers=github_headers)
        sha = None
        
        if response.status_code == 200:
            file_data = response.json()
            sha = file_data['sha']
            print_status(f"Found existing {file_path}, SHA: {sha[:7]}")
        elif response.status_code == 404:
            print_status(f"Creating new file: {file_path}")
        else:
            print_status(f"Error getting file info: {response.status_code}", "ERROR")
            return False
        
        # Encode content
        encoded_content = base64.b64encode(content.encode('utf-8')).decode('utf-8')
        
        # Prepare update data
        update_data = {
            "message": commit_message,
            "content": encoded_content,
            "branch": "main"
        }
        
        if sha:
            update_data["sha"] = sha
        
        # Update file
        response = requests.put(url, headers=github_headers, json=update_data)
        
        if response.status_code in [200, 201]:
            print_status(f"Successfully updated {file_path}", "SUCCESS")
            return True
        else:
            error_data = response.json() if response.content else {}
            print_status(f"Failed to update {file_path}: {response.status_code} - {error_data.get('message', 'Unknown error')}", "ERROR")
            return False
            
    except Exception as e:
        print_status(f"Exception updating {file_path}: {str(e)}", "ERROR")
        return False

def trigger_codemagic_build():
    """Trigger CodeMagic build"""
    url = "https://api.codemagic.io/builds"
    
    build_data = {
        "appId": APP_ID,
        "workflowId": "android-workflow",
        "branch": "main",
        "environment": {
            "variables": {
                "BUILD_MODE": "debug"
            }
        }
    }
    
    try:
        response = requests.post(url, headers=codemagic_headers, json=build_data)
        
        if response.status_code == 201:
            build_info = response.json()
            build_id = build_info.get('_id')
            print_status(f"Build triggered successfully! Build ID: {build_id}", "SUCCESS")
            return build_id
        else:
            error_data = response.json() if response.content else {}
            print_status(f"Failed to trigger build: {response.status_code} - {error_data.get('message', 'Unknown error')}", "ERROR")
            return None
            
    except Exception as e:
        print_status(f"Exception triggering build: {str(e)}", "ERROR")
        return None

def monitor_build(build_id):
    """Monitor build progress"""
    url = f"https://api.codemagic.io/builds/{build_id}"
    
    print_status("Starting build monitoring...")
    last_status = None
    start_time = time.time()
    timeout = 1800  # 30 minutes
    
    while time.time() - start_time < timeout:
        try:
            response = requests.get(url, headers=codemagic_headers)
            
            if response.status_code == 200:
                build_data = response.json()
                current_status = build_data.get('status')
                
                if current_status != last_status:
                    print_status(f"Build status: {current_status}")
                    last_status = current_status
                
                if current_status in ['successful', 'failed', 'cancelled', 'timeout']:
                    return current_status
                
            time.sleep(30)  # Check every 30 seconds
            
        except Exception as e:
            print_status(f"Error monitoring build: {str(e)}", "WARNING")
            time.sleep(30)
    
    print_status("Build monitoring timeout", "WARNING")
    return "timeout"

def get_build_artifacts(build_id):
    """Get build artifacts"""
    url = f"https://api.codemagic.io/builds/{build_id}/artifacts"
    
    try:
        response = requests.get(url, headers=codemagic_headers)
        
        if response.status_code == 200:
            artifacts = response.json()
            return artifacts
        else:
            print_status(f"Failed to get artifacts: {response.status_code}", "ERROR")
            return []
            
    except Exception as e:
        print_status(f"Exception getting artifacts: {str(e)}", "ERROR")
        return []

def main():
    print_status("🚀 Starting Persian AI Assistant Build Process")
    print_status(f"Repository: {REPO_OWNER}/{REPO_NAME}")
    print_status(f"CodeMagic App ID: {APP_ID}")
    print("=" * 60)
    
    # Step 1: Update GitHub files
    print_status("📤 Step 1: Updating GitHub files...")
    
    # Updated codemagic.yaml
    codemagic_content = """workflows:
  android-workflow:
    name: Android Workflow
    max_build_duration: 60
    instance_type: mac_mini_m1
    environment:
      java: "17"
      android_signing:
        - keystore_reference
      vars:
        PACKAGE_NAME: "com.example.persianaiapp"
      groups:
        - google_play_store
    cache:
      cache_paths:
        - ~/.gradle/caches
        - ~/.gradle/wrapper
        - ~/.android/build-cache
    triggering:
      events:
        - push
        - tag
        - pull_request
      branch_patterns:
        - pattern: main
          include: true
          source: true
    scripts:
      - name: Set up local.properties
        script: |
          echo "sdk.dir=$ANDROID_HOME" > "$CM_BUILD_DIR/local.properties"
      - name: Make gradlew executable
        script: chmod +x ./gradlew
      - name: Clean and build debug APK
        script: |
          ./gradlew clean
          ./gradlew assembleDebug --stacktrace --info
    artifacts:
      - app/build/outputs/**/*.apk
      - app/build/outputs/**/*.aab
    publishing:
      email:
        recipients:
          - build@persianai.app
        notify:
          success: true
          failure: true"""
    
    # Updated gradle.properties
    gradle_props_content = """# Project-wide Gradle settings
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.daemon=false

# AndroidX package structure
android.useAndroidX=true
# Kotlin code style
kotlin.code.style=official
# Enables namespacing of each library's R class
android.nonTransitiveRClass=true

# Disable build cache for CI
org.gradle.caching=false
# Disable configuration cache for stability
org.gradle.configuration-cache=false

# Android options
android.enableJetifier=true

# Kotlin compiler options
kapt.use.worker.api=false
kapt.incremental.apt=false

# Fix for common build issues
org.gradle.unsafe.configuration-cache=false"""
    
    # Update files
    files_updated = 0
    
    if update_github_file("codemagic.yaml", codemagic_content, "Fix: Optimize CodeMagic configuration for stable builds"):
        files_updated += 1
    
    if update_github_file("gradle.properties", gradle_props_content, "Fix: Optimize Gradle properties for CI/CD builds"):
        files_updated += 1
    
    if files_updated < 2:
        print_status("Failed to update required files. Aborting.", "ERROR")
        return False
    
    print_status(f"Successfully updated {files_updated} files", "SUCCESS")
    
    # Wait for GitHub to process
    print_status("Waiting for GitHub to process changes...")
    time.sleep(15)
    
    # Step 2: Trigger CodeMagic build
    print_status("🔄 Step 2: Triggering CodeMagic build...")
    
    build_id = trigger_codemagic_build()
    if not build_id:
        print_status("Failed to trigger build. Aborting.", "ERROR")
        return False
    
    # Step 3: Monitor build
    print_status("⏳ Step 3: Monitoring build progress...")
    
    final_status = monitor_build(build_id)
    
    # Step 4: Handle results
    print_status(f"🏁 Final build status: {final_status}")
    
    if final_status == 'successful':
        print_status("🎉 Build completed successfully!", "SUCCESS")
        
        artifacts = get_build_artifacts(build_id)
        if artifacts:
            print_status("📦 Available artifacts:", "SUCCESS")
            for artifact in artifacts:
                name = artifact.get('name', 'Unknown')
                size_mb = artifact.get('size_bytes', 0) / 1024 / 1024
                url = artifact.get('url', 'No URL')
                print(f"   • {name} ({size_mb:.2f} MB)")
                print(f"     Download: {url}")
        
        print_status(f"🔗 CodeMagic Build: https://codemagic.io/app/{APP_ID}/build/{build_id}", "SUCCESS")
        
    elif final_status == 'failed':
        print_status(f"❌ Build failed! Check logs: https://codemagic.io/app/{APP_ID}/build/{build_id}", "ERROR")
        
    else:
        print_status(f"⏸️ Build ended with status: {final_status}", "WARNING")
    
    print("=" * 60)
    print_status(f"📱 GitHub Repository: https://github.com/{REPO_OWNER}/{REPO_NAME}")
    print_status("🔚 Process completed!")
    
    return final_status == 'successful'

if __name__ == "__main__":
    main()
