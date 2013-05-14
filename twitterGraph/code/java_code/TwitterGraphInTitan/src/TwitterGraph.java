import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.Query.Compare;


public class TwitterGraph {
	
	
	//Loads the Twitter dataset into TitanGraph
	//Loads Users, Followers and Tweets
	public static void LoadTwitterGraph(TitanGraph g, String usersDataFile, String followersDataFile, String tweetsDataFile, boolean doCommit)
	{		
		
		g.createKeyIndex(TwitterGraphConstants.USERID, Vertex.class);	
		g.createKeyIndex(TwitterGraphConstants.TWEETID, Vertex.class);	

		String twitterUsersDataFile = usersDataFile;
		TwitterGraph.LoadUsers(twitterUsersDataFile, g);
							
		String twitterUsersFollowersDataFile = followersDataFile;
		TwitterGraph.LoadFollowersData(twitterUsersFollowersDataFile, g);
		
		String twitterTweetsDataFile = tweetsDataFile;
		TwitterGraph.LoadTweetsData(twitterTweetsDataFile, g);
		
		if(doCommit)
		{
			g.commit();
		}
		
	}
	
	//util function
	//to get the epoch corresponding to a given date string 
	//formatStr is the format for the datetime string. for example => "EEE MMM d HH:mm:ss yyyy" or "yyyy-MM-dd HH:mm:ss" etc
	//dateStr should be in the format specified by formatStr. If its not in that format the epoch for UTCNOW is returned
	static long GetEpoch(String dateStr, String formatStr)
	{
		SimpleDateFormat df = new SimpleDateFormat(formatStr);
		
	    Date date;
	    long epoch = -1;
		try {
			
			date = df.parse(dateStr);
			epoch = date.getTime();
			//System.out.println(String.format("Setting Epoch: dateStr:%s Epoch:%s", date, epoch));		    
		    
		} catch (ParseException e) {
			//could not parse dateStr, so use current UTC time
			
			e.printStackTrace();
			DateTime datetime = DateTime.now(DateTimeZone.UTC);
			epoch = datetime.getMillis();
			//System.out.println(String.format("Setting Milliseconds: datetime:%s Epoch:%s", datetime, epoch));			
			
		} 	
		return epoch;
	}
		
	//load twitter users data from a file into given TitanGraph. 
	//Format of each line in file => uid<tab>screenname<tab>uname<tab>createtime<tab>followercount
	static void LoadUsers(String twitterUsersFile, TitanGraph g)
	{
		System.out.println("Loading Users Data");
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(twitterUsersFile));
 
