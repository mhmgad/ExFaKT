# Rule-based Fact Checking #

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
 * Fact Spotting Configuration: `FactChecking/src/main/resources/factchecking.properties`
 
 Sample of the configurations can be find in '/src/main/resources/sample_conf' folders. Most of the paramters can be overritten  by the CLI arguments.
 
 ## Run ##
 
 The main running scripts are generated into queryrewriting/assembler/bin folder.
 
 ### Checking facts ###
 
 To spot facts with the rewriting process, we run:
 
 `sh QueryRewriting/assemble/bin/check_facts.sh`
 
 Arguments
 ```
 usage: check_facts.sh [options]
  -cf,--ckeckFact                                Check correctness of a fact
  -conf,--configurationFile <file>               Input configuration file
  -eval,--EvaluationMethod <method>              Evaluation Method
  -f,--factsFiles <file>                         Facts File
  -h,--help                                      Show Help
  -o,--outputFile <file>                         Output File
  -q,--queryFiles <file>                         Query File in IRIS format
  -r,--rulesFiles <file>                         Rules File
  -spotConf,--spottingConfigurationFile <file>   Spotting configuration file
 ```
 
  ### Evaluate fact checking rankings ###
  
  `sh QueryRewriting/assemble/bin/eval_labels.sh <column with ranking score> <column with labels> <input file>`
  
  
 
 
 
 
 

 
 
 
 
