#!/bin/bash
# Persian AI Assistant - Final Test Script
echo "=== Persian AI Assistant - Final Build Test ==="
echo "This script will guide you through testing all platforms"
echo ""

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}🔧 STEP 1: Push Changes to GitHub${NC}"
echo "Run this command:"
echo "git push https://<SET_VIA_ENV_OR_CI_SECRET>@github.com/ghadirb/PersianAIAssistant.git main"
echo ""

echo -e "${YELLOW}📊 STEP 2: Monitor GitHub Actions${NC}"
echo "1. Open: https://github.com/ghadirb/Persian-AI-Assistant-Android-/actions"
echo "2. Look for 'Persian AI Assistant - Fixed Build'"
echo "3. Click 'Run workflow' -> Select 'debug'"
echo "4. Wait 5-10 minutes"
echo "5. Download APK from 'Artifacts' section"
echo ""

echo -e "${YELLOW}🔧 STEP 3: Test CodeMagic${NC}"
echo "1. Open: https://codemagic.io/"
echo "2. Login with token: <SET_VIA_ENV_OR_CI_SECRET>"
echo "3. Find your Persian AI Assistant project"
echo "4. Click 'Start new build'"
echo "5. Check email: ghadir.baraty@gmail.com"
echo ""

echo -e "${YELLOW}🐙 STEP 4: Alternative - GitLab${NC}"
echo "1. Open: https://gitlab.com"
echo "2. Create new repository (if needed)"
echo "3. Use token: <SET_VIA_ENV_OR_CI_SECRET>"
echo "4. GitLab CI will build automatically"
echo ""

echo -e "${GREEN}✅ WHAT TO EXPECT:${NC}"
echo "- GitHub Actions: Green checkmark + APK download"
echo "- CodeMagic: APK file in email"
echo "- GitLab: Automatic build with download link"
echo ""

echo -e "${RED}❌ IF YOU SEE ERRORS:${NC}"
echo "- Check token validity"
echo "- Verify repository name"
echo "- Look at build logs for details"
echo ""

echo -e "${YELLOW}📞 REPORT RESULTS TO ME:${NC}"
echo "After testing, tell me:"
echo "1. Which platform worked?"
echo "2. Any error messages?"
echo "3. Did you get APK files?"
echo ""

echo -e "${GREEN}🎯 READY TO TEST!${NC}"
echo "Follow the steps above and let me know the results!"
