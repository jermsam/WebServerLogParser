# WebServerLogParser
A parser in Java that parses web server access log file,  loads the log to MySQL and checks if a given IP makes more than a certain number of requests for the given duration.
The goal is to write a parser in Java that parses web server access log file,
 loads the log to MySQL and checks if a given IP makes more than a certain number of requests for the given duration.

(1) I have Create a java tool that can parse and load the given log file to MySQL:
	its executable .jar file is located in dist directory

  The delimiter of the log file is pipe (|)

	Below is the sample log.txt file read.
	The webserver log file is to be droped in the C:/ directory
	And Named log.txt.
	i.e: server log file path is: C:/log.txt  

	SAMPLE C:/log.txt
	
	127.0.0.1|-|frank|[2000-10-21.21:55:36 -0700]|"GET /apache_pb.gif HTTP/1.0"|200|2326
	127.0.0.2|-|frank|[2000-10-21.21:55:39 -0703]|"GET /apache_pb.gif HTTP/1.0"|400|2326
	127.0.0.3|-|frank|[2000-10-21.22:55:39 -0703]|"GET /apache_pb.gif HTTP/1.0"|400|2326
	127.0.0.1|-|frank|[2000-10-21.22:55:40 -0703]|"GET /apache_pb.gif HTTP/1.0"|200|2326
	127.0.0.1|-|waren|[2000-10-21.23:55:42 -0700]|"GET /apache_pb.gif HTTP/1.0"|401|2326
	127.0.0.2|-|frank|[2000-10-21.23:56:36 -0700]|"GET /apache_pb.gif HTTP/1.0"|407|2326
	127.0.0.3|-|frank|[2000-10-21.23:56:21 -0703]|"GET /apache_pb.gif HTTP/1.0"|417|2326
	127.0.0.1|-|waren|[2000-10-21.23:59:36 -0700]|"GET /apache_pb.gif HTTP/1.0"|200|2326
	127.0.0.1|-|frank|[2000-10-22.21:55:36 -0700]|"GET /apache_pb.gif HTTP/1.0"|400|2326
	127.0.0.2|-|frank|[2000-10-22.21:55:39 -0703]|"GET /apache_pb.gif HTTP/1.0"|450|2326
	127.0.0.3|-|frank|[2000-10-22.22:55:39 -0703]|"GET /apache_pb.gif HTTP/1.0"|200|2326
	127.0.0.1|-|waren|[2000-10-22.23:55:42 -0700]|"GET /apache_pb.gif HTTP/1.0"|200|2326
	127.0.0.2|-|frank|[2000-10-23.23:56:36 -0700]|"GET /apache_pb.gif HTTP/1.0"|414|2326
	127.0.0.3|-|frank|[2000-10-23.23:56:21 -0703]|"GET /apache_pb.gif HTTP/1.0"|410|2326
	127.0.0.1|-|waren|[2000-10-23.23:59:36 -0700]|"GET /apache_pb.gif HTTP/1.0"|400|2326



(2) The tool takes "startDate", "duration" and "threshold" as command line arguments. 
	
	"startDate" is of "yyyy-MM-dd.HH:mm:ss" format, 
	"duration" can take only "hourly", "daily" as inputs 
	and "threshold" can be an integer.

(3) This is how the tool works:

	java -cp "parser.jar" com.ef.Parser --startDate=2000-10-21.21:55:36 --duration=hourly --threshold=11

The tool will find any IPs that made more than 100 requests starting from 2017-01-01.13:00:00 to 2017-01-01.14:00:00 (one hour) 
and print them to console AND also load them to another MySQL table with comments on why it's blocked.

	GIVEN THAT EACH user_block COMMENT has a Unique code and also Each Ip Address has a unique ID,
	and Each Comment can apply to multiple Ips, 
	where Each Ip is able to have more than one Comment,

I MODIFIED THE REQUIREMENT TO THE FOLLOWING:

	separate tables for:
		ip ; namely ipaddress with fields (ipAddress_id,address) ipAddress_id being the primary key and
		comment; namely statuscomment with fields(statuscomment_code,comment) statuscomment_code being the primary key
	the two tables impliment are many to many relationship through HIBERNATE ORM

To Test The Tool:
I run the following command from the console:
java -cp "parser.jar" com.ef.Parser --startDate=2000-10-21.21:55:36 --duration=daily --threshold=1
On the sample C:/log.txt file above.

The tool was able to find any IPs that made more than 1 requests starting from 2000-10-21.21:55:36 to 2000-10-21.22:55:36 (24 hours) 
and printed them to console.

	SOMETHING LIKE:
	
	_________________________________________
	BLOCKED IP vs COMMENT AGGREGATE LOGS
	_________________________________________
	IP        :      COMMENT  
	________________________________________
	127.0.0.3 : 400 Bad Request
	127.0.0.3 : 417 Expectation Failed
	127.0.0.2 : 400 Bad Request
	127.0.0.2 : 408 Request Timeout
	127.0.0.1 : 401 Unauthorized
	127.0.0.1 : Not Blocked
	________________________________________

 AND also loaded them to the other MySQL tables mapping ips with comments on why it's blocked.
	
	SOMETHING LIKE:

	TABLE: ipaddress
	_________________________________________
	IP        :      COMMENT  
	________________________________________
	1 	  : 	127.0.0.1
	2 	  : 	127.0.0.2
	3 	  : 	127.0.0.3
	________________________________________

	TABLE: statuscomment
	_________________________________________
	IP        :      COMMENT  
	________________________________________
	200 	  : 	Not Blocked
	400   	  : 	400 Bad Request
	401 	  : 	401 Unauthorized
	407 	  : 	408 Request Timeout
	417 	  : 	417 Expectation Failed 
	________________________________________

	TABLE: ipaddresses_statuscomments
	_________________________________________
	statuscomment_code   :      ipAddress_id  
	________________________________________
		417 	     : 		1
		407   	     : 		2
		400 	     : 		2
		401 	     : 		3
		200 	     : 		3 
	________________________________________
Hence realising the objective even in a more rhobust way tan what way required.

FOR PERFORMANCE REASONS:
Used java 8's lambda expressins (functional programming) for efficiency and minimal code
Used Hibernate ORM for proper persisting of Java Object with relational SQL Objects and automatic database creation
I used log4j Logger to manage Hibernet logs
Also did some commenting... to give a clue about what's going on


SQL--- HERE I WAS REQUIRED TO:

My SOLUTION, 
was based on the required data structure of the single table database
I called the table parsetable


(1) Write MySQL query to find IPs that mode more than a certain number of requests for a given time period.
Ex: Write SQL to find IPs that made more than 100 requests starting from 2017-01-01.13:00:00 to 2017-01-01.14:00:00.

		
	SELECT ip, COUNT (ip) AS requests FROM parsetable
	WHERE startDate BETWEEN "2017-01-01 13:00:00" 
	AND "2017-01-01 14:00:00"
	GROUP BY (ip) 
	HAVING requests > 100;

(2) Write MySQL query to find requests made by a given IP. 

	SELECT ip, COUNT (ip) AS requests FROM parsetable
	WHERE startDate BETWEEN "2017-01-01 13:00:00" 
	AND "2017-01-01 14:00:00"
	GROUP BY (ip) 

NB: SNAPSHOTS ON MY TESTS ARE INCLUDED IN A DIRECTORY NAMED sqlSolution
