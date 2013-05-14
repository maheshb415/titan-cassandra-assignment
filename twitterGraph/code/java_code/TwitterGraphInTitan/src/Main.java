

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.thinkaurelius.titan.core.*; 
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.Query.Compare;

public class Main {	
	
	public static void PrintUsage()
	{
		System.out.println("\nUsage:");
		
		System.out.println("\nUse below command for one time loading the graph\n");
		System.out.println("	java -jar TwitterGraphInTitan.jar load <cassandraConfigFile> <twitterUsersDataFile> <twitterUsersFollowersDataFile> <twitterTweetsDataFile>");
		System.out.println("	Use below sample command if you are running from the directory containing the jar file (titan-cassandra-assignment/twitterGraph/build)");
		System.out.println("	java -jar TwitterGraphInTitan.jar load ../config/titan.cassandra.properties ../data/twitterDataSets/TwitterUsersData.txt ../data/twitterDataSets/TwitterUsersFollowersData.txt ../data/twitterDataSets/TwitterTweetsData.txt");

		System.out.println("\nUse below command for getting followers of a given user\n");
		System.out.println("	java -jar TwitterGraphInTitan.jar getfollowers <cassandraConfigFile> <userId> <afterDateSting> <DateFormat>");
		System.out.println("	Use below sample command if you are running from the directory containing the jar file (titan-cassandra-assignment/twitterGraph/build)");
		System.out.println("	java -jar TwitterGraphInTitan.jar getfollowers ../config/titan.cassandra.properties 813286 \"2013-05-12 00:00:00\" \"yyyy-MM-dd HH:mm:ss\"");
		
	}
	public static void main(String[] args) {
				
		
		if(args.length != 5) {
			PrintUsage();
			return;			
		}
		boolean loadGraph = false;
		boolean getFollowers = false;
		
		String configFile = args[1]; //titan-cassandra connection config file
		
		//needed for load command
		String twitterUsersDataFile = ""; //file containing twitter users data
		String twitterUsersFollowersDataFile = ""; //file containing twitter followers data
		String twitterTweetsDataFile = ""; //file containing twitter tweets data
		
		//needed for getfollowers command
		long uid = 0; //uid whose followers need to be retrieved
		String afterDateStr = "2013-05-12 00:00:00"; //followers need to be retrieved after this date
		String dateFormat = "yyyy-MM-dd HH:mm:ss"; //format of date afterDateStr
		
		if(args[0].equalsIgnoreCase("load")) {	
			//need to load the graph
			twitterUsersDataFile = args[2];
			twitterUsersFollowersDataFile = args[3];
			twitterTweetsDataFile = args[4];	
			loadGraph = true;
		}
		else if(args[0].equalsIgnoreCase("getfollowers"))	{
			uid = Long.parseLong(args[2]);
			afterDateStr = args[3];
			dateFormat = args[4];
			getFollowers = true;
		}
		else {
			System.out.println("Invalid command " + args[0]);
			PrintUsage();
			return;
		}
		
		TitanGraph g = null;		
		
		try {
						
			g = TitanFactory.open(configFile);		
			
			//load the graph, if its needed
			if(loadGraph) {
				TwitterGraph.LoadTwitterGraph(g, twitterUsersDataFile, twitterUsersFollowersDataFile, twitterTweetsDataFile, true);	
			}

			if(getFollowers) {
				/*
				System.out.println(String.format("ToGet All followers of user with id %s and after datetime %s and format %s"
						,uid, afterDateStr, dateFormat));
				*/
				long epoch = TwitterGraph.GetEpoch(afterDateStr, dateFormat);
				TwitterGraph.PrintAllFollowersAfterGivenDateTime(g, uid, epoch);
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally 		{
			g.shutdown();
		}	
	}

}