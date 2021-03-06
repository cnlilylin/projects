import urllib2
import json
import datetime
import csv
import time
from pprint import pprint

NUM_STATUS=10
APP_ID = ""
APP_SECRET = "" # DO NOT SHARE WITH ANYONE!


page_id = raw_input("Please enter page id: ")
print(page_id)

access_token = "645860552234333|udjs8bUf5j30dNAaGLtjTe8kUyI"
f1_name="./data/"+page_id+"_posts.json"
f2_name="./data/"+page_id+"_comments.json"

f1=open(f1_name, 'w+')
f2=open(f2_name, 'w+')

def request_until_succeed(url):
    req = urllib2.Request(url)
    success = False
    while success is False:
        try: 
            response = urllib2.urlopen(req)
            if response.getcode() == 200:
                success = True
        except Exception as e:
            print e
            time.sleep(5)
            
            print "Error for URL %s: %s" % (url, datetime.datetime.now())

    return response.read()

def get_url(num_statuses, page_id, access_token):
    base = "https://graph.facebook.com/v2.1"
    node = "/%s/feed" % page_id
    fields = "/?fields=message,link,created_time,type,name,id,comments.limit(20).summary(true),shares,reactions.limit(0).summary(true)"
    parameters = "&limit=%s&access_token=%s" % (num_statuses, access_token)
    url = base + node + fields + parameters
    return url

def retrieve_data(num_statuses, page_id, access_token):
    url=get_url(NUM_STATUS, page_id, access_token)
    data = json.loads(request_until_succeed(url))
    print >> f1, json.dumps(data, indent = 4, sort_keys = True)
    return data

def get_reactions(status_id, access_token):
    
    base = "https://graph.facebook.com/v2.6"
    node = "/%s" % status_id
    reactions = "/?fields=" \
                    "reactions.type(LIKE).limit(0).summary(total_count).as(like)" \
                    ",reactions.type(LOVE).limit(0).summary(total_count).as(love)" \
                    ",reactions.type(WOW).limit(0).summary(total_count).as(wow)" \
                    ",reactions.type(HAHA).limit(0).summary(total_count).as(haha)" \
                    ",reactions.type(SAD).limit(0).summary(total_count).as(sad)" \
                    ",reactions.type(ANGRY).limit(0).summary(total_count).as(angry)"
    parameters = "&access_token=%s" % access_token
    url = base + node + reactions + parameters
    
    # retrieve data
    data = json.loads(request_until_succeed(url))
    
    return data

def processFacebookPageFeedStatus(status, access_token):
    
    # The status is now a Python dictionary, so for top-level items,
    # we can simply call the key.
    
    # Additionally, some items may not always exist,
    # so must check for existence first
    
    status_id = status['id']
    status_message = '' if 'message' not in status.keys() else unicode_normalize(status['message'])
    link_name = '' if 'name' not in status.keys() else unicode_normalize(status['name'])
    status_type = status['type']
    status_link = '' if 'link' not in status.keys() else unicode_normalize(status['link'])
    
    # Time needs special care since a) it's in UTC and
    # b) it's not easy to use in statistical programs.
    
    status_published = datetime.datetime.strptime(status['created_time'],'%Y-%m-%dT%H:%M:%S+0000')
    status_published = status_published + datetime.timedelta(hours=-5) # EST
    status_published = status_published.strftime('%Y-%m-%d %H:%M:%S') # best time format for spreadsheet programs
    
    # Nested items require chaining dictionary keys.
    
    num_reactions = 0 if 'reactions' not in status else status['reactions']['summary']['total_count']
    num_comments = 0 if 'comments' not in status else status['comments']['summary']['total_count']
    num_shares = 0 if 'shares' not in status else status['shares']['count']
    
    # Counts of each reaction separately; good for sentiment
    # Only check for reactions if past date of implementation: http://newsroom.fb.com/news/2016/02/reactions-now-available-globally/
    
    reactions = get_reactions(status_id, access_token) if status_published > '2016-02-24 00:00:00' else {}
    
    num_likes = 0 if 'like' not in reactions else reactions['like']['summary']['total_count']
    
    # Special case: Set number of Likes to Number of reactions for pre-reaction statuses
    
    num_likes = num_reactions if status_published < '2016-02-24 00:00:00' else num_likes
    
    num_loves = 0 if 'love' not in reactions else reactions['love']['summary']['total_count']
    num_wows = 0 if 'wow' not in reactions else reactions['wow']['summary']['total_count']
    num_hahas = 0 if 'haha' not in reactions else reactions['haha']['summary']['total_count']
    num_sads = 0 if 'sad' not in reactions else reactions['sad']['summary']['total_count']
    num_angrys = 0 if 'angry' not in reactions else reactions['angry']['summary']['total_count']
    
    # Return a tuple of all processed data
    
    return (status_id, status_message, link_name, status_type, status_link,
           status_published, num_reactions, num_comments, num_shares,  num_likes,
           num_loves, num_wows, num_hahas, num_sads, num_angrys)

# Needed to write tricky unicode correctly to csv
def unicode_normalize(text):
    return text.translate({ 0x2018:0x27, 0x2019:0x27, 0x201C:0x22, 0x201D:0x22, 0xa0:0x20 }).encode('utf-8')


def convert_to_excel(page_id, access_token):
    with open('data/%s_facebook_statuses.csv' % page_id, 'wb') as file:
        w = csv.writer(file)
        w.writerow(["status_id", "status_message", "status_author", "link_name", "status_type", "status_link",
           "status_published", "num_reactions", "num_comments", "num_shares", "num_likes",
           "num_loves", "num_wows", "num_hahas", "num_sads", "num_angrys"])
        
        has_next_page = True
        num_processed = 0   # keep a count on how many we've processed
        scrape_starttime = datetime.datetime.now()
        
        print "Scanning %s Facebook Page: %s\n" % (page_id, scrape_starttime)
        
        statuses = retrieve_data(NUM_STATUS, page_id, access_token)
        
        while has_next_page:
            for status in statuses['data']:
            
                # Ensure it is a status with the expected metadata
                if 'reactions' in status:            
                    w.writerow(processFacebookPageFeedStatus(status, access_token))
                
                # output progress occasionally to make sure code is not stalling
                num_processed += 1
                if num_processed % 10 == 0:
                    print "%s Statuses Processed: %s" % (num_processed, datetime.datetime.now())
                    
            # if there is no next page, we're done.
            if 'paging' in statuses.keys():
                statuses = json.loads(request_until_succeed(statuses['paging']['next']))
            else:
                has_next_page = False
                
        
        print "\nDone!\n%s Statuses Processed in %s" % (num_processed, datetime.datetime.now() - scrape_starttime)



convert_to_excel(page_id, access_token)
for status in statuses['data']:
    comments = status['comments']
    for comment in comments['data']:
        print >> f2, json.dumps(comment, indent = 4, sort_keys = True)


