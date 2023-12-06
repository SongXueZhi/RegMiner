# Python script to generate a configuration file named "env.properties"

from get_jdks import scan_jdks
# Define the content of the configuration file
config_content = """
###SQL ######
sql_enable=0
sql_url=jdbc:mysql://10.177.21.179:3306/code_annotation2?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=UTF8
username=root
passwd=123456
###jenv ######
jenv_enable=0
###java path ######

"""

# File path

file_path = 'env.properties'

# Writing the content to the file
with open(file_path, 'w') as file:
    file.write(config_content.strip())

scan_jdks()


