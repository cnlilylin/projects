import urllib.parse
import oauth2
import hashlib

CONSUMER_KEY="J6dCgsnIGIeOwlutLSwvTpxi0"
CONSUMER_SECRET="UJRHMeRgBHu2EWqv4vDq5PTU5iyYCEfEuFrWNKeThflBMhQ5Z4"
TOKEN_KEY="225399518-Pnec6ZGwCZQ7Wu9OXE8LbPiH4ZdBrwJuUUH41wf3"
TOKEN_SECRET="5aHPgU7NcJrsXP6fb2vPbUQhcZE9AJkzmYzszjG6T7Ul5"

def oauth_twitter(url,key,secret,http_method="GET",post_body="",http_headers=None):
	consumer = oauth2.Consumer(key=CONSUMER_KEY,secret=CONSUMER_SECRET)
	token=oauth2.Token(key=key,secret=secret)
	client=oauth2.Client(consumer, token)
	resp, content= client.request(encode_string(url), http_method, encode_string(post_body))
	return content



def encode_string(str):
	return str.encode('utf-8')

def search_twitter():
	url="https://api.twitter.com/1.1/search/tweets.json?src=typd&q=olympics"
	content=oauth_twitter(url,TOKEN_KEY,TOKEN_SECRET)
	print(content)

search_twitter()