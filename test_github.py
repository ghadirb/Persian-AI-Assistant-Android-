import requests
import base64

print("🚀 Testing GitHub API access...")

GITHUB_TOKEN = "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"
REPO_OWNER = "ghadirb"
REPO_NAME = "PersianAIAssistantAndroid"

headers = {
    "Authorization": f"token {GITHUB_TOKEN}",
    "Accept": "application/vnd.github.v3+json"
}

# Test 1: Check repository access
print("Test 1: Repository access...")
url = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}"

try:
    response = requests.get(url, headers=headers)
    print(f"Status: {response.status_code}")
    
    if response.status_code == 200:
        data = response.json()
        print(f"✅ Repository: {data['full_name']}")
        print(f"✅ Default branch: {data['default_branch']}")
        print(f"✅ Last updated: {data['updated_at']}")
    else:
        print(f"❌ Error: {response.text}")
        
except Exception as e:
    print(f"❌ Exception: {e}")

# Test 2: Check file access
print("\nTest 2: File access...")
url = f"https://api.github.com/repos/{REPO_OWNER}/{REPO_NAME}/contents/codemagic.yaml"

try:
    response = requests.get(url, headers=headers)
    print(f"Status: {response.status_code}")
    
    if response.status_code == 200:
        data = response.json()
        print(f"✅ File found: {data['name']}")
        print(f"✅ Size: {data['size']} bytes")
        print(f"✅ SHA: {data['sha'][:8]}")
    elif response.status_code == 404:
        print("📝 File not found (will create new)")
    else:
        print(f"❌ Error: {response.text}")
        
except Exception as e:
    print(f"❌ Exception: {e}")

print("\n" + "="*50)
print("GitHub API test completed!")
