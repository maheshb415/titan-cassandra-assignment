

public class TwitterGraphConstants {
	
	//property name for User Node
	public static final String USERID = "uid";
	public static final String USERNAME = "uname";
	public static final String USERSCREENNAME = "screenname";
	public static final String USERCREATETIME = "createtime";
	
	//property name for Tweet Node
	public static final String TWEETID = "tweetid";
	public static final String TEXT = "text";
	public static final String TWEETTIME = "tweettime";	
		
	
	//Edge type names
	public static final String FOLLOWSEDGETYPE = "follows"; //between two User Nodes
	public static final String FOLLOWEDBYEDGETYPE = "followedby"; //between two User Nodes
	public static final String TWEETSEDGETYPE = "tweets"; //between a User Node and a Tweet Node
	
	//Edge properties
	public static final String STARTFOLLOWTIME = "followtime"; //for edge types FOLLOWSEDGETYPE and FOLLOWEDBYEDGETYPE
	
}