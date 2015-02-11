#!/bin/bash
set -e
# @AUTHOR: NISHANT NEERAJ (http://naishe.in)
# THIS IS THE FILE THAT YOU EXECUTE LOCALLY
# YOU MUST FILL UP THE APPROPRIATE VARIABLES
# YOU MAY NEED TO CHANGE THIS SCRIPT UPLOAD 
# SNITCH FILES, IF ANY

# LOCATION TO USE IDENTITY FILE FROM
identity_file="/LOCATION/OF/IDENTITY_FILE.pem"

# REMOTE USER TO LOG IN AS
remote_user="root"

#YOU MAY CHANGE LOCATION LIKE: "${HOME}/Desktop/install_cassandra.sh"
install_script="install_cassandra.sh" 

# YOUR SERVERS WHERE CASSANDRA IS TO BE INSTALLED
servers=( 'c1.mydomain.com' 'c2.mydomain.com' 'c2.mydomain.com' )

for server in ${servers[@]} ; do
  echo ">> Uploading script to ${server} to remote user's home"
  scp -i ${identity_file} ${install_script} ${remote_user}@${server}:~/install_cassandra.sh
  echo ">> Executing script..."
  ssh -t -i ${identity_file} ${remote_user}@${server} "sudo chmod a+x ~/install_cassandra.sh && ~/install_cassandra.sh"
  echo ">> Installation finished for server: ${server}"
  echo "----------------------------------------------"
done
echo ">> Cluster initialization is finished."
exit 0;
