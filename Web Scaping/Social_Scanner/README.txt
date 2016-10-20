Social Scanner by Lily Lin v1.0

Current version:

FACEBOOK_SCANNER:
	
	Abstract:
	facebook_scanner can scan a public page and gather info of public posts, such as likes, comments, reactions, etc. Text of status and comments can be used for sentiment analysis, but is not included in this version. 

	Instructions:

	(1) Make sure you have python2.7 installed on your machine.
	(2) Open Terminal in Mac(Or bash in Windows), from your home directory, "cd" into Social_Scanner_Lily
			E.G. If Social_Scanner_Lily is under Downloads, I would enter in commandline "cd Downloads/Social_Scanner_Lily"
	(3) Enter "cd facebook_scanner" to enter the facebook folder
	(4) Enter "python getFBpage.py"
	(5) The program will prompt user to enter page ID. Valid page ids are such as "CNN", "NBColympics", "bbc". Note: it is not case-sensitive. Please only enter one at a time
	(6) The scanner will start running. the output will be in the "data" folder. It will contain a copy in 	JSON format and one in CSV which can be opened in excel

	FAQ:

	(1) Why is there no output?

	Please check your internet settings. If you're in mainland China please use VPN, you may need to set bash_profile.

	(2) How does authentication work?

	Right now, the default user is a dummy, whose app id and secret is entered for you. If you want to use your own id and secret for more access rights, just replace "APP_ID" and "APP_SECRET". This version works with Facebook API v2.1.


TWITTER_SCANNER
twitter_scanner is under development and cannot be used yet.

For questions or comments please contact me at cnlilylin@berkeley.edu