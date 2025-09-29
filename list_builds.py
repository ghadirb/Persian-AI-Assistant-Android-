#!/usr/bin/env python3
import requests
import json
from datetime import datetime

def list_recent_builds():
    url = "https://api.codemagic.io/builds"
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
            
            print(f"\nFound {len(builds)} recent builds:")
            print("-" * 80)
            
            for i, build in enumerate(builds, 1):
                build_id = build.get('_id', 'Unknown')
                status = build.get('status', 'Unknown')
                workflow = build.get('workflowId', 'Unknown')
                branch = build.get('branch', 'Unknown')
                
                # Format timestamps
                started_at = build.get('startedAt', '')
                finished_at = build.get('finishedAt', '')
                
                if started_at:
                    try:
                        started_dt = datetime.fromisoformat(started_at.replace('Z', '+00:00'))
                        started_str = started_dt.strftime('%Y-%m-%d %H:%M:%S')
                    except:
                        started_str = started_at
                else:
                    started_str = 'Not started'
                
                print(f"{i}. Build ID: {build_id}")
                print(f"   Status: {status}")
                print(f"   Workflow: {workflow}")
                print(f"   Branch: {branch}")
                print(f"   Started: {started_str}")
                
                if status == 'finished':
                    build_status = build.get('buildStatus', 'Unknown')
                    print(f"   Result: {build_status}")
                    
                    if finished_at:
                        try:
                            finished_dt = datetime.fromisoformat(finished_at.replace('Z', '+00:00'))
                            finished_str = finished_dt.strftime('%Y-%m-%d %H:%M:%S')
                            print(f"   Finished: {finished_str}")
                        except:
                            print(f"   Finished: {finished_at}")
                
                print("-" * 40)
                
        else:
            print(f"[ERROR] Failed to get builds: {response.text}")
            
    except Exception as e:
        print(f"[ERROR] Error: {e}")

if __name__ == "__main__":
    list_recent_builds()
