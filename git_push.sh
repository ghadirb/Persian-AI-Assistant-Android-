#!/bin/bash

# Set GitHub credentials
export GITHUB_TOKEN="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"
export GIT_AUTHOR_NAME="CodeMagic Bot"
export GIT_AUTHOR_EMAIL="codemagic@bot.com"
export GIT_COMMITTER_NAME="CodeMagic Bot"
export GIT_COMMITTER_EMAIL="codemagic@bot.com"

# Navigate to project
cd "C:\Users\Admin\Downloads\Compressed\PersianAIAssistantAndroid-main"

echo "=== Initializing Git ==="
git init
git remote add origin https://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@github.com/ghadirb/PersianAIAssistantAndroid.git

echo "=== Adding files ==="
git add .

echo "=== Committing changes ==="
git commit -m "Fix: CodeMagic build optimization - gradle settings, dependencies, and CI/CD config improved"

echo "=== Pushing to GitHub ==="
git push -f origin HEAD:main

echo "✅ Push completed!"
