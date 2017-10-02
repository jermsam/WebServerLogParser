package com.ef;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**inhales log String exhales LogEntry object*/
public class WebLogParse {
  private static final SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss",Locale.US);
public static Date parseDate(String dateStr){
	ParsePosition pp=new ParsePosition(0);
	return dateFormat.parse(dateStr, pp);
}
public static String matchTo(StringBuilder sb, String delimiter){
	int x=sb.indexOf(delimiter);
	if(x==-1){ x=sb.length();}
	String ans=sb.substring(0,x);
	sb.delete(0, x+delimiter.length());
	return ans;
}
public static LogEntry parseEntry(String line){
	//log format:
	//127.0.0.1|-|waren|[2000-10-21.33:55:36 -0700]|"GET /apache_pb.gif HTTP/1.0"|200|2326
	//Assumes line is valid and in this format:
    StringBuilder sb = new StringBuilder(line);
    String ip = matchTo(sb, "|");
    matchTo(sb, "|"); //ignore -
//    System.out.println("IP: "+ip);
   matchTo(sb, "|["); //ignore -, and eat the leading [
    String dateStr = matchTo(sb, "]|\""); 
    Date date = parseDate(dateStr);
//    System.out.println("Access Date: "+dateStr);
   matchTo(sb, "\"|"); //eat both
    String statusStr = matchTo(sb, "|");
//    System.out.println("STATUS: "+statusStr);
    int status = Integer.parseInt(statusStr);
     matchTo(sb, " ");
    return new LogEntry(ip,date,status); //object whose toString format is  "ipAddress|accessTime|StatusCode"
}
}
