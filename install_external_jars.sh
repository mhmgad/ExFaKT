#!/usr/bin/env bash

sh ./FactChecking/install_external_jars.sh

mvn install:install-file -Dfile=./libs/iris-impl-0.8.2.jar -DgroupId=at.sti2.iris -DartifactId=iris-impl -Dversion=0.8.2 -Dpackaging=jar
mvn install:install-file -Dfile=./libs/iris-api-0.8.2.jar -DgroupId=at.sti2.iris -DartifactId=iris-api -Dversion=0.8.2 -Dpackaging=jar

#Ambiverse

#git submodule update --init --recursive
#
#pushd external/ambiverse_client
#
#cp resources/client_secrets_template.json resources/client_secrets.json


#sed -i -e 's/foo/bar/g' filename