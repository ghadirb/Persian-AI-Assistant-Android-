import requests
import json
import time
import base64

# Configuration
GITHUB_TOKEN = "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"
CODEMAGIC_TOKEN = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
REPO_OWNER = "ghadirb"
REPO_NAME = "PersianAIAssistantAndroid"
APP_ID = "68d2bb0d849df2693dd0a310"

# Headers
github_headers = {
    "Authorization": f"token {GITHUB_TOKEN}",
    "Accept": "application/vnd.github.v3+json"
}

codemagic_headers = {
    "X-Auth-Token": CODEMAGIC_TOKEN,
    "Content-Type": "application/json"
}

def update_github_file(file_path, github_path, content, message):
    """Update a file on GitHub"""
    url = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}/contents/{github_path}"
    
    # Get current SHA
    response = requests.get(url, headers=github_headers)
    sha = None
    if response.status_code == 200:
        sha = response.json()['sha']
    
    # Encode content
    encoded_content = base64.b64encode(content.encode('utf-8')).decode('utf-8')
    
    # Update data
    data = {
        "message": message,
        "content": encoded_content
    }
    if sha:
        data["sha"] = sha
    
    # Update file
    response = requests.put(url, headers=github_headers, json=data)
    return response.status_code in [200, 201]

def trigger_codemagic_build():
    """Trigger CodeMagic build"""
    url = "https://api.codemagic.io/builds"
    
    data = {
        "appId": APP_ID,
        "workflowId": "android-workflow", 
        "branch": "main"
    }
    
    response = requests.post(url, headers=codemagic_headers, json=data)
    print(f"CodeMagic trigger response: {response.status_code}")
    
    if response.status_code == 201:
        build_data = response.json()
        return build_data.get("_id")
    return None

def check_build_status(build_id):
    """Check build status"""
    url = f"https://api.codemagic.io/builds/{build_id}"
    response = requests.get(url, headers=codemagic_headers)
    
    if response.status_code == 200:
        build_data = response.json()
        return build_data.get("status")
    return None

def download_artifacts(build_id):
    """Download build artifacts"""
    url = f"https://api.codemagic.io/builds/{build_id}/artifacts"
    response = requests.get(url, headers=codemagic_headers)
    
    if response.status_code == 200:
        artifacts = response.json()
        return artifacts
    return []

# Main process
print("🚀 Starting automated build process...")

# Step 1: Update GitHub files
print("\n📤 Step 1: Updating GitHub repository...")

# Read and update codemagic.yaml
with open("C:\\Users\\Admin\\Downloads\\Compressed\\PersianAIAssistantAndroid-main\\codemagic.yaml", 'r', encoding='utf-8') as f:
    codemagic_content = f.read()

success1 = update_github_file("codemagic.yaml", "codemagic.yaml", codemagic_content, 
                             "Fix: Optimize CodeMagic configuration for stable builds")

# Read and update gradle.properties  
with open("C:\\Users\\Admin\\Downloads\\Compressed\\PersianAIAssistantAndroid-main\\gradle.properties", 'r', encoding='utf-8') as f:
    gradle_props_content = f.read()

success2 = update_github_file("gradle.properties", "gradle.properties", gradle_props_content,
                             "Fix: Optimize Gradle properties for CI/CD builds")

# Read and update app/build.gradle
with open("C:\\Users\\Admin\\Downloads\\Compressed\\PersianAIAssistantAndroid-main\\app\\build.gradle", 'r', encoding='utf-8') as f:
    build_gradle_content = f.read()

success3 = update_github_file("app/build.gradle", "app/build.gradle", build_gradle_content,
                             "Fix: Optimize app dependencies and build configuration")

if success1 and success2 and success3:
    print("✅ All files updated successfully on GitHub!")
else:
    print("❌ Some files failed to update")
    exit(1)

# Wait a bit for GitHub to process
time.sleep(10)

# Step 2: Trigger CodeMagic build
print("\n🔄 Step 2: Triggering CodeMagic build...")
build_id = trigger_codemagic_build()

if not build_id:
    print("❌ Failed to trigger build")
    exit(1)

print(f"✅ Build triggered! Build ID: {build_id}")

# Step 3: Monitor build
print("\n⏳ Step 3: Monitoring build progress...")
last_status = None
start_time = time.time()
max_wait_time = 1800  # 30 minutes

while True:
    if time.time() - start_time > max_wait_time:
        print("⏰ Build timeout reached")
        break
    
    status = check_build_status(build_id)
    
    if status != last_status:
        print(f"📊 Build status: {status}")
        last_status = status
    
    if status in ['successful', 'failed', 'cancelled', 'timeout']:
        break
        
    time.sleep(30)

# Step 4: Handle results
print(f"\n🏁 Final build status: {last_status}")

if last_status == 'successful':
    print("🎉 Build successful! Downloading artifacts...")
    artifacts = download_artifacts(build_id)
    
    if artifacts:
        print("📦 Available artifacts:")
        for artifact in artifacts:
            print(f"  - {artifact.get('name', 'Unknown')}: {artifact.get('url', 'No URL')}")
        
        print(f"\n✅ BUILD COMPLETED SUCCESSFULLY!")
        print(f"🔗 CodeMagic Build URL: https://codemagic.io/app/{APP_ID}/build/{build_id}")
        
    else:
        print("⚠️ Build successful but no artifacts found")
        
elif last_status == 'failed':
    print(f"❌ Build failed! Check logs: https://codemagic.io/app/{APP_ID}/build/{build_id}")
else:
    print(f"⏸️ Build status: {last_status}")

print(f"\n📱 GitHub Repository: https://github.com/{REPO_OWNER}/{REPO_NAME}")
print("🔚 Process completed!")
