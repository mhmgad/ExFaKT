# ExFaKT #

This program takes as input candidate facts, Knowledge Graph (KG), set of Horn rules and Text corpus and finds the set of evidences supporting or refuting the candidate facts.

This is source code for   
***ExFaKT: A framework for explaining facts over knowledge graphs and text**,   
Mohamed Gad-Elrab, Daria Stepanova, Jacopo Urbani, Gerhard Weikum  
In 12th International Conference on Web Search and Data Mining, 87-95, ACM 2019*.

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
 
 ## Run as webservice (for the demo) ##
 
 ### Start the Web Service ###
 
1. clone with git clone --recurse-submodules https://github.com/mhmgad/ExFaK
2. Download Elasticsearch 5.3 https://www.elastic.co/downloads/past-releases/elasticsearch-5-3-1
3. Download Elasticsearch indexed data and extract them:http://resources.mpi-inf.mpg.de/d5/exfakt/esData.tar.gz  
this includes Yago related index and Wikipedia_sentences
4. change "path.data:" in the folder elasticsearch-5.3.1/config/elasticsearch.yml to the extracted esData folder
`path.data:<path>/esData`
5. start Elasticsearch `./<elasticsearch_path>/bin/elasticsearch`

6. Then you can directly run the webservice using
`sh ./scripts/run_webservice2.sh elasticsearch_host eleasticsearch_port`

Note: the script assumes that Elasticsearch is running on a different machine and initiate a ssh tunnel, you can skip it if both are on the same machine.

### Java Client ###

You can use the [java client](./Client/src/main/java/client/explain/ExplanationExtractorClient.java) to call explanation API.
 
 ## Run CLI ##
 
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
  
  
 
 
 
 
 

 
 
 
 
