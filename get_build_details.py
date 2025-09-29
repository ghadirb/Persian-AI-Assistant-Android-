#!/usr/bin/env python3
import requests
import json

def get_build_details(build_id):
    # First try to get build from builds endpoint
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
        print(f"Status Code: {response.status_code}")
        
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
                print(f"Found build {build_id}")
                print("Raw Build JSON:")
                print(json.dumps(target_build, indent=2)[:3000] + "...")
                print("\n" + "="*50 + "\n")
                
                print(f"Build ID: {target_build.get('_id', 'Unknown')}")
                print(f"Status: {target_build.get('status', 'Unknown')}")
                print(f"Build Status: {target_build.get('buildStatus', 'Unknown')}")
                print(f"Workflow: {target_build.get('workflowId', 'Unknown')}")
                print(f"Branch: {target_build.get('branch', 'Unknown')}")
            else:
                print(f"Build {build_id} not found in recent builds")
            
            # Check for error messages in target_build
            if target_build and 'errorMessage' in target_build:
                print(f"\nError Message: {target_build['errorMessage']}")
            
            # Check build steps in target_build
            if target_build and 'buildActions' in target_build:
                build_actions = target_build['buildActions']
                print(f"\nBuild Actions ({len(build_actions)}):")
                for i, step in enumerate(build_actions, 1):
                    step_name = step.get('name', f'Step {i}')
                    step_status = step.get('status', 'Unknown')
                    print(f"  {i}. {step_name}: {step_status}")
                    
                    if step_status == 'failed':
                        if 'output' in step:
                            print(f"     Output: {step['output'][:500]}...")
                        if 'errorOutput' in step:
                            print(f"     Error: {step['errorOutput'][:500]}...")
            
            # Check logs
            if 'logs' in build_data:
                print(f"\nLogs:")
                logs = build_data['logs']
                if isinstance(logs, str):
                    print(logs[:1000] + "..." if len(logs) > 1000 else logs)
                elif isinstance(logs, list):
                    for log in logs[:5]:  # Show first 5 log entries
                        print(f"  - {log}")
                        
        else:
            print(f"[ERROR] Failed to get build details: {response.text}")
            
    except Exception as e:
        print(f"[ERROR] Error: {e}")

if __name__ == "__main__":
    # Latest failed build
    build_id = "68dac613908369b5360476e5"
    get_build_details(build_id)
