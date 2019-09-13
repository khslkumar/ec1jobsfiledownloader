package com.genie.job.portal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;  

public class CsvFileSplitter {


	public static List<String> splitCsvFile(String folderPath, String csvFileFullPath, String csvFileName)  
	{  
		ArrayList<String> fileNames = new ArrayList<String>();
		
		 try{  
		  // Reading file and getting no. of files to be generated  
		  double nol = 1000.0; //  No. of lines to be split and saved in each output file.  
		  File file = new File(csvFileFullPath);  
		  Scanner scanner = new Scanner(file);  
		  int count = 0;  
		  while (scanner.hasNextLine())   
		  {  
		   scanner.nextLine();  
		   count++;  
		  }  
		  System.out.println("Lines in the file: " + count);     // Displays no. of lines in the input file.  
	
		  double temp = (count/nol);  
		  int temp1=(int)temp;  
		  int nof=0;  
		  if(temp1==temp)  
		  {  
		   nof=temp1;  
		  }  
		  else  
		  {  
		   nof=temp1+1;  
		  }  
		  System.out.println("No. of files to be generated :"+nof); // Displays no. of files to be generated.  
	
		  //---------------------------------------------------------------------------------------------------------  
	
		  // Actual splitting of file into smaller files  
	
		  FileInputStream fstream = new FileInputStream(csvFileFullPath); 
		  DataInputStream in = new DataInputStream(fstream);  
	
		  BufferedReader br = new BufferedReader(new InputStreamReader(in)); 
		  String strLine;  
	
		  for (int j=1;j<=nof;j++)  
		  {  
			  String targetFileFullPath = folderPath + csvFileName + "_" + j + ".csv";
			  String targetFileName = csvFileName + "_" + j + ".csv";
			  
			  fileNames.add(targetFileName);
			  
			   FileWriter fstream1 = new FileWriter(targetFileFullPath);     // Destination File Location  
			   BufferedWriter out = new BufferedWriter(fstream1);
			   
			   for (int i=1;i<=nol;i++)  
			   {  
				    strLine = br.readLine();   
				    if (strLine!= null)  
				    {  
				    	out.write(strLine);   
					     if(i!=nol)  
					     {  
					    	 out.newLine();  
					     }  
			    }  
			   }  
			   out.close();  
		  }  
	
		  in.close();  
		 }
		 catch (Exception e)  
		 {  
			  System.err.println("Error: " + e.getMessage());  
			  return null;
		 }  
		 
		 return fileNames;
		 
	}
	
/*	public static void main(String args[])
	{
		String csvFileFullPath = "D:\\WebDriverDownloads\\StafferLink\\NewFiles\\StafferLinkJobs_1531387496467.csv";
		String csvFileName = "StafferLinkJobs_1531387496467.csv";
		String folderPath = "D:\\WebDriverDownloads\\StafferLink\\NewFiles\\";
		
		CsvFileSplitter.splitCsvFile(folderPath, csvFileFullPath, csvFileName);
	}*/

}
