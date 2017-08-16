#!/usr/bin/env bash

sh ./FactChecking/install_external_jars.sh

mvn install:install-file -Dfile=./external_libs/iris-impl-0.8.2-SNAPSHOT.jar -DgroupId=at.sti2.iris -DartifactId=iris-impl -Dversion=-0.8.2-SNAPSHOT -Dpackaging=jar
mvn install:install-file -Dfile=./external_libs/iris-api-0.8.2-SNAPSHOT.jar -DgroupId=at.sti2.iris -DartifactId=iris-api -Dversion=-0.8.2-SNAPSHOT -Dpackaging=jar

#Ambiverse

#git submodule update --init --recursive
#
#pushd external/ambiverse_client
#
#cp resources/client_secrets_template.json resources/client_secrets.json


#sed -i -e 's/foo/bar/g' filename