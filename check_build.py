#!/usr/bin/env python3
import requests
import json
import time

def check_build_status(build_id):
    url = f"https://api.codemagic.io/builds/{build_id}"
    headers = {
        "x-auth-token": "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
    }
    
    try:
        response = requests.get(url, headers=headers)
        print(f"Status Code: {response.status_code}")
        
        if response.status_code == 200:
            build_data = response.json()
            status = build_data.get('status', 'Unknown')
            workflow = build_data.get('workflowId', 'Unknown')
            branch = build_data.get('branch', 'Unknown')
            
            print(f"Build Status: {status}")
            print(f"Workflow: {workflow}")
            print(f"Branch: {branch}")
            
            if 'startedAt' in build_data:
                print(f"Started At: {build_data['startedAt']}")
            
            if status == 'finished':
                if 'finishedAt' in build_data:
                    print(f"Finished At: {build_data['finishedAt']}")
                
                build_status = build_data.get('buildStatus', 'Unknown')
                print(f"Build Result: {build_status}")
                
                if build_status == 'success':
                    print("[SUCCESS] Build completed successfully!")
                    if 'artefacts' in build_data:
                        print("Artifacts:")
                        for artifact in build_data['artefacts']:
                            print(f"  - {artifact.get('name', 'Unknown')}: {artifact.get('url', 'No URL')}")
                else:
                    print(f"[ERROR] Build failed with status: {build_status}")
                    
            elif status == 'building':
                print("[INFO] Build is still in progress...")
            elif status == 'queued':
                print("[INFO] Build is queued...")
            else:
                print(f"[INFO] Build status: {status}")
                
        else:
            print(f"[ERROR] Failed to get build status: {response.text}")
            
    except Exception as e:
        print(f"[ERROR] Error: {e}")

if __name__ == "__main__":
    build_id = "68dac613908369b5360476e5"  # New build after fixing conflicts
    check_build_status(build_id)
