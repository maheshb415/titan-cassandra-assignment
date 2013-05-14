titan-cassandra-assignment
==========================

This repository has code for Twitter Graph implementation in titan-cassandra

Pre-requisites

Linux => I have tested on Ubuntu 12.04
Java => I installed using steps http://www.blogs.digitalworlds.net/softwarenotes/?p=41
Titan Graph database => I installed from http://s3.thinkaurelius.com/downloads/titan/titan-cassandra-0.2.1.zip
Datastax cassandra => I installed from Datastax Enterprise 3.0.1 from http://downloads.datastax.com/enterprise/dse.tar.gz 
Eclipse => Required to open the java project TwitterGraphInTitan. I installed from http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/junosr2  

Organization

screenshots/ => has the screenshots for the various milestones in the assignment
	
twitterGraph => has everything related to twitter graph implementation in titan/cassandra

 	twitterGraph/design/ => has the documents related to design of Twitter Graph
		twitterGraph/design/TwitterGraphModel.txt => has description of TwitterGraph Model in Titan 
			
 	twitterGraph/data/ => has the twitter data used in this assignment
		
	 	twitterGraph/data/twitterDataSets  => the data used in the assignment. Check the first line for the schema of each file
		twitterGraph/data/CommandsToPullDataSets.sh => shell scripts used to generate the above dataset


	twitterGraph/code/ => has the source code for the assignement
	
		twitterGraph/code/python_scripts => has python scripts to pull the datasets in twitterGraph/data/twitterDataSets. Read twitterGraph/data/CommandsToPullDataSets.sh for more info
		twitterGraph/code/java_code => this has the eclipse workspace for java code for TwitterGraph Implementation. Name of Java project is TwitterGraphInTitan

	twitterGraph/config/ => has the config files for the assignment

		twitterGraph/config/titan.cassandra.properties	=> config file for connecting titan to datastax cassandra


	twitterGraph/build/ => has the executable jar file for the assignment
		
		TwitterGraphInTitan.jar => the executable for the assignment. For usage go to the directory twitterGraph/build/ on command prompt and run "java -jar TwitterGraphInTitan.jar" 
		
