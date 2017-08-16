# Rule-based Fact Checking#

This program takes as input candidate facts, Knowledge Graph (KG), set of Horn rules and Text corpus and finds the set of evidences supporting or refuting the candidate facts.


## Installation ##

### Dependencies ###

This project depends on 2 other projects:
  * FactChecking:  The code is included
  * IRIS-Reasoner: we use libs files built from https://github.com/NICTA/iris-reasoner
  
  To load the libraries into mvn repositry:
  
  `sh install_external_jars.sh`
  
### Packaging ###  

 Then to generarte scripts and the jar package execute these commands in the project root directory:
 
 ```
 mvn clean
 mvn package -Dmaven.test.skip=true
 mvn install -Dmaven.test.skip=true
 ```
 ## Configuration ##
 
 There are 2 main configiration files 
 * Rewriting Configuration: `QueryRewriting/src/main/resources/fact_checking_rewriting.properties`
 * Fact Spotting Configuration: `FactSpotting/src/main/resources/factchecking.properties`
 
 ## Run ##
 
 The main running scripts are generated into queryrewriting/assembler/bin folder.
 
 To spot facts with the rewriting process, we run:
 
 `sh QueryRewriting/assemble/bin/check_facts.sh`
 

 
 
 
 
