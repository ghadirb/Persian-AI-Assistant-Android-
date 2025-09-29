#!/bin/bash

echo "=== Persian AI Assistant Build Test ==="
echo "Testing the fixed build configuration..."

# Check if we're in the right directory
if [ ! -f "app/build.gradle" ]; then
    echo "❌ Error: app/build.gradle not found. Please run this script from the project root."
    exit 1
fi

# Check gradlew
if [ ! -f "gradlew" ]; then
    echo "❌ Error: gradlew not found"
    exit 1
fi

# Make gradlew executable
chmod +x gradlew

echo "✅ Project structure looks good"

# Check for Hilt version consistency
echo "Checking Hilt version consistency..."
if grep -q "hilt-navigation-compose:1.1.0" app/build.gradle; then
    echo "❌ Error: Found hardcoded Hilt version 1.1.0"
    echo "This should be using the variable hilt_version (2.48)"
    exit 1
else
    echo "✅ Hilt versions are consistent"
fi

# Check for missing version variables
echo "Checking for missing version variables..."
MISSING_VARS=()

if ! grep -q "biometric_version" build.gradle; then
    MISSING_VARS+=("biometric_version")
fi

if ! grep -q "junit_version" build.gradle; then
    MISSING_VARS+=("junit_version")
fi

if ! grep -q "test_junit_version" build.gradle; then
    MISSING_VARS+=("test_junit_version")
fi

if ! grep -q "espresso_version" build.gradle; then
    MISSING_VARS+=("espresso_version")
fi

if [ ${#MISSING_VARS[@]} -gt 0 ]; then
    echo "❌ Error: Missing version variables: ${MISSING_VARS[*]}"
    exit 1
else
    echo "✅ All version variables are present"
fi

echo ""
echo "=== Configuration Test Passed! ==="
echo "The build configuration should now work correctly."
echo ""
echo "To test with your tokens:"
echo "1. Push changes to GitHub (if not already pushed)"
echo "2. Go to GitHub Actions and run the 'fixed-build.yml' workflow"
echo "3. Check CodeMagic with your token"
echo "4. If issues persist, try GitLab as alternative"
echo ""
echo "GitHub token: <SET_VIA_ENV_OR_CI_SECRET>"
echo "CodeMagic token: <SET_VIA_ENV_OR_CI_SECRET>"
echo "GitLab token: <SET_VIA_ENV_OR_CI_SECRET>"
