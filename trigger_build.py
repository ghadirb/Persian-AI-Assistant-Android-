#!/usr/bin/env python3
import requests
import json
import sys

def trigger_codemagic_build():
    url = "https://api.codemagic.io/builds"
    headers = {
        "x-auth-token": "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ",
        "Content-Type": "application/json"
    }
    
    payload = {
        "appId": "68d2bb0d849df2693dd0a310",
        "workflowId": "simple-apk",
        "branch": "main"
    }
    
    try:
        response = requests.post(url, headers=headers, json=payload)
        print(f"Status Code: {response.status_code}")
        print(f"Response: {response.text}")
        
        if response.status_code in [200, 201]:
            build_data = response.json()
            build_id = build_data.get('buildId') or build_data.get('_id', 'Unknown')
            print(f"[SUCCESS] Build triggered successfully!")
            print(f"Build ID: {build_id}")
            print(f"Build URL: https://codemagic.io/app/{payload['appId']}/build/{build_id}")
        else:
            print(f"[ERROR] Failed to trigger build: {response.text}")
            
    except Exception as e:
        print(f"[ERROR] Error: {e}")

if __name__ == "__main__":
    trigger_codemagic_build()
