package com.genie.job.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelFileToCsvFileConvertor {
	
	static private Pattern rxquote = Pattern.compile("\"");
	
	static private String encodeValue(String value) {
	    boolean needQuotes = false;
	    if ( value.indexOf(',') != -1 || value.indexOf('"') != -1 ||
	         value.indexOf('\n') != -1 || value.indexOf('\r') != -1 )
	        needQuotes = true;
	    Matcher m = rxquote.matcher(value);
	    if ( m.find() ) needQuotes = true; value = m.replaceAll("\"\"");
	    if ( needQuotes ) return "\"" + value + "\"";
	    else return value;
	}

	
	public static String convertXslxToCsvFile(String xlsxFile) throws Exception
	{
		String csvFile = xlsxFile.substring(0, xlsxFile.indexOf(".")) + ".csv";

		Workbook wb = new XSSFWorkbook(new FileInputStream(new File(xlsxFile)));

		DataFormatter formatter = new DataFormatter();
		PrintStream out = new PrintStream(new FileOutputStream(csvFile),
		                                  true, "UTF-8");
		byte[] bom = {(byte)0xEF, (byte)0xBB, (byte)0xBF};
		out.write(bom);
		
	    Sheet sheet = wb.getSheetAt(0);
	    for (int r = 0, rn = sheet.getLastRowNum() ; r <= rn ; r++) {
	        Row row = sheet.getRow(r);
	        if ( row == null ) { out.println(','); continue; }
	        boolean firstCell = true;
	        for (int c = 0, cn = row.getLastCellNum() ; c < cn ; c++) {
	            Cell cell = row.getCell(c, Row.RETURN_NULL_AND_BLANK);
	            if ( ! firstCell ) out.print(',');
	            if ( cell != null ) {
	                String value = formatter.formatCellValue(cell);
	                out.print(encodeValue(value));
	            }
	            firstCell = false;
	        }
	        out.println();
	    }
		return csvFile;	
	}
	
	public static String convertXlsToCsvFile(String xlsFileFullPath, String xlsFileName) throws Exception
	{
		System.out.println("Inside convertXlsToCsvFile method");
		System.out.println("XlsFileFullPath is " + xlsFileFullPath);
		
		String csvFile = xlsFileName.substring(0, xlsFileName.indexOf(".")) + ".csv";
		String csvFileFullPath = xlsFileFullPath.substring(0, xlsFileFullPath.lastIndexOf(".")) + ".csv";

		//Workbook wb = new XSSFWorkbook(new FileInputStream(new File(xlsxFile)));
		FileInputStream fis = new FileInputStream(new File(xlsFileFullPath));
		HSSFWorkbook myWorkBook = new HSSFWorkbook(fis);

		DataFormatter formatter = new DataFormatter();
		PrintStream out = new PrintStream(new FileOutputStream(csvFileFullPath),
		                                  true, "UTF-8");
		byte[] bom = {(byte)0xEF, (byte)0xBB, (byte)0xBF};
		out.write(bom);
		
	    HSSFSheet sheet = myWorkBook.getSheetAt(0); 
	    for (int r = 0, rn = sheet.getLastRowNum() ; r <= rn ; r++) {
	        Row row = sheet.getRow(r);
	        if ( row == null ) { out.println(','); continue; }
	        boolean firstCell = true;
	        for (int c = 0, cn = row.getLastCellNum() ; c < cn ; c++) {
	            Cell cell = row.getCell(c, Row.RETURN_NULL_AND_BLANK);
	            if ( ! firstCell ) out.print(',');
	            if ( cell != null ) {
	                String value = formatter.formatCellValue(cell);
	                out.print(encodeValue(value));
	            }
	            firstCell = false;
	        }
	        out.println();
	    }
	    
	    myWorkBook.close();
	    out.close();
	    fis.close();

	    System.out.println("CSV file path: " + csvFileFullPath);

	    System.out.println("CSV file length: " + new File(csvFileFullPath).length());

		return csvFile;	
	}

	
	
	public static void main(String args[])
	{
		try
		{
			System.out.println(ExcelFileToCsvFileConvertor.convertXlsToCsvFile("D:\\WebDriverDownloads\\StafferLink\\NewFiles\\StafferLinkJobs_1531400295950.xls", "StafferLinkJobs_1531400295950.xls"));
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
