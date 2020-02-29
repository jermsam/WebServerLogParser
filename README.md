# WebServerLogParser
Code base for:
[WebServerLogAnalizer tool](https://github.com/jermsam/WebServerLogAnalizer)

## File descriptions
### Illustrations
1. [sqlSolutions.JPG:](https://github.com/jermsam/WebServerLogParser/blob/master/sqlSolutions.JPG) An image file that highlights the most important parameters that make up the parse table (for the [Additional SQL Exercise](https://github.com/jermsam/WebServerLogAnalizer#additional-sql-exercise))

2. [solution1.jpg:](https://github.com/jermsam/WebServerLogParser/blob/master/solution1.JPG)and [solution2.jpg](https://github.com/jermsam/WebServerLogParser/blob/master/solution2.JPG) are the corresponding results for the questions of the exercise.

### log file (sample data)
3. [log.txt:](https://github.com/jermsam/WebServerLogParser/blob/master/log.txt)a sample log file

### JPA (Persisting the Java Objects To MySQL database tables)
4. [hibernate.cfg.xml:](https://github.com/jermsam/WebServerLogParser/blob/master/hibernate.cfg.xml)configuration file for the Hibernate Object Relational Mapper that allows for auto generation of our database tables using the Java Persistance API.
This file is where we tell Hibernates how to connect to the database and which Java classes should be mapped to database tables.

5. [HibernateUtil.java:](https://github.com/jermsam/WebServerLogParser/blob/master/HibernateUtil.java)The file that defines the Hibernate Session Factory. In Hibernate, you perform database operations via a Session which can be obtained from a SessionFactory. The SessionFactory loads Hibernate configuration file, analyzes the mapping and creates connection to the database. 
As you can tell; this tool was developed two year ago before. That was before Hibernate v4. If you happen to be reproduce the tool, you will have to redefine your Session Factory as demonstrated in the code below:

```
package com.ef.data;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
public class HibernateUtil {

	private static SessionFactory sessionFactory; 
  
	private static SessionFactory buildSessionFactory() {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			if (sessionFactory == null) {
            // innitiate configuration
            Configuration configuration = new Configuration().configure();
            // Innitiate service registry builder
            ServiceRegistryBuilder registry = new ServiceRegistryBuilder();
            // apply the configuration settings
            registry.applySettings(configuration.getProperties());
            //build service registry
            ServiceRegistry serviceRegistry = registry.buildServiceRegistry();
             // build session factory from service registry
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);           
        }
         
        return sessionFactory;
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}
}
```

As you can see  the new recommended code snippet builds the SessionFactory based on a ServiceRegistry and obtains the Session.

6. [IpAddress.java:](https://github.com/jermsam/WebServerLogParser/blob/master/IpAddress.java)The Modal class that defines an IpAddress JPA Model object and how it is related to status comments through the `ipAddresses_statusComments` table. That is An Ip Address can have many Status Comments and A Status Comment can belong to Many Ip Addresses `(@ManyToMany)`

7. [StatusComment.java:](https://github.com/jermsam/WebServerLogParser/blob/master/StatusComment.java)The Modal class that defines an StatusComment JPA Model object and how it is related to ip addresses through the `ipAddresses_statusComments` table. That is A Status Comment can belong to many IP Addresses and An Ip Address can have Many Status Comments `(@ManyToMany)`

### Parsing the data

8. [LogEntry.java: ] (https://github.com/jermsam/WebServerLogParser/blob/master/LogEntry.java)A blue print for each log

9. [WebLogParse.java:](https://github.com/jermsam/WebServerLogParser/blob/master/WebLogParse.java)Defines a class of Objects that would take in a String of Log information of the form `127.0.0.1|-|waren|[2000-10-21.33:55:36 -0700]|"GET /apache_pb.gif HTTP/1.0"|200|2326` and convert it into [LogEntry Object](https://docs.oracle.com/cd/E29587_01/PlatformServices.60x/ps_log_gen/src/clsrg_about_the_LogEntry_class.html)consisting of `ip`, `date` and `status` key:value pairs. Note that we defined our custom LogEntry in 8 above

### The Parser App (Actual Business Logic)

10. [LogAnalyser.java:](https://github.com/jermsam/WebServerLogParser/blob/master/LogAnalyser.java)This does the actual analysis of the data. It defines methods that: 
(i) readFile: read the log File. Iterate over the lines in the log file and Uses WebLogParse to convert each to LogEntry objects adding each of the resulting LogEntries to records list
(ii)getRecordsBtnDuration: Filters Log records that fall within a specified duration after a specified start date
(iii)mapIpAddressAgainstComment: This Maps the IP addresses against their respective comments in the logs returned within a specified duration after a specified start date and prints it to the console in form of the tables you saw in the results. ie: `BLOCKED IP vs COMMENT AGGREGATE LOGS`

11. [Parser.java:](https://github.com/jermsam/WebServerLogParser/blob/master/Parser.java). The main file. The entry point of the application. It will give you directions incase you do not use the tool as per recommendations or run the applications if you follow the right instructions.

12. [README.md:](https://github.com/jermsam/WebServerLogParser/blob/master/README.md) The read me file. This is where you are. Giving you a summary of what the implementation of this tool is like.
