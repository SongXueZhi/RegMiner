# Python script to generate a configuration file named "env.properties"

from get_jdks import scan_jdks
# Define the content of the configuration file
config_content = """
###SQL ######
sql_enable=1
sql_url=jdbc:mysql://10.176.34.95:3306/code_annotation2?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF8
username=root
passwd=1235
###jenv ######
jenv_enable=0
###enable monitor####
open_monitor=1
###java path ######

"""

# File path

file_path = 'env.properties'

# Writing the content to the file
with open(file_path, 'w') as file:
    file.write(config_content.strip())

scan_jdks()


