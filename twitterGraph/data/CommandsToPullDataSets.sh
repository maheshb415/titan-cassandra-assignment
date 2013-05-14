#Below are the steps to generate the twitter dataset for experimental purpose. Twitter developer APIs are used for pulling the data

#STEP1 => get top 100 twitter celebrities from http://twitaholic.com/

#STEP2 => get the twitter user data of top 10 twitter users
# args[1] => Top10TwitterCelebritiesScreenName.txt => ScreenNames for top 10 twitter users
# args[2] => Top10TwitterCelebritiesUserData.txt => Twitter data for users in Top10TwitterCelebritiesScreenName.txt 
# args[3] => yes/no => "yes" means the file specified by DistinctTwitterUserIds.txt has twitter user's screennames. "no" means the file has user ids 
python ../code/python_scripts/get_twitter_user_details.py twitterDataSets/Top10TwitterCelebritiesScreenName.txt twitterDataSets/Top10TwitterCelebritiesUserData.txt yes

#STEP3 => get the twitter follwers of top 10 twitter users
# args[1] => Top10TwitterCelebritiesUserData.txt => top 10 twitter celebrities data obtained in STEP2
# args[2] => Top10TwitterCelebritiesFollowersData.txt => few followers for each of top 10 twitter users. Number of followers retrieved is = (#actualfollowers/2000000)
# args[3] => DistinctTwitterUserIds.txt => distinct twitter user ids in the file Top10TwitterCelebritiesFollowersData.txt
python ../code/python_scripts/get_twitter_user_followers.py twitterDataSets/Top10TwitterCelebritiesUserData.txt twitterDataSets/Top10TwitterCelebritiesFollowersData.txt twitterDataSets/DistinctTwitterUserIds.txt

#STEP4 => get the twitter user data of all the distinct userids found in STEP3
# args[1] => DistinctTwitterUserIds.txt => Distinct twitter user ids
# args[2] => TwitterUsersData.txt => Twitter data for users in DistinctTwitterUserIds.txt
# args[3] => yes/no => "yes" means the file specified by DistinctTwitterUserIds.txt has twitter user's screennames. "no" means the file has user ids 
python ../code/python_scripts/get_twitter_user_details.py twitterDataSets/DistinctTwitterUserIds.txt twitterDataSets/TwitterUsersData.txt no

#STEP5 => get the twitter tweets data of all the distinct userids found in STEP3. 100 tweets of each user are pulled
# args[1] => DistinctTwitterUserIds.txt => Distinct twitter user ids
# args[2] => TwitterTweetsData.txt => Twitter tweets data for users in DistinctTwitterUserIds.txt
# args[3] => yes/no => "yes" means the file specified by DistinctTwitterUserIds.txt has twitter user's screennames. "no" means the file has user ids 
python ../code/python_scripts/get_twitter_tweets_for_users.py twitterDataSets/DistinctTwitterUserIds.txt twitterDataSets/TwitterTweetsData.txt no

#STEP6 => add more followers to each user so that graph is not too sparse. Upto 100 followers are added for each user, from the list of distinct userids
# args[1] => DistinctTwitterUserIds.txt => Distinct twitter user ids
# args[2] => Top10TwitterCelebritiesFollowersData.txt => followers data for top 10 users which was generated in STEP2. This data is existing followers data. In addition to this more followers are added to each user
# args[3] => TwitterUsersData.txt => Twitter users data which was generated in STEP4. 
# args[4] => TwitterUsersFollowersData.txt => The generated twitter users followers data
python ../code/python_scripts/add_twitter_follower_edges.py twitterDataSets/DistinctTwitterUserIds.txt twitterDataSets/Top10TwitterCelebritiesFollowersData.txt  twitterDataSets/TwitterUsersData.txt twitterDataSets/TwitterUsersFollowersData.txt
