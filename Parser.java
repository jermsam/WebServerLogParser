package com.ef;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {
	public static void main(String[] args) {
		LogAnalyser analiser= new LogAnalyser();
		analiser.readFile("C:/log.txt");
		
		//;
//		 String[] array = {"--startDate=2000-10-21.21:55:36", "--duration=daily", "--threshold=1"};
//		 String duration=decodeArgs(array).get("duration");
//		 String startDate=decodeArgs(array).get("startDate");
//		 int threshold=Integer.parseInt(decodeArgs(array).get("threshold"));
//		 System.out.println(startDate+" : "+duration+" : "+threshold);
//		 
		// analiser.mapIpAddressAgainstComment("2000-10-21.21:55:36","daily",1);
		//String[] array = {"--startDate=2000-10-21.21:55:36", "--duration=daily", "--threshold=1"};
		if(args.length>0){ runApp(analiser,args );}else{
		System.out.println("Sorry: YOU DON'T RUN THE TOOL THAT WAY..");
		System.out.println("You've got to run a command similar to: ");
		System.out.println("java -cp \"parser.jar\" com.ef.Parser --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100");
		System.out.println("in your OS's console.");
		System.out.println("NB: check that you are running java 8 ++");
		System.out.println("Also Ensure that your MySQL server is running");
		System.out.println("on USER = root and PASSWORD = total");
		System.out.println("and has a database named: 'parsetest'");
		
		System.out.println("ALSO: DROP THE webserver log file in C:/'");
		System.out.println("AND: rename it to 'log.txt' without the quotes");
		System.out.println("so u can have C:/log.txt as complete path");
		
		System.out.println("Thanks");
		}
	}

	
	public static  HashMap<String,String> decodeArgs(String[] array){
		return (HashMap<String, String>) Stream.of(array)
		        .map(elem -> elem.split("\\="))
		        .filter(elem -> elem.length==2)
		        .collect(Collectors.toMap(e -> e[0], e -> e[1]))
		        .entrySet()
		        .stream()
		        .collect(
		        		Collectors.toMap(e -> e.getKey().split("\\-\\-")[1], e -> e.getValue()));
		
	}
	
	public static void runApp(LogAnalyser analiser,String[] array){
		
		 String duration=decodeArgs(array).get("duration");
		 String startDate=decodeArgs(array).get("startDate");
		 int threshold=Integer.parseInt(decodeArgs(array).get("threshold"));
		 System.out.println(startDate+" : "+duration+" : "+threshold);
		 
		 analiser.mapIpAddressAgainstComment("2000-10-21.21:55:36","daily",1);
	}
}
