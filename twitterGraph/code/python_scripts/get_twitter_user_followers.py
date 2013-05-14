import sys
import httplib2
import json

def get_followers_data(user_data_file, follower_outputfile, distinct_user_id_outputfile):
    follower_fout = open(follower_outputfile, "w")
    distinct_user_id_fout = open(distinct_user_id_outputfile, "w")
    fin = open(user_data_file, "r")
    key = "id"
    distinct_user_ids = set([])    
    for line in fin:
        line = line[0:-1]
        if line[0:1] == '#':
            continue
        print line
        parts = line.split('\t')
        print "parts", parts  
        uid = parts[0]      
        distinct_user_ids.add(uid) 
        followers_count = parts[4]
        followers_to_take = int(followers_count) / 2000000
        print "uid", uid, "followers_count", followers_count, "followers_to_take", followers_to_take  
        resp, content = httplib2.Http().request("https://api.twitter.com/1/followers/ids.json?cursor=-1&{0}={1}".format(key, uid))
        print "HTTP status => ", resp['status']
        if(resp['status'] == '200'):
            j = json.loads(content)
            for follower in j['ids'][0:followers_to_take]:
                distinct_user_ids.add(follower) 
                follower_fout.write("{0}\t{1}\n".format(follower, uid))

    fin.close()
    follower_fout.close()

   
    for uid in distinct_user_ids:
        distinct_user_id_fout.write("{0}\n".format(uid))
        
    distinct_user_id_fout.close()
            
        
if __name__ == '__main__':
    print sys.argv[1], sys.argv[2], sys.argv[3]
    get_followers_data(sys.argv[1], sys.argv[2], sys.argv[3])

