import requests
import base64
import json
import os

# GitHub settings
GITHUB_TOKEN = "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"
REPO_OWNER = "ghadirb"
REPO_NAME = "PersianAIAssistantAndroid"

headers = {
    "Authorization": f"token {GITHUB_TOKEN}",
    "Accept": "application/vnd.github.v3+json"
}

def upload_file(file_path, github_path, message):
    """Upload a file to GitHub"""
    url = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}/contents/{github_path}"
    
    # Read file content
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Encode content
    encoded_content = base64.b64encode(content.encode('utf-8')).decode('utf-8')
    
    # Get current file SHA if exists
    response = requests.get(url, headers=headers)
    sha = None
    if response.status_code == 200:
        sha = response.json()['sha']
    
    # Prepare data
    data = {
        "message": message,
        "content": encoded_content
    }
    if sha:
        data["sha"] = sha
    
    # Upload
    response = requests.put(url, headers=headers, json=data)
    print(f"Upload {github_path}: {response.status_code}")
    return response.status_code == 200 or response.status_code == 201

# Upload modified files
files_to_upload = [
    ("C:\\Users\\Admin\\Downloads\\Compressed\\PersianAIAssistantAndroid-main\\codemagic.yaml", "codemagic.yaml", "Fix: Optimize CodeMagic build configuration"),
    ("C:\\Users\\Admin\\Downloads\\Compressed\\PersianAIAssistantAndroid-main\\gradle.properties", "gradle.properties", "Fix: Optimize Gradle properties for CI/CD"),
    ("C:\\Users\\Admin\\Downloads\\Compressed\\PersianAIAssistantAndroid-main\\app\\build.gradle", "app/build.gradle", "Fix: Optimize app build.gradle dependencies"),
]

print("Uploading files to GitHub...")
for local_path, github_path, message in files_to_upload:
    success = upload_file(local_path, github_path, message)
    if success:
        print(f"✅ {github_path} uploaded successfully")
    else:
        print(f"❌ Failed to upload {github_path}")

print("GitHub upload completed!")
