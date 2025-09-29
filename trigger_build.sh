#!/bin/bash

# CodeMagic API settings
CODEMAGIC_TOKEN="sC89KeWx9DqYyg1gGFHXF0IIxLwJV4PdM-0L1urk4nQ"
APP_ID="68d2bb0d849df2693dd0a310"
WORKFLOW_ID="android-workflow"

echo "=== Triggering CodeMagic Build ==="

# Trigger build
curl -X POST \
  "https://api.codemagic.io/builds" \
  -H "X-Auth-Token: $CODEMAGIC_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "appId": "'$APP_ID'",
    "workflowId": "'$WORKFLOW_ID'",
    "branch": "main",
    "environment": {
      "variables": {
        "BUILD_TYPE": "debug"
      }
    }
  }' > build_response.json

echo "✅ Build triggered!"
echo "Response saved to build_response.json"

# Show response
cat build_response.json
