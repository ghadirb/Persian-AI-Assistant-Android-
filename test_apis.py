import requests
import json

# Test GitHub API
GITHUB_TOKEN = "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"
REPO_OWNER = "ghadirb"
REPO_NAME = "PersianAIAssistantAndroid"

headers = {
    "Authorization": f"token {GITHUB_TOKEN}",
    "Accept": "application/vnd.github.v3+json",
    "User-Agent": "PersianAI-BuildBot/1.0"
}

print("Testing GitHub API connection...")

try:
    # Test repository access
    url = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}"
    response = requests.get(url, headers=headers)
    
    print(f"Repository access: {response.status_code}")
    if response.status_code == 200:
        repo_data = response.json()
        print(f"Repository: {repo_data['full_name']}")
        print(f"Default branch: {repo_data['default_branch']}")
        print(f"✅ GitHub API connection successful")
    else:
        print(f"❌ GitHub API error: {response.status_code}")
        print(response.text)
        
except Exception as e:
    print(f"❌ Exception: {e}")

print("\nTesting CodeMagic API...")

# Test CodeMagic API
CODEMAGIC_TOKEN = "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
APP_ID = "68d2bb0d849df2693dd0a310"

codemagic_headers = {
    "X-Auth-Token": CODEMAGIC_TOKEN,
    "Content-Type": "application/json"
}

try:
    # Test app access
    url = f"https://api.codemagic.io/apps/{APP_ID}"
    response = requests.get(url, headers=codemagic_headers)
    
    print(f"CodeMagic app access: {response.status_code}")
    if response.status_code == 200:
        app_data = response.json()
        print(f"App name: {app_data['appName']}")
        print(f"✅ CodeMagic API connection successful")
    else:
        print(f"❌ CodeMagic API error: {response.status_code}")
        print(response.text)
        
except Exception as e:
    print(f"❌ Exception: {e}")

print("API test completed!")
