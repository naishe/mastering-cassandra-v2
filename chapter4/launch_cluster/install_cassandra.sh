#!/bin/bash
set -e
# @AUTHOR: NISHANT NEERAJ (http://naishe.in)
# This script does the following:
# 1. download cassandra
# 2. create directories
# 3. updates cassandra.yaml with 
#      cluster_name
#      seeds
#      listen_address
#      rpc_address
#      initial_token
#      endpoint_snitch
#      data dir, commit log dir and logging dir
# 4. start Cassandra
# YOU MUST CHANGE THE VARIABLES TO ADAPT YOUR 
# CONFIGURATION OF THE NODE

#SYNOPSYS
function printHelp(){
  cat << EOF
Synopsis:
  $0
  
  Downloads, installs, configures and starts Cassandra.
EOF
}

#if [ $# -lt 1 ] ; then
#  printHelp
#  exit 1
#fi

# VARIABLES !!! EDIT AS YOUR CONFIG
# ---------------------------------

# DOWNLOAD URL, GET ONE CLOSEST TO YOUR LOCATION FROM http://cassandra.apache.org/download
download_url='http://www.us.apache.org/dist/cassandra/2.1.0/apache-cassandra-2.1.0-bin.tar.gz'
file_name="cassandra.tar.gz"

# NAME OF THE FOLDER THAT GETS CREATED WHEN UNZIPPED, IT CAN BE GUESSED FROM THE URL
name='cassandra'

# DIRECTORY WHERE CASSANDRA WILL BE INSTALLED TO
install_dir='/opt'

# DATA DIRECTORY
data_dir='/mnt/cassandra-data'

# LOGGING DIRECTORY, YOU NEED NOT SUMP EVERYTHING IN /MNT
logging_dir='/mnt/cassandra-logs'

# CASSANDRA CONFIG !!! EDIT AS YOUR CONFIG
# WATCH FOR DOUBLE QUOTES WITHIN SINGLE QUOTES, THEY ARE INTETIONAL!

cluster_name='"My Cluster"'
seeds='"10.110.6.30"'
listen_address=''
rpc_address=''
endpoint_snitch="Ec2Snitch"

echo "--- DOWNLOADING CASSANDRA"
wget -O /tmp/${file_name} ${download_url}

echo "--- EXTRACTING..."
sudo mkdir ${install_dir}/${name}
sudo tar xzf /tmp/${file_name} -C ${install_dir}/${name} --strip-components 1

#echo "--- SETTING UP SYM-LINK"
#sudo ln -s ${install_dir}/${name} ${install_dir}/cassandra

echo "--- CREATE DIRECTORIES"
sudo mkdir -p ${data_dir}/data ${data_dir}/commitlog ${logging_dir}
sudo chown -R ${USER} ${data_dir} ${logging_dir}

echo "--- UPDATING CASSANRA YAML (in place)"
sudo cp ${install_dir}/cassandra/conf/cassandra.yaml ${install_dir}/cassandra/conf/cassandra.yaml.BKP

sudo sed -i \
  -e "s/^cluster_name.*/cluster_name: ${cluster_name}/g" \
  -e "s/\(\-\s*seeds:\).*/\1 ${seeds}/g" \
  -e "s/^listen_address.*/listen_address: ${listen_address}/g" \
  -e "s/^rpc_address.*/rpc_address: ${rpc_address}/g" \
  -e "s/^endpoint_snitch.*/endpoint_snitch: ${endpoint_snitch}/g" \
  -e "s|^#\s\+data_file_directories:|data_file_directories:\n  - ${data_dir}/data|g" \
  -e "s|^#\s\+commitlog_directory:.*|commitlog_directory: ${data_dir}/commitlog|g" \
  -e "s|^#\s\+saved_caches_directory:.*|saved_caches_directory: ${data_dir}/saved_caches|g" \
  ${install_dir}/cassandra/conf/cassandra.yaml

sudo sed -i \
  -e "s|\${cassandra.logdir}|${logging_dir}|g" \
  ${install_dir}/cassandra/conf/logback.xml
  
echo "--- STARTING CASSANDRA"
# NOHUP, ignore SIGHUP signal to kill Cassandra Daemon
nohup ${install_dir}/cassandra/bin/cassandra > ${logging_dir}/startup.log &
sleep 5

echo "--- INSTALLATION FINISHED"
exit 0
