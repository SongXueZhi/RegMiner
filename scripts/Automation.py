# Script to automate the downloading process

import subprocess, secrets, os, logging, sys
from contextlib import contextmanager
from multiprocessing import Pool

PROJECT_NAME = "couchbase/couchbase-java-client"
ROOT_DIR = os.getcwd()
MINER_DIR = "miner_jar"
SQL_SUPPORT = False # server not working at the moment
MAX_PROCESS = 2

logging.basicConfig(level=logging.INFO, format="%(levelname)s - %(message)s")

class NotSupportedError(RuntimeError):pass

@contextmanager
def change_dir(dir):
    old_dir = os.getcwd()
    os.chdir(dir)
    try:
        yield
    finally:
        os.chdir(old_dir)

def get_clone_link(project_name):
    return f"https://github.com/{project_name}.git"

def run_command(command):
    process = subprocess.run(command, stdout=subprocess.PIPE,
                             stderr=subprocess.STDOUT, shell=True, check=True)
    return process

def download_repository(link):
    logging.info(f"Cloning {link}")
    try:
        process = run_command(f"git clone {link}")
    except subprocess.CalledProcessError:
        raise FileExistsError
    else:
        output = process.stdout.decode()
        output = output[output.find("'") + 1:]
        output = output[:output.find("'")]
        return output

def prepare_files(dir):
    logging.info(f"Preparing {dir}")
    temp_dir = secrets.token_hex(8)
    run_command(f"mv {dir} {temp_dir}")
    run_command(f"mkdir {dir}")
    run_command(f"mv {temp_dir} {dir}/meta")
    return dir

def is_maven(dir):
    files = os.listdir(f"{dir}/meta")
    for file in files:
        if file == "pom.xml":
            return True
    return False

def is_gradle(dir):
    files = os.listdir(f"{dir}/meta")
    for file in files:
        if file == "build.gradle":
            return True
    return False

def generate_config(dir):
    with open(f"{dir}/{MINER_DIR}/env.properties", "w+") as f:
        output = ["code_cover=0", "auto_compile=1",
                  f"project_name={dir}", f"root_dir={ROOT_DIR}"]

        if is_maven(dir):
            output.append("### Maven Configuration ###")
            output.append("command_line=mvn compile test-compile")
            output.append("test_line=mvn test -Dtest=")
            output.append("test_symbol=#")
        elif is_gradle(dir):
            output.append("### Gradle Configuration ###")
            output.append("command_line=./gradlew compileJava compileTestJava")
            output.append("test_line=./gradlew test --tests")
            output.append("test_symbol=.")
        else:
            raise NotSupportedError # not handling multi subproject
        output.append("### SQL Configuration ###")
        if SQL_SUPPORT:
            output.append("sql_enable=1")
            output.append("sql_url=jdbc:mysql://106.12.16.151:3306/regression?-")
            output.append("useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF8")
            output.append("username=root")
            output.append("passwd=123456")
        else:
            output.append("sql_enable=0")
        f.write("\n".join(output))

def prepare_miner(dir):
    run_command(f"cp -r {MINER_DIR} {dir}/.")
    generate_config(dir)

def run_miner(dir):
    logging.info(f"Running miner on {dir}")
    with change_dir(f"{dir}/{MINER_DIR}"):
        run_command("./run.sh")

def process_project(project_name):
    logging.info(f"Processing {project_name}")
    try:
        link = get_clone_link(project_name)
        directory = download_repository(link)
        directory = prepare_files(directory)
        prepare_miner(directory)
        run_miner(directory)
    except NotSupportedError:
        logging.error(f"{project_name} contains multiple subprojects.\
            It is not supported at the moment.")
    except FileExistsError:
        logging.error(f"{project_name} already exists.")
    except Exception as e:
        logging.error(e)
    else:
        logging.info(f"{project_name} done")

def main():
    if len(sys.argv) == 1: 
        raise RuntimeError("Missing file containing projects")
    file_name = sys.argv[1]
    projects = []
    with open(file_name, "r") as f:
        projects = f.readlines()
    projects = [project.strip() for project in projects]
    logging.info("Input file loaded")
    logging.debug(projects)
    logging.info(f"Executing with {MAX_PROCESS} processes")
    with Pool(MAX_PROCESS) as p:
        p.map(process_project, projects)

if __name__ == '__main__':
    main()
