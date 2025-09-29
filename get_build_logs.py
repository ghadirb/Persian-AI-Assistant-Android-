#!/usr/bin/env python3
import requests
import json

def get_build_logs(build_id):
    url = f"https://api.codemagic.io/builds"
    headers = {
        "x-auth-token": "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
    }
    
    params = {
        "appId": "68d2bb0d849df2693dd0a310",
        "limit": 10
    }
    
    try:
        response = requests.get(url, headers=headers, params=params)
        
        if response.status_code == 200:
            data = response.json()
            builds = data.get('builds', [])
            
            # Find the specific build
            target_build = None
            for build in builds:
                if build.get('_id') == build_id:
                    target_build = build
                    break
            
            if target_build:
                print(f"Build {build_id} - Status: {target_build.get('status')}")
                print("="*60)
                
                # Check build actions for failures
                build_actions = target_build.get('buildActions', [])
                
                for action in build_actions:
                    action_name = action.get('name', 'Unknown')
                    action_status = action.get('status', 'Unknown')
                    log_url = action.get('logUrl')
                    
                    print(f"\nAction: {action_name}")
                    print(f"Status: {action_status}")
                    
                    if action_status == 'failed':
                        print(f"*** FAILED ACTION FOUND ***")
                        
                        # Try to get logs for this failed action
                        if log_url:
                            print(f"Getting logs from: {log_url}")
                            try:
                                log_response = requests.get(log_url, headers=headers)
                                if log_response.status_code == 200:
                                    log_content = log_response.text
                                    print("FAILURE LOGS:")
                                    print("-" * 40)
                                    print(log_content[-2000:])  # Last 2000 chars
                                    print("-" * 40)
                                else:
                                    print(f"Failed to get logs: {log_response.status_code}")
                            except Exception as e:
                                print(f"Error getting logs: {e}")
                    
                    # Check subactions
                    subactions = action.get('subactions', [])
                    for subaction in subactions:
                        sub_status = subaction.get('status', 'Unknown')
                        if sub_status == 'failed':
                            print(f"  Subaction failed: {subaction.get('command', 'Unknown')[:100]}...")
                            if 'output' in subaction:
                                print(f"  Output: {subaction['output'][-500:]}")
                
                # Also check if there's a general error message
                if 'errorMessage' in target_build:
                    print(f"\nGeneral Error: {target_build['errorMessage']}")
                    
            else:
                print(f"Build {build_id} not found")
                
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    build_id = "68dacf5349fd08c7ce8ee1bc"  # Latest failed build
    get_build_logs(build_id)
