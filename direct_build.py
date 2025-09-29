#!/usr/bin/env python3
"""
Persian AI Assistant - Direct GitHub & CodeMagic Integration
This script directly updates GitHub files and triggers CodeMagic build
"""

import requests
import json
import base64
import time
import sys

def main():
    print("🚀 Persian AI Assistant - Direct Build Process")
    print("=" * 60)
    
    # Configuration
    GITHUB_TOKEN = "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"
    CODEMAGIC_TOKEN = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
    REPO_OWNER = "ghadirb"  
    REPO_NAME = "PersianAIAssistantAndroid"
    APP_ID = "68d2bb0d849df2693dd0a310"
    
    # Step 1: Update codemagic.yaml
    print("📤 Step 1: Updating codemagic.yaml...")
    
    codemagic_yaml = """workflows:
  android-workflow:
    name: Android Workflow
    max_build_duration: 60
    instance_type: mac_mini_m1
    environment:
      java: "17"
    cache:
      cache_paths:
        - ~/.gradle/caches
        - ~/.gradle/wrapper
    triggering:
      events:
        - push
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
      - name: Build debug APK
        script: |
          ./gradlew clean
          ./gradlew assembleDebug --stacktrace
    artifacts:
      - app/build/outputs/**/*.apk
    publishing:
      email:
        recipients:
          - build@persianai.app
        notify:
          success: true
          failure: true"""
    
    # GitHub headers
    github_headers = {
        "Authorization": f"token {GITHUB_TOKEN}",
        "Accept": "application/vnd.github.v3+json",
        "User-Agent": "PersianAI-Bot/1.0"
    }
    
    # CodeMagic headers  
    codemagic_headers = {
        "X-Auth-Token": CODEMAGIC_TOKEN,
        "Content-Type": "application/json"
    }
    
    try:
        # Get current codemagic.yaml SHA
        url = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}/contents/codemagic.yaml"
        response = requests.get(url, headers=github_headers)
        
        sha = None
        if response.status_code == 200:
            file_data = response.json()
            sha = file_data['sha']
            print(f"✅ Found existing codemagic.yaml, SHA: {sha[:8]}")
        elif response.status_code == 404:
            print("📝 Creating new codemagic.yaml")
        else:
            print(f"❌ Error accessing GitHub: {response.status_code}")
            return False
        
        # Update codemagic.yaml
        encoded_content = base64.b64encode(codemagic_yaml.encode('utf-8')).decode('utf-8')
        
        update_data = {
            "message": "Fix: Optimize CodeMagic configuration for stable Android builds",
            "content": encoded_content,
            "branch": "main"
        }
        
        if sha:
            update_data["sha"] = sha
        
        response = requests.put(url, headers=github_headers, json=update_data)
        
        if response.status_code in [200, 201]:
            print("✅ codemagic.yaml updated successfully")
        else:
            print(f"❌ Failed to update codemagic.yaml: {response.status_code}")
            error_data = response.json() if response.content else {}
            print(f"Error: {error_data.get('message', 'Unknown error')}")
            return False
        
        # Step 2: Update gradle.properties
        print("\n📤 Step 2: Updating gradle.properties...")
        
        gradle_props = """# Project-wide Gradle settings
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
        
        # Get current gradle.properties SHA
        url = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}/contents/gradle.properties"
        response = requests.get(url, headers=github_headers)
        
        sha = None
        if response.status_code == 200:
            file_data = response.json()
            sha = file_data['sha']
            print(f"✅ Found existing gradle.properties, SHA: {sha[:8]}")
        
        # Update gradle.properties
        encoded_content = base64.b64encode(gradle_props.encode('utf-8')).decode('utf-8')
        
        update_data = {
            "message": "Fix: Optimize Gradle properties for CI/CD builds",
            "content": encoded_content,
            "branch": "main"
        }
        
        if sha:
            update_data["sha"] = sha
        
        response = requests.put(url, headers=github_headers, json=update_data)
        
        if response.status_code in [200, 201]:
            print("✅ gradle.properties updated successfully")
        else:
            print(f"❌ Failed to update gradle.properties: {response.status_code}")
            return False
        
        # Wait for GitHub to process changes
        print("\n⏳ Waiting for GitHub to process changes...")
        time.sleep(10)
        
        # Step 3: Trigger CodeMagic build
        print("\n🔄 Step 3: Triggering CodeMagic build...")
        
        build_data = {
            "appId": APP_ID,
            "workflowId": "android-workflow",
            "branch": "main"
        }
        
        response = requests.post("https://api.codemagic.io/builds", 
                               headers=codemagic_headers, 
                               json=build_data)
        
        if response.status_code == 201:
            build_info = response.json()
            build_id = build_info.get('_id')
            print(f"✅ Build triggered successfully!")
            print(f"🆔 Build ID: {build_id}")
            print(f"🔗 Build URL: https://codemagic.io/app/{APP_ID}/build/{build_id}")
        else:
            print(f"❌ Failed to trigger build: {response.status_code}")
            error_data = response.json() if response.content else {}
            print(f"Error: {error_data.get('message', 'Unknown error')}")
            return False
        
        # Step 4: Monitor build (simplified)
        print("\n⏳ Step 4: Starting build monitoring...")
        print("📊 Build is now running in CodeMagic...")
        print(f"👀 Monitor progress at: https://codemagic.io/app/{APP_ID}/build/{build_id}")
        
        # Monitor for a few cycles
        for i in range(10):  # Check 10 times (5 minutes)
            time.sleep(30)  # Wait 30 seconds
            
            try:
                response = requests.get(f"https://api.codemagic.io/builds/{build_id}", 
                                      headers=codemagic_headers)
                
                if response.status_code == 200:
                    build_data = response.json()
                    status = build_data.get('status', 'unknown')
                    print(f"📊 Build status: {status}")
                    
                    if status == 'successful':
                        print("\n🎉 BUILD SUCCESSFUL!")
                        
                        # Get artifacts
                        artifacts_response = requests.get(f"https://api.codemagic.io/builds/{build_id}/artifacts",
                                                        headers=codemagic_headers)
                        
                        if artifacts_response.status_code == 200:
                            artifacts = artifacts_response.json()
                            if artifacts:
                                print("\n📦 Available downloads:")
                                for artifact in artifacts:
                                    name = artifact.get('name', 'APK File')
                                    size_mb = artifact.get('size_bytes', 0) / 1024 / 1024
                                    download_url = artifact.get('url')
                                    print(f"   📱 {name} ({size_mb:.1f} MB)")
                                    print(f"   🔗 {download_url}")
                        
                        print(f"\n✅ PROCESS COMPLETED SUCCESSFULLY!")
                        print(f"📱 Your Persian AI Assistant APK is ready!")
                        return True
                        
                    elif status == 'failed':
                        print(f"\n❌ Build failed!")
                        print(f"🔍 Check logs: https://codemagic.io/app/{APP_ID}/build/{build_id}")
                        return False
                        
                    elif status in ['cancelled', 'timeout']:
                        print(f"\n⏸️ Build {status}")
                        return False
                        
                else:
                    print(f"⚠️ Error checking build status: {response.status_code}")
                    
            except Exception as e:
                print(f"⚠️ Error monitoring build: {e}")
        
        # If we get here, build is still running
        print(f"\n⏳ Build is still running after 5 minutes...")
        print(f"📊 Continue monitoring at: https://codemagic.io/app/{APP_ID}/build/{build_id}")
        print("✅ Process setup completed! Build will continue in background.")
        
        return True
        
    except Exception as e:
        print(f"\n❌ Unexpected error: {e}")
        return False

if __name__ == "__main__":
    success = main()
    
    print("\n" + "=" * 60)
    if success:
        print("🏁 Persian AI Assistant build process completed!")
    else:
        print("💥 Build process encountered errors!")
    
    print("📱 GitHub: https://github.com/ghadirb/PersianAIAssistantAndroid")
    print("🔧 CodeMagic: https://codemagic.io/app/68d2bb0d849df2693dd0a310")
    print("=" * 60)
