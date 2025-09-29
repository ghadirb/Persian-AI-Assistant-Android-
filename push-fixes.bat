@echo off
echo === Pushing fixes to GitHub ===

git add .
git commit -m "Fix: CodeMagic build issues - optimized gradle settings and simplified configs"
git push origin main

echo === Push completed ===
echo Next: Test in CodeMagic
pause
