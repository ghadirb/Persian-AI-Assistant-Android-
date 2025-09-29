#!/usr/bin/env python3
import os
import re

def find_conflicts(root_dir):
    """Find all files with git conflict markers"""
    conflicts = {}
    conflict_pattern = re.compile(r'<<<<<<<|=======|>>>>>>>')
    
    # Patterns to exclude
    exclude_dirs = {'.git', '.gradle', 'build', '.idea', 'node_modules'}
    
    for dirpath, dirnames, filenames in os.walk(root_dir):
        # Remove excluded directories
        dirnames[:] = [d for d in dirnames if d not in exclude_dirs]
        
        for filename in filenames:
            # Skip binary and generated files
            if filename.endswith(('.jar', '.apk', '.png', '.jpg', '.class', '.dex')):
                continue
                
            filepath = os.path.join(dirpath, filename)
            try:
                with open(filepath, 'r', encoding='utf-8') as f:
                    lines = f.readlines()
                    conflict_lines = []
                    for i, line in enumerate(lines, 1):
                        if conflict_pattern.search(line):
                            conflict_lines.append((i, line.strip()))
                    
                    if conflict_lines:
                        conflicts[filepath] = conflict_lines
            except:
                # Skip files that can't be read as text
                pass
    
    return conflicts

if __name__ == "__main__":
    root = r"c:\Users\Admin\CascadeProjects\PersianAIAssistant"
    
    print("[*] Searching for conflict markers...")
    print("=" * 60)
    
    conflicts = find_conflicts(root)
    
    if conflicts:
        print(f"\n[ERROR] Found conflicts in {len(conflicts)} files:\n")
        for filepath, lines in conflicts.items():
            rel_path = os.path.relpath(filepath, root)
            print(f"File: {rel_path}")
            print(f"   Conflicts at lines: {', '.join(str(l[0]) for l in lines[:5])}")
            if len(lines) > 5:
                print(f"   ... and {len(lines) - 5} more")
            print()
    else:
        print("\n[OK] No conflict markers found!")
    
    print("=" * 60)
    print(f"Total files with conflicts: {len(conflicts)}")
