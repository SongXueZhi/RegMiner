# Run this script before running the whole project in order to scan jdks and write them to config file
# Write the absolute path for each jdk
import os
import re
import sys

DEFAULT_LINUX_DIR = '/usr/lib/jvm'
DEFAULT_MAC_DIR = '/Library/Java/JavaVirtualMachines'
DEFAULT_WIN_DIR = 'C:\\Program Files\\Java'
DEFAULT_OUT_FILE = '../env.properties'

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
version_pattern = re.compile(r'(?:jdk|java?)(?:-|)(\d+)(?:\.(\d+))?(?:\.(\d+))?(?:_.+)?')

# print the jdk dir and version
print("Found JDK installations:")
jdk_map = {}
for jd in java_dirs:
    full_path = os.path.join(jdk_root_dir, jd)

    # for macOS, add 'Contents/Home' after jdk path
    if OS == 'Darwin' and 'Contents' in os.listdir(full_path):
        full_path = os.path.join(full_path, 'Contents/Home')

    match = version_pattern.match(jd)
    if match:
        version_parts = match.groups()
        if version_parts[0] == '1':
            version = version_parts[1]
        else:
            version = version_parts[0]

    else:
        version = "Unknown"
    if 'bin' in os.listdir(full_path):
        print(f"- {full_path} (Version: JDK {version})")
        jdk_map[version] = full_path
    else:
        print(f"- {full_path} (Version: JDK {version}) (Not a JDK installation)")


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
