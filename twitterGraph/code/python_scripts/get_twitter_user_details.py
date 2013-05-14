import sys
import httplib2
import json

def get_user_data(user_file, output_file, is_screen_name):
    fout = open(output_file, "w")
    fin = open(user_file, "r")
    key = "id"
    if is_screen_name == "yes":
        key = "screen_name"
        
    for sname in fin:
        print sname
        try:
            resp, content = httplib2.Http().request("https://api.twitter.com/1/users/show.json?{0}={1}".format(key, sname[0:-1]))
            print "status => ", resp['status']
            if(resp['status'] == '200'):
                j = json.loads(content)
                line = "{0}\t{1}\t{2}\t{3}\t{4}\n".format(j['id'], j['screen_name'], j['name'], j['created_at'], j['followers_count'])
                print line 
                fout.write(line)
        except:
            print "Unexpected error:", sys.exc_info()[0]
    fin.close()
    fout.close()
            
        
if __name__ == '__main__':
    print sys.argv[1], sys.argv[2], sys.argv[3]
    get_user_data(sys.argv[1], sys.argv[2], sys.argv[3])

