# Run this script before running the whole project in order to scan jdks and write them to config file
# Write the absolute path for each jdk
import os
import re
import sys
import subprocess
DEFAULT_LINUX_DIR = '/usr/lib/jvm'
DEFAULT_MAC_DIR = '/Library/Java/JavaVirtualMachines'
DEFAULT_WIN_DIR = 'C:\\Program Files\\Java'
DEFAULT_OUT_FILE = 'env.properties'

DEFAULT_DIRS = {
    'Linux': DEFAULT_LINUX_DIR,
    'Darwin': DEFAULT_MAC_DIR,
    'Mac': DEFAULT_MAC_DIR,
    'Windows': DEFAULT_WIN_DIR,
    'nt': DEFAULT_WIN_DIR
}


def get_jdk_dir(osname):
    if len(sys.argv) > 1:
        return sys.argv[1]
    return DEFAULT_DIRS[osname]

def get_jdk_version(java_bin_path):
    try:
        # Execute the java -version command and capture stderr
        result = subprocess.run([java_bin_path, '-version'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        if result.returncode == 0:
            # Parse stderr output to get version information
            version_info = re.search(r'version "(.*?)"', result.stderr)  # Changed from result.stdout to result.stderr
            if version_info:
                return version_info.group(1)
    except Exception as e:
        print(f"Error getting version for {java_bin_path}: {e}")
    return "Unknown"


jdk_root_dir = ""
OS = "Windows"  # win as default!
java_dirs = []

# the path should contain jdk or java, then the directory should also include bin(sub-dir) directly
# mac: /Library/Java/JavaVirtualMachines/jdk1.8.0_291.jdk/Contents/Home
# linux: /usr/lib/jvm/java-8-openjdk-amd64

# check os type
if os.name == 'posix':
    OS = os.uname().sysname
    print(OS)
    jdk_root_dir = get_jdk_dir(OS)
    print("Scanning in dir:", jdk_root_dir)
    if OS == 'Linux':
        java_dirs = [d for d in os.listdir(jdk_root_dir) if
                     os.path.isdir(os.path.join(jdk_root_dir, d)) and ('jdk' in d or 'java' in d) and os.path.exists(
                         os.path.join(jdk_root_dir, d, 'bin'))]
    elif OS == 'Darwin':
        java_dirs = [d for d in os.listdir(jdk_root_dir) if os.path.isdir(
            os.path.join(jdk_root_dir, d)) and ('jdk' in d or 'java' in d) and os.path.exists(
            os.path.join(jdk_root_dir, d, 'Contents/Home/bin'))]
    else:
        print(f"Unsupported operating system: {OS}")
        exit(1)

elif os.name == 'nt':  # Windows
    print(OS)
    jdk_root_dir = get_jdk_dir(OS)
    print("Scanning in dir:", jdk_root_dir)
    java_dirs = [d for d in os.listdir(jdk_root_dir) if
                 os.path.isdir(os.path.join(jdk_root_dir, d)) and ('jdk' in d or 'java' in d) and os.path.exists(
                     os.path.join(jdk_root_dir, d, 'bin'))]

else:
    print("Unsupported operating system")
    exit(1)

# regex for matching jd version
# version_pattern = re.compile(r'(?:jdk|java?)(?:-|)(\d+)(?:\.(\d+))?(?:\.(\d+))?(?:_.+)?')
version_pattern = re.compile(r'(?:1\.)?(\d+)')
# print the jdk dir and version
print("Found JDK installations:")
jdk_map = {}
# 在循环中调用新函数
for jd in java_dirs:
    full_path = os.path.join(jdk_root_dir, jd)

    # 对于 macOS, 添加 'Contents/Home'
    if OS == 'Darwin' and 'Contents' in os.listdir(full_path):
        full_path = os.path.join(full_path, 'Contents/Home')

    java_bin_path = os.path.join(full_path, 'bin/java')
    if os.path.exists(java_bin_path):
        version = get_jdk_version(java_bin_path)
        print(f"- {full_path} (Version: JDK {version})")

        # Simplified version pattern match
        match = version_pattern.search(version)
        if match:
            # Extract major version number
            major_version = match.group(1)
            # Convert to 'J' format (e.g., '6' becomes 'J6')
            jdk_key = f"J{major_version}"
            # Choose the JDK path for this version (replace if already exists)
            jdk_map[jdk_key] = full_path
        else:
            print(f"- {full_path} (Cannot determine JDK major version)")
    else:
        print(f"- {full_path} (Not a valid JDK installation)")

# Writing to config file
with open(DEFAULT_OUT_FILE, 'w') as f:  # Use 'w' to overwrite existing content
    for jdk_key, path in jdk_map.items():
        f.write(f'{jdk_key}_file={path}\n')



# if no jdk for a specific version, write a default
# eg: if jdk6 not exists and jdk7 exists, jdk6 will be set as jdk7
with open(DEFAULT_OUT_FILE, 'a') as f:
    not_found = []
    for i in range(6, 21):
        if str(i) in jdk_map.keys():
            for j in not_found:
                f.write(f'j{j}_file={jdk_map[str(i)]}\n')
            not_found = []
            f.write(f'j{i}_file={jdk_map[str(i)]}\n')
        else:
            not_found.append(str(i))
