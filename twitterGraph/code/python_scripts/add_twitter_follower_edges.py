import sys
import random
from dateutil.parser import parse
from datetime import timedelta, datetime

def add_twitter_follower_edges(distinct_user_id_file, followers_data_file, user_data_file, follower_data_outputfile):
    follower_fout = open(follower_data_outputfile, "w")
    followers_data_fin = open(followers_data_file, "r")
    distinct_user_id_fin = open(distinct_user_id_file, "r")
    user_data_fin = open(user_data_file, "r")
    
    distinct_follower_data = set([])  

    distinct_user_ids = [] 

    user_createdate = {}

    
    for line in followers_data_fin:
        line = line[0:-1]
        if line[0:1] == '#':
            continue
        parts = line.split('\t')
        print "parts", parts 
        distinct_follower_data.add((parts[0], parts[1]))
          
    for line in distinct_user_id_fin:
        line = line[0:-1]
        if line[0:1] == '#':
            continue
        distinct_user_ids.append(line)
        

    for line in user_data_fin:
        line = line[0:-1]
        if line[0:1] == '#':
            continue
        parts = line.split('\t')
        print "parts", parts 
        user_createdate[parts[0]] = parse(parts[3])
        print user_createdate[parts[0]]

    followers_data_fin.close()
    distinct_user_id_fin.close()
    user_data_fin.close()

    num_u = len(distinct_user_ids)
   
    for uid in distinct_user_ids:
        num_followers = random.randint(0, 100)
        for f in range(0, num_followers):
            index = random.randint(0, num_u-1)
            if distinct_user_ids[index] != uid:
                distinct_follower_data.add((distinct_user_ids[index], uid))
        
    for (a,b) in distinct_follower_data:
        print "(",a,",",b,")"
        num_days_delay_for_following = random.randint(0, 1000)
        try:
            time_to_start_follow = user_createdate[b] + timedelta(days=num_days_delay_for_following)
            follower_fout.write("{0}\t{1}\t{2}\n".format(a,b,time_to_start_follow))
        except:
            print "Unexpected error:", sys.exc_info()[0]
    follower_fout.close()
            
        
if __name__ == '__main__':
    print sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4]
    add_twitter_follower_edges(sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4])

