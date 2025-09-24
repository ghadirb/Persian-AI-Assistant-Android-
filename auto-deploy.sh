#!/bin/bash

echo "=== Persian AI Assistant - Automated Build & Deploy Script ==="
echo "This script will automatically push to all platforms and monitor builds"
echo ""

# Configuration
GITHUB_REPO="ghadirb/Persian-AI-Assistant-Android-"
GITHUB_TOKEN="${GITHUB_TOKEN:-<SET_VIA_ENV>}"
CODEMAGIC_TOKEN="${CODEMAGIC_TOKEN:-<SET_VIA_ENV>}"
GITLAB_TOKEN="YOUR_GITLAB_TOKEN_HERE"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔧 STEP 1: Preparing repository...${NC}"
cd "c:\Users\Admin\CascadeProjects\PersianAIAssistant"

# Check if we have changes
if [ -n "$(git status --porcelain)" ]; then
    echo -e "${YELLOW}📝 Found changes, committing...${NC}"
    git add .
    git commit -m "🔧 Fix build issues: Hilt version consistency, improved CI/CD

- Fixed Hilt version inconsistency (1.1.0 → 2.48)
- Added missing version variables for biometric, junit, test-junit, espresso
- Created fixed-build.yml workflow with better error handling
- Created .gitlab-ci.yml as alternative build option
- Improved CodeMagic configuration for better APK detection
- Enhanced artifact collection in all workflows"
else
    echo -e "${GREEN}✅ Repository is clean${NC}"
fi

echo -e "${BLUE}🚀 STEP 2: Pushing to GitHub...${NC}"
git remote set-url origin "https://$GITHUB_TOKEN@github.com/$GITHUB_REPO.git"
if git push origin main; then
    echo -e "${GREEN}✅ Successfully pushed to GitHub${NC}"
else
    echo -e "${RED}❌ Failed to push to GitHub${NC}"
    exit 1
fi

echo -e "${BLUE}⚡ STEP 3: Triggering GitHub Actions workflow...${NC}"
curl -X POST \
  -H "Authorization: token $GITHUB_TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Content-Type: application/json" \
  https://api.github.com/repos/$GITHUB_REPO/actions/workflows/fixed-build.yml/dispatches \
  -d '{"ref":"main","inputs":{"build_type":"debug"}}'

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ GitHub workflow triggered successfully${NC}"
else
    echo -e "${RED}❌ Failed to trigger GitHub workflow${NC}"
fi

echo -e "${BLUE}📊 STEP 4: Checking GitHub Actions status...${NC}"
echo "Waiting 30 seconds for workflow to start..."
sleep 30

# Get latest workflow run
WORKFLOW_RUN=$(curl -s -H "Authorization: token $GITHUB_TOKEN" \
  "https://api.github.com/repos/$GITHUB_REPO/actions/runs?per_page=1" | \
  grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)

if [ -n "$WORKFLOW_RUN" ]; then
    echo -e "${YELLOW}📋 Latest workflow run ID: $WORKFLOW_RUN${NC}"
    echo -e "${YELLOW}🔗 Monitor at: https://github.com/$GITHUB_REPO/actions/runs/$WORKFLOW_RUN${NC}"
else
    echo -e "${RED}❌ Could not get workflow run ID${NC}"
fi

echo -e "${BLUE}🔧 STEP 5: Testing CodeMagic API...${NC}"
CODEMAGIC_RESPONSE=$(curl -s -H "Authorization: Bearer $CODEMAGIC_TOKEN" \
  "https://api.codemagic.io/apps")

if echo "$CODEMAGIC_RESPONSE" | grep -q "applications"; then
    echo -e "${GREEN}✅ CodeMagic API authentication successful${NC}"
    echo -e "${YELLOW}📱 CodeMagic apps found${NC}"
else
    echo -e "${RED}❌ CodeMagic API authentication failed${NC}"
    echo "Response: $CODEMAGIC_RESPONSE"
fi

echo -e "${BLUE}🐙 STEP 6: Testing GitLab API...${NC}"
GITLAB_RESPONSE=$(curl -s -H "PRIVATE-TOKEN: $GITLAB_TOKEN" \
  "https://gitlab.com/api/v4/user")

if echo "$GITLAB_RESPONSE" | grep -q "username"; then
    echo -e "${GREEN}✅ GitLab API authentication successful${NC}"
    GITLAB_USER=$(echo "$GITLAB_RESPONSE" | grep -o '"username":"[^"]*' | cut -d'"' -f4)
    echo -e "${YELLOW}👤 GitLab user: $GITLAB_USER${NC}"
else
    echo -e "${RED}❌ GitLab API authentication failed${NC}"
    echo "Response: $GITLAB_RESPONSE"
fi

echo ""
echo -e "${GREEN}🎯 BUILD MONITORING SUMMARY:${NC}"
echo "=================================="
echo ""
echo -e "${YELLOW}📊 GitHub Actions:${NC}"
echo "   URL: https://github.com/$GITHUB_REPO/actions"
if [ -n "$WORKFLOW_RUN" ]; then
    echo "   Run: https://github.com/$GITHUB_REPO/actions/runs/$WORKFLOW_RUN"
fi
echo ""
echo -e "${YELLOW}🔧 CodeMagic:${NC}"
echo "   URL: https://codemagic.io/"
echo "   Token: <SET_VIA_ENV>"
echo ""
echo -e "${YELLOW}🐙 GitLab:${NC}"
echo "   URL: https://gitlab.com"
echo "   Token: YOUR_GITLAB_TOKEN_HERE"
echo ""
echo -e "${GREEN}📱 EXPECTED RESULTS:${NC}"
echo "- GitHub: APK file in Artifacts section (5-10 minutes)"
echo "- CodeMagic: APK file in email (ghadir.baraty@gmail.com)"
echo "- GitLab: Can be used as backup platform"
echo ""
echo -e "${BLUE}📞 NEXT STEPS:${NC}"
echo "1. Monitor GitHub Actions build progress"
echo "2. Check your email for CodeMagic notifications"
echo "3. Report back with results for further assistance"
echo ""
echo -e "${GREEN}✅ Automated deployment completed!${NC}"
