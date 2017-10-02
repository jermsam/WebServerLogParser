package com.ef;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.ef.data.HibernateUtil;
import com.ef.data.IpAddress;
import com.ef.data.StatusComment;

public class LogAnalyser {
private ArrayList<LogEntry> records;
//initialize to an empty array list at object creation
public LogAnalyser() {
	super();
	this.records = new ArrayList<>();
}

public void readFile(String filename){
	/**Create File resource  for fileName 
	 * iterate over its lines;
	 * use WebLogParse for each to convert it to LogEntry objects
	 * add resulting LogEntry to records list
	 * try-with-resources to tame:
	 * java.nio.file.FileSystemException: /proc: Too many open files
	*/
	try (Stream<String> lines = Files.lines(Paths.get(filename))) {//Stream created in an auto closable
		lines.forEachOrdered(line->{records.add(WebLogParse.parseEntry(line));});
		} catch (IOException e) {
			e.printStackTrace();
		}
}

public void printAll(String startDate,String duration){
	getRecordsBtnDuration(startDate,duration).forEachOrdered(System.out::println);//iterate over each and print to console
}

public HashMap<String,List<LogEntry>> groupRecordsByIpAddress(){
	return (HashMap<String, List<LogEntry>>) records.stream()
			.collect(Collectors.groupingBy(LogEntry::getIpAddress));
}

public HashMap<String,List<LogEntry>> groupRecordsByIpAddress(String startDate,String duration){
	return (HashMap<String, List<LogEntry>>) getRecordsBtnDuration(startDate,duration)
			.collect(Collectors.groupingBy(LogEntry::getIpAddress));
}

public HashMap<String,Long> countRecordsByIpAddress(String startDate,String duration){
	return (HashMap<String, Long>) getRecordsBtnDuration(startDate,duration)
			.collect(Collectors.groupingBy(LogEntry::getIpAddress,Collectors.counting()));
}

public HashMap<String,List<LogEntry>> groupRecordsByIpAddress(String startDate,String duration, int threshhold){
	return (HashMap<String, List<LogEntry>>) groupRecordsByIpAddress(startDate,duration)
			.entrySet()
            .stream()
            .filter(a->a.getValue().size()>threshhold)
            .collect(Collectors.toMap(e->e.getKey(),e->e.getValue()));
}

private LocalDateTime convertDateToLocalTime(Date date){
	return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
}

private LocalDateTime convertDateToLocalTime(String date){
	return LocalDateTime.of(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd.HH:mm:ss")),
					LocalDateTime.now().toLocalTime()
					);	
}

private LocalDateTime computeEndDate(String startDate,String duration){
	 if(duration=="hourly")
		 return convertDateToLocalTime(startDate).plusHours(1);
	 else if(duration=="daily")
		return convertDateToLocalTime(startDate).plusDays(1);
	 return null;
}

 public Stream<LogEntry> getRecordsBtnDuration(String startDate,String duration){
	 return records.stream().filter(
					r->((convertDateToLocalTime(r.getAccessTime()).isAfter(convertDateToLocalTime(startDate))
					&&convertDateToLocalTime(r.getAccessTime()).isBefore(computeEndDate(startDate,duration))
					))
					);
 }
 
 private String returnComment(int serverBlockCode){
	 String comment="";
	
		 switch(serverBlockCode){
		 case 400: comment= "400 Bad Request";
		 break;
		 case 401: comment= "401 Unauthorized";
		 break;
		 case 402: comment= "402 Payment Required";
		 break;
		 case 403: comment= "403 Forbidden";
		 break;
		 case 404: comment= "404 Not Found";
		 break; 
		 case 405: comment= "405 Method Not Allowed";
		 break;
		 case 406: comment= "406 Not Acceptable";
		 break;
		 case 407: comment= "407 Proxy Authentication Required";
		 case 408: comment= "408 Request Timeout";
		 break;
		 case 409: comment= "409 Conflict";
		 break;
		 case 410: comment= "410 Gone";
		 break;
		 case 411: comment= "411 Length Required";
		 break;
		 case 412: comment= "412 Precondition Failed";
		 break; 
		 case 413: comment= "413 Request Entity Too Large";
		 break;
		 case 414: comment= "414 Request-URI Too Long";
		 break;
		 case 415: comment= "415 Unsupported Media Type";
		 break;
		 case 416: comment= "416 Requested Range Not Satisfiable";
		 break;
		 case 417: comment= "417 Expectation Failed";
		 break;
		 case 418: comment= "418 I'm a teapot (RFC 2324)";
		 break; 
		 case 420: comment= "420 Enhance Your Calm (Twitter)";
		 break;
		 case 422: comment= "422 Unprocessable Entity (WebDAV)";
		 break;
		 case 423: comment= "423 Locked (WebDAV)";
		 break; 
		 case 424: comment= "424 Failed Dependency (WebDAV)";
		 break;
		 case 425: comment= "425 Reserved for WebDAV";
		 break;
		 case 426: comment= "426 Upgrade Required";
		 break;
		 case 428: comment= "428 Precondition Required";
		 break;
		 case 429: comment= "429 Too Many Requests";
		 break; 
		 case 431: comment= "431 Request Header Fields Too Large";
		 break;
		 case 444: comment= "444 No Response (Nginx)";
		 break;
		 case 449: comment= "449 Retry With (Microsoft)";
		 break;
		 case 450: comment= "450 Blocked by Windows Parental Controls (Microsoft)";
		 break; 
		 case 451: comment= "451 Unavailable For Legal Reasons";
		 break;
		 case 499: comment= "499 Client Closed Request (Nginx)";
		 default: comment= "Not Blocked";
		 }
	
	return comment;
 }

 public void mapIpAddressAgainstComment(String startDate,String duration, int threshhold){
	 List<StatusComment> comments=new ArrayList<>();
	groupRecordsByIpAddress(startDate,duration,threshhold)
				.entrySet()
	            .stream().forEach(e->{
	            			Session session = HibernateUtil.getSessionFactory().openSession();
	            			Transaction transaction = null;
	            			try {
	            				transaction = session.beginTransaction();
	            				IpAddress address = new IpAddress(e.getKey());
	            				
	            				ArrayList<IpAddress> addresses = new ArrayList<IpAddress>();
	            				addresses.add(address);
	            				e.getValue().stream().distinct()
	            				.collect(Collectors.groupingBy(LogEntry::getStatusCode))
	            				.entrySet().stream().forEach(leSet->{
	            					
           					StatusComment comment = new StatusComment(leSet.getKey(),returnComment(leSet.getKey()),addresses);
           					comments.add(comment);
           					session.saveOrUpdate(comment);
            				});
	            				
	            				transaction.commit();
	            			} catch (Exception ex) {
	            				transaction.rollback();
	            				ex.printStackTrace();
	            			} finally {
	            				session.close();
	            			}
	            			
	            		});
	System.out.println("_______________________________________");
	System.out.println("BLOCKED IP vs COMMENT AGGREGATE LOGS ");
	System.out.println("________________________________________");
	System.out.println("  IP      :      COMMENT  ");
	System.out.println("________________________________________");
	comments.stream().forEachOrdered(c->System.out.println(
			c.getIpAddresses().get(0).getAddress()+" : "+c.getComment()
						));
	System.out.println("________________________________________");
			
	}
 
}
