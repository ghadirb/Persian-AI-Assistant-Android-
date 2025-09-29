#!/usr/bin/env python3
import requests
import json
import time

def monitor_build(build_id, max_wait_minutes=25):
    url = f"https://api.codemagic.io/builds/{build_id}"
    headers = {
        "x-auth-token": "sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
    }
    
    start_time = time.time()
    max_wait_seconds = max_wait_minutes * 60
    
    print(f"Monitoring build {build_id}...")
    print(f"Max wait time: {max_wait_minutes} minutes")
    print("-" * 50)
    
    while True:
        try:
            elapsed = time.time() - start_time
            if elapsed > max_wait_seconds:
                print(f"[TIMEOUT] Stopped monitoring after {max_wait_minutes} minutes")
                break
                
            response = requests.get(url, headers=headers)
            
            if response.status_code == 200:
                build_data = response.json()
                status = build_data.get('status', 'Unknown')
                workflow = build_data.get('workflowId', 'Unknown')
                
                elapsed_min = int(elapsed // 60)
                elapsed_sec = int(elapsed % 60)
                
                print(f"[{elapsed_min:02d}:{elapsed_sec:02d}] Status: {status}")
                
                if status == 'finished':
                    build_status = build_data.get('buildStatus', 'Unknown')
                    print(f"Build Result: {build_status}")
                    
                    if build_status == 'success':
                        print("[SUCCESS] Build completed successfully!")
                        
                        # Check for artifacts
                        artifacts = build_data.get('artefacts', [])
                        if artifacts:
                            print("\nArtifacts available:")
                            for artifact in artifacts:
                                name = artifact.get('name', 'Unknown')
                                url = artifact.get('url', 'No URL')
                                size = artifact.get('size', 'Unknown size')
                                print(f"  - {name} ({size})")
                                print(f"    Download: {url}")
                        else:
                            print("No artifacts found")
                            
                    elif build_status == 'failed':
                        print("[ERROR] Build failed!")
                        
                        # Try to get error details
                        if 'steps' in build_data:
                            print("\nBuild steps:")
                            for step in build_data['steps']:
                                step_name = step.get('name', 'Unknown')
                                step_status = step.get('status', 'Unknown')
                                print(f"  - {step_name}: {step_status}")
                                
                                if step_status == 'failed' and 'script' in step:
                                    print(f"    Failed script: {step['script']}")
                    
                    break
                    
                elif status in ['building', 'queued']:
                    # Continue monitoring
                    pass
                else:
                    print(f"[INFO] Unexpected status: {status}")
                    
            else:
                print(f"[ERROR] API request failed: {response.status_code}")
                
        except Exception as e:
            print(f"[ERROR] {e}")
            
        # Wait 30 seconds before next check
        time.sleep(30)

if __name__ == "__main__":
    build_id = "68dacf5349fd08c7ce8ee1bc"  # Latest build after fixing more conflicts
    monitor_build(build_id)