			while ((sCurrentLine = br.readLine()) != null) {
				
				try {
					//System.out.println(sCurrentLine);
					
					//ignore schema line
					if(sCurrentLine.startsWith("#")) {
						continue;
					}
					
					String[] parts = sCurrentLine.split("\t");
					
					if(parts.length != 5) {
						continue;
					}
					
					long userId = Long.parseLong(parts[0]);
					
					//Check if user vertex already exists. if it exists then its properties will be overwritten
					Vertex user;
									
					try {
						
						user = g.getVertices(TwitterGraphConstants.USERID, userId).iterator().next();
											
					} catch (java.util.NoSuchElementException e1) {					
						System.out.println(String.format("Did not find user uid:%s", userId));
						//create new user
						user = g.addVertex(null); 
					}
									
					String userScreenName = parts[1];
					String userName = parts[2];
					String userCreateTime = parts[3];
					
					System.out.println(String.format("Setting values UserId:%s userScreenName:%s userName:%s userCreateTime:%s", userId, userScreenName, userName, userCreateTime));
					
					user.setProperty(TwitterGraphConstants.USERID, userId);
					user.setProperty(TwitterGraphConstants.USERSCREENNAME, userScreenName);
					user.setProperty(TwitterGraphConstants.USERNAME, userName);
					
					//set user create time				
					long epoch = GetEpoch(userCreateTime, "EEE MMM d HH:mm:ss yyyy");
					user.setProperty(TwitterGraphConstants.USERCREATETIME, epoch);
					
					System.out.println(String.format("Set values UserId:%s userScreenName:%s userName:%s userCreateTime:%s", 
							user.getProperty(TwitterGraphConstants.USERID),
							user.getProperty(TwitterGraphConstants.USERSCREENNAME),
							user.getProperty(TwitterGraphConstants.USERNAME),
							user.getProperty(TwitterGraphConstants.USERCREATETIME)));
					
				} catch (Exception e) {
					
					e.printStackTrace();
				}
						
			}
 
		} catch (IOException e) {
			
			e.printStackTrace();
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		finally {
			
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
		}
		System.out.println("Finished Loading Users Data");
	}
	
	//load twitter follower data from a file. 
	//format of each line in file => followerUID<tab>followedUID<tab>followtime
	static void LoadFollowersData(String twitterUsersFollowersDataFile, TitanGraph g)
	{
		System.out.println("Loading Followers Data");
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(twitterUsersFollowersDataFile));
 
			while ((sCurrentLine = br.readLine()) != null) {
				//System.out.println(sCurrentLine);
				if(sCurrentLine.startsWith("#")) {
					continue;
				}
				String[] parts = sCurrentLine.split("\t");
				
				if(parts.length != 3) {
					continue;
				}
				
				long followerUID = Long.parseLong(parts[0]);
				long followedUID = Long.parseLong(parts[1]);
				String followTime = parts[2];
				System.out.println(String.format("Setting values followerUID:%s followedUID:%s followTime:%s", followerUID, followedUID, followTime));
				
				
				Vertex followerUser;
				Vertex followedUser;
				try {
					
					followerUser = g.getVertices(TwitterGraphConstants.USERID, followerUID).iterator().next();
					followedUser = g.getVertices(TwitterGraphConstants.USERID, followedUID).iterator().next();
					
				} catch (java.util.NoSuchElementException e1) {
					
					System.out.println(String.format("Did not find users followerUID:%s followedUID:%s", followerUID, followedUID));
					
					continue;
				}
				
				
				Edge follows = g.addEdge(null, followerUser, followedUser, TwitterGraphConstants.FOLLOWSEDGETYPE);
				Edge followedby = g.addEdge(null, followedUser, followerUser, TwitterGraphConstants.FOLLOWEDBYEDGETYPE);
				
				// set follow time
				long epoch = GetEpoch(followTime, "yyyy-MM-dd HH:mm:ss");
				follows.setProperty(TwitterGraphConstants.STARTFOLLOWTIME, epoch);
				followedby.setProperty(TwitterGraphConstants.STARTFOLLOWTIME, epoch);				
			}
 
		} catch (IOException e) {
			
			e.printStackTrace();
			
		} catch (Exception e) {
			
			e.printStackTrace();	
			
		} finally {
			
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
		}		
		System.out.println("Finished Loading Followers Data");
	}
	
	//load twitter tweets data from a file. 
	//format of each line in file => uid<tab>tweetid<tab>text<tab>tweettime
	
	static void LoadTweetsData(String twitterTweetsDataFile, TitanGraph g)
	{
		System.out.println("Loading Tweets Data");
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(twitterTweetsDataFile));
 
			while ((sCurrentLine = br.readLine()) != null) {
				
				try {
					//System.out.println(sCurrentLine);
					if(sCurrentLine.startsWith("#")) {
						continue;
					}
					String[] parts = sCurrentLine.split("\t");
					
					if(parts.length != 4) {
						continue;
					}
					
					long uid = Long.parseLong(parts[0]);
					long tweetid = Long.parseLong(parts[1]);
					
					String text = parts[2];
					
					String tweetTime = parts[3];
					
					System.out.println(String.format("Setting values uid:%s tweetid:%s text:%s tweetTime:%s", 
							uid, tweetid, text, tweetTime));				
					
					//Check if tweet vertex already exists. if it exists then its properties will be overwritten
					Vertex tweet;
									
					try {
						
						tweet = g.getVertices(TwitterGraphConstants.TWEETID, tweetid).iterator().next();
											
					} catch (java.util.NoSuchElementException e1) {					
						System.out.println(String.format("Did not find tweet tweetid:%s", tweetid));
						//create new tweet 
						tweet = g.addVertex(null); 
					}
					
									
					tweet.setProperty(TwitterGraphConstants.TWEETID, tweetid);
					tweet.setProperty(TwitterGraphConstants.TEXT, text);
					
					// set Tweet time
					long epoch = GetEpoch(tweetTime, "EEE MMM d HH:mm:ss yyyy");
					tweet.setProperty(TwitterGraphConstants.TWEETTIME, epoch);	
					
					//Get user vertex
					Vertex user;
									
					try {
						
						user = g.getVertices(TwitterGraphConstants.USERID, uid).iterator().next();
											
					} catch (java.util.NoSuchElementException e1) {
						
						System.out.println(String.format("Did not find user uid:%s", uid));
						
						continue;
					}
					
					//create the Tweets edge
					Edge tweets = g.addEdge(null, user, tweet, TwitterGraphConstants.TWEETSEDGETYPE);	
					
				} catch (Exception e) {
					
					e.printStackTrace();	
					
				}
						    
			}
 
		} catch (IOException e) {
			
			e.printStackTrace();
			
		} catch (Exception e) {
			
			e.printStackTrace();	
			
		} finally {
			
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
		}
		
		System.out.println("Finished Loading Tweets Data");
	}
	
	//function to print all followers of a user after specific time
	public static void PrintAllFollowersAfterGivenDateTime(TitanGraph g, long userId, long epoch) {
		
		
		Vertex v = null;
		long uid = 0, createtime;
		String screenname = null,uname = null;
		try {
			v = g.getVertices(TwitterGraphConstants.USERID, userId).iterator().next();
			
			uid = v.getProperty(TwitterGraphConstants.USERID);
			screenname = v.getProperty(TwitterGraphConstants.USERSCREENNAME);
			uname = v.getProperty(TwitterGraphConstants.USERNAME);
			createtime = v.getProperty(TwitterGraphConstants.USERCREATETIME);
			
			System.out.println(String.format("User Values UserId:%s userScreenName:%s userName:%s userCreateTime:%s", 
					uid,
					screenname,
					uname,
					createtime));
		} catch (java.util.NoSuchElementException e1) {
			e1.printStackTrace();
		}			
		
		System.out.println(String.format("The followers for User with uid:%s screenname:%s uname:%s after epoch:%s are as follows", uid, screenname, uname, epoch)); 
		System.out.println("uid\tscreenname\tusername");		
		for(Vertex vertex : v.query().labels(TwitterGraphConstants.FOLLOWEDBYEDGETYPE).has(TwitterGraphConstants.STARTFOLLOWTIME, epoch, Compare.GREATER_THAN).vertices()) { 
			  System.out.println(String.format("%s\t%s\t%s", vertex.getProperty(TwitterGraphConstants.USERID), vertex.getProperty(TwitterGraphConstants.USERSCREENNAME), vertex.getProperty(TwitterGraphConstants.USERNAME))); 
		}
	}


}