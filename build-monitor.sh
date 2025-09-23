#!/bin/bash
echo "=== Persian AI Assistant Build Monitor ==="
echo "Monitoring both GitHub Actions and CodeMagic builds..."
echo ""

# Function to get GitHub Actions status
get_github_status() {
    local response=$(curl -s -H "Authorization: token ${GITHUB_TOKEN:-<SET_VIA_ENV>}" "https://api.github.com/repos/ghadirb/PersianAIAssistant/actions/runs?per_page=1")
    local status=$(echo "$response" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data['workflow_runs'][0]['status'] if data['workflow_runs'] else 'no_runs')")
    local conclusion=$(echo "$response" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data['workflow_runs'][0]['conclusion'] if data['workflow_runs'] else 'no_runs')")
    local run_id=$(echo "$response" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data['workflow_runs'][0]['id'] if data['workflow_runs'] else 'no_runs')")
    echo "GitHub Actions: $status / $conclusion (ID: $run_id)"
    return 0
}

# Function to get CodeMagic status
get_codemagic_status() {
    local response=$(curl -s -H "X-Auth-Token: ${CODEMAGIC_TOKEN:-<SET_VIA_ENV>}" "https://api.codemagic.io/builds")
    local status=$(echo "$response" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data['builds'][0]['build']['status'] if data['builds'] else 'no_builds')")
    local build_id=$(echo "$response" | python3 -c "import sys, json; data=json.load(sys.stdin); print(data['builds'][0]['build']['_id'] if data['builds'] else 'no_builds')")
    echo "CodeMagic: $status (ID: $build_id)"
    return 0
}

# Main monitoring loop
while true; do
    echo "----------------------------------------"
    echo "Build Status at $(date)"
    echo "----------------------------------------"

    get_github_status
    echo ""
    get_codemagic_status

    echo ""
    echo "----------------------------------------"
    echo "Press Ctrl+C to stop monitoring"
    echo "----------------------------------------"
    sleep 60
done
