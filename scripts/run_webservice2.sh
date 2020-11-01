#!/usr/bin/env bash

SERVER_ELASTIC=$1
PORT=$2
#connect to server hosting ELASTIC
ssh -f $SERVER_ELASTIC -L ${PORT}:localhost:${PORT} -N

# we need locations of data and configurations
DEMO_DATA_FOLDER=$3

echo $DEMO_DATA_FOLDER

parent_path="$(dirname "$(dirname "$(readlink -fm "$0")")")"
#"$(dirname "$dir")"

echo $parent_path



#store a backup from current configurations
#spotting
#mv $parent_path/FactSpotting/src/main/resources/factchecking.properties $parent_path/FactSpotting/src/main/resources/factchecking.properties.tmp
##rewritting
#mv $parent_path/QueryRewriting/src/main/resources/fact_checking_rewriting.properties $parent_path/QueryRewriting/src/main/resources/fact_checking_rewriting.properties.tmp
#
##copy demo properties files
#cp $DEMO_DATA_FOLDER/factchecking.properties $parent_path/FactSpotting/src/main/resources/factchecking.properties
#cp $DEMO_DATA_FOLDER/fact_checking_rewriting.properties $parent_path/QueryRewriting/src/main/resources/fact_checking_rewriting.properties

#install the core

(cd $parent_path && mvn clean && mvn initialize && mvn install -U)
#echo $PWD



##return the configurations
#mv $parent_path/FactSpotting/src/main/resources/factchecking.properties.tmp $parent_path/FactSpotting/src/main/resources/factchecking.properties
#mv $parent_path/QueryRewriting/src/main/resources/fact_checking_rewriting.properties.tmp $parent_path/QueryRewriting/src/main/resources/fact_checking_rewriting.properties
#
#


#run server
cd $parent_path/WebService2
echo $PWD
#mvn clean
#mvn play2:run

export SBT_OPTS="-XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=100G -Xmx100G"


./sbt "run 9350"
