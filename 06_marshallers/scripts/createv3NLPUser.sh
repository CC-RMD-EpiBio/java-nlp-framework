# -------------------------------------
# This script kicks off a script to create 
# the v3NLP mysql user.
# 
# This script assumes that mysqld (the server)
# is running, and that there is a root user
# and the root user has a password that you 
# will provide interactively.
#
# This script assumes that mysql is in the
# command line path.
#
# -------------------------------------
mysql --user=root mysql -p < createv3NLPUser.sql


