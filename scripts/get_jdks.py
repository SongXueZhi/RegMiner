import os
import re
import subprocess

    # Define default directories
DEFAULT_LINUX_DIR = '/usr/lib/jvm'
DEFAULT_MAC_DIR = '/Library/Java/JavaVirtualMachines'
DEFAULT_WIN_DIR = 'C:\\Program Files\\Java'
DEFAULT_OUT_FILE = 'env.properties'

# Default directories based on OS
DEFAULT_DIRS = {
    'Linux': DEFAULT_LINUX_DIR,
    'Darwin': DEFAULT_MAC_DIR,
    'Mac': DEFAULT_MAC_DIR,
    'Windows': DEFAULT_WIN_DIR,
    'nt': DEFAULT_WIN_DIR
}

# Function to get JDK directory
def get_jdk_dir(osname, custom_dir=None):
    return custom_dir if custom_dir else DEFAULT_DIRS[osname]

# Function to get JDK version
def get_jdk_version(java_bin_path):
    try:
        result = subprocess.run([java_bin_path, '-version'], stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        if result.returncode == 0:
            version_info = re.search(r'version "(.*?)"', result.stderr)
            if version_info:
                return version_info.group(1)
    except Exception as e:
        print(f"Error getting version for {java_bin_path}: {e}")
    return "Unknown"

# Main function to scan JDKs
def scan_jdks(custom_dir=None):
    OS = os.name
    if OS == 'posix':
        OS = os.uname().sysname
    elif OS != 'nt':
        print("Unsupported operating system")
        return

    jdk_root_dir = get_jdk_dir(OS, custom_dir)
    java_dirs = []
    if OS in ['Linux', 'Windows', 'nt']:
        java_dirs = [d for d in os.listdir(jdk_root_dir) if
                    os.path.isdir(os.path.join(jdk_root_dir, d)) and ('jdk' in d or 'java' in d) and os.path.exists(
                        os.path.join(jdk_root_dir, d, 'bin'))]
    elif OS == 'Darwin':
        java_dirs = [d for d in os.listdir(jdk_root_dir) if os.path.isdir(
            os.path.join(jdk_root_dir, d)) and ('jdk' in d or 'java' in d) and os.path.exists(
            os.path.join(jdk_root_dir, d, 'Contents/Home/bin'))]

    version_pattern = re.compile(r'(?:1\.)?(\d+)')
    jdk_map = {}
    for jd in java_dirs:
        full_path = os.path.join(jdk_root_dir, jd)
        if OS == 'Darwin' and 'Contents' in os.listdir(full_path):
            full_path = os.path.join(full_path, 'Contents/Home')

        java_bin_path = os.path.join(full_path, 'bin/java')
        if os.path.exists(java_bin_path):
            version = get_jdk_version(java_bin_path)
            match = version_pattern.search(version)
            if match:
                major_version = match.group(1)
                jdk_key = f"j{major_version}"
                jdk_map[jdk_key] = full_path

    supported_jdks = ["j7", "j8", "j11", "j17"]
    with open(DEFAULT_OUT_FILE, 'a') as f:
        f.write("\n")
        for jdk_key, path in jdk_map.items():
            if jdk_key in supported_jdks:
                f.write(f'{jdk_key}_file={path}\n')

# Example usage
# scan_jdks("/custom/path")  # Provide a custom path if needed
