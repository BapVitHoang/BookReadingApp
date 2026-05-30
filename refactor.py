import os
import re

base_pkg = 'com.hcmute.bookreadingapp'
base_dir = r'D:\SPKT_2023\year_3\MobilePro\BookReadingApp\app\src\main\java\com\hcmute\bookreadingapp'

files_to_move = {
    'AudioPlayerActivity.java': 'ui/audio',
    'PodCourseDetailActivity.java': 'ui/audio',
    'PodCourseFragment.java': 'ui/audio',
    'BookDetailActivity.java': 'ui/reader',
    'ReadingActivity.java': 'ui/reader',
    'LoginActivity.java': 'ui/auth',
    'RegisterActivity.java': 'ui/auth',
    'MainActivity.java': 'ui/main',
    'ExploreFragment.java': 'ui/main',
    'LibraryFragment.java': 'ui/main',
    'ProfileFragment.java': 'ui/main',
    'ChallengesFragment.java': 'ui/main',
    'BooksFragment.java': 'ui/main',
}

for subdir in ['ui/audio', 'ui/reader', 'ui/auth', 'ui/main', 'data/api', 'data/local', 'data/repository', 'service', 'model', 'viewmodel', 'utils']:
    os.makedirs(os.path.join(base_dir, subdir), exist_ok=True)

file_contents = {}
for file, subpkg in files_to_move.items():
    path = os.path.join(base_dir, file)
    if os.path.exists(path):
        with open(path, 'r', encoding='utf-8') as f:
            file_contents[file] = f.read()

for file, content in file_contents.items():
    subpkg = files_to_move[file]
    new_pkg = f"{base_pkg}.{subpkg.replace('/', '.')}"
    
    content = re.sub(r'package ' + base_pkg + r';', f'package {new_pkg};\n\nimport {base_pkg}.R;', content)
    
    imports_to_add = set()
    for other_file, other_subpkg in files_to_move.items():
        if other_file == file: continue
        class_name = other_file.replace('.java', '')
        if class_name in content:
            imports_to_add.add(f"import {base_pkg}.{other_subpkg.replace('/', '.')}.{class_name};")
    
    if imports_to_add:
        pkg_statement = f'package {new_pkg};'
        parts = content.split(pkg_statement)
        if len(parts) > 1:
            imports_str = '\n' + '\n'.join(imports_to_add)
            content = parts[0] + pkg_statement + imports_str + parts[1]
    
    new_path = os.path.join(base_dir, subpkg, file)
    with open(new_path, 'w', encoding='utf-8') as f:
        f.write(content)
        
    old_path = os.path.join(base_dir, file)
    if os.path.exists(old_path):
        os.remove(old_path)
        
print('Refactor successful')
