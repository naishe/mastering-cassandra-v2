MASTERING APACHE CASSANDRA: CHAPTER 4
-------------------------------------

There are two files in this chapter `install_cassandra.sh` and `upload_and_execute.sh`. These are shell scripts created and tested on Ubuntu 14.04, GNU bash 4.2.45.

###How to use them?

These files are basically automated script to launch and setup a complete Cassandra cluster without much human intervention.

**install_cassandra.sh:** This script is responsible for downloading, installing and configuring Cassandra on a machine. _Make sure the parameters are changes as per your configurations_.

**upload_and_execute.sh:** This script is companion script to the previous one. It contains information about the the server host addresses and a password-less key based SSH connectivity details. Based on number of machines in the cluster this script generated initial\_token for each machine. It then uploads the previous script to that machine and execute that script remotely. _Please make sure you change variable to suite your setup._ Specially, Snitch and Seed host address.

I have tested it on AWS, it took about 40 seconds to configure one machine. An eight node cluster Cassandra was launched in about five minutes, up and running, load balanced ready to take load.
