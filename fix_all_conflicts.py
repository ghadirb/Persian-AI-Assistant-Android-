#!/usr/bin/env python3
import os
import re

def fix_conflicts_in_file(filepath):
    """Remove git conflict markers from a file"""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # Remove conflict markers and keep HEAD version
        lines = content.split('\n')
        result = []
        skip_mode = None
        
        for line in lines:
            if line.startswith('<<<<<<< HEAD'):
                skip_mode = None  # Keep HEAD content
            elif line.startswith('======='):
                skip_mode = 'skip'  # Start skipping non-HEAD content
            elif line.startswith('>>>>>>> '):
                skip_mode = None  # End of conflict
            elif skip_mode != 'skip':
                result.append(line)
        
        # Write back
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write('\n'.join(result))
        
        return True
    except Exception as e:
        print(f"Error processing {filepath}: {e}")
        return False

if __name__ == "__main__":
    # Only fix important build files
    important_files = [
        r"app\src\main\java\com\example\persianaiapp\di\DatabaseModule.kt",
        r"app\src\main\java\com\example\persianaiapp\di\RepositoryModule.kt",
        r"app\src\main\java\com\example\persianaiapp\ui\chat\ChatViewModel.kt",
    ]
    
    root = r"c:\Users\Admin\CascadeProjects\PersianAIAssistant"
    
    print("[*] Fixing conflicts in important files...")
    print("=" * 60)
    
    fixed_count = 0
    for rel_path in important_files:
        filepath = os.path.join(root, rel_path)
        if os.path.exists(filepath):
            if fix_conflicts_in_file(filepath):
                print(f"[OK] Fixed: {rel_path}")
                fixed_count += 1
            else:
                print(f"[ERROR] Failed: {rel_path}")
        else:
            print(f"[WARN] Not found: {rel_path}")
    
    print("=" * 60)
    print(f"Fixed {fixed_count} files")
