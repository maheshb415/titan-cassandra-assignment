import sys
import httplib2
import json

def get_tweets_for_users(user_file, output_file, is_screen_name):
    fout = open(output_file, "w")
    fin = open(user_file, "r")
    key = "id"
    if is_screen_name == "yes":
        key = "screen_name"
        
    for sname in fin:
        #sname = sname[0:-1]
        print sname
        if sname[0:1] == '#':
            continue
        try:
            req = "https://api.twitter.com/1/statuses/user_timeline.json?include_entities=true&include_rts=true&{0}={1}&count=100".format(key, sname[0:-1])
            print "req => ", req
            resp, content = httplib2.Http().request(req)
            print "status => ", resp['status']
            if(resp['status'] == '200'):
                j = json.loads(content)
                for tweet in j:
                    print sname, tweet['id'], tweet['text'], tweet['text'].encode('ascii', 'ignore'), tweet['created_at'] 
                    line = "{0}\t{1}\t{2}\t{3}\n".format(sname[0:-1], tweet['id'], tweet['text'].encode('ascii', 'ignore'), tweet['created_at'])
                    print line 
                    fout.write(line)
        except:
            print "Unexpected error:", sys.exc_info()[0]
    fin.close()
    fout.close()
            
        
if __name__ == '__main__':
    print sys.argv[1], sys.argv[2], sys.argv[3]
    get_tweets_for_users(sys.argv[1], sys.argv[2], sys.argv[3])

