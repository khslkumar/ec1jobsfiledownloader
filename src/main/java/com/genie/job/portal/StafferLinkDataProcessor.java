package com.genie.job.portal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.firefox.FirefoxBinary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("unused")
public class StafferLinkDataProcessor{
  private WebDriver driver;
  private String baseUrl;
  private String userName;
  private String password;
  private Long sourceSystemServerId;
  
  private static int FILE_DOWNLOAD_NOT_STARTED = 1;
  private static int FILE_DOWNLOAD_SUCCESSFUL = 2;
  private static int FILE_PROCESSING_INITIATED = 3;
  private static int FILE_PROCESSED = 4;
  
	private String renamePath = null;
	private String renameFileName = null;
  
  private int fileStatus;


  private static String fileDownloadBasePath = null;
  private BatchJobMetricsDTO batchMetricsDto;

  private static final String geckoDriverExeFullPath = "C:\\gecko\\geckodriver-v0.17.0-win64\\geckodriver.exe";
  
  private final Logger log = LoggerFactory.getLogger(StafferLinkDataProcessor.class);
  private List<Long> fileSourceJobIDs;
  private RestTemplate restTemplate;
  private String myFile;
  private final MailService mailService;
	private String jobsAdminEmailId;
	private List<FileUploadResponseDTO> uploadResponsesDtoList = new ArrayList<FileUploadResponseDTO>();
	private ZonedDateTime fileCreateTriggerTimestamp = null;
	private ZonedDateTime fileProcessTriggerTimestamp = null;
  
  private List<String>  fileNames = new ArrayList<String>();
private String chromeDriverForSelenium;
 
  public StafferLinkDataProcessor(MailService mailService, BatchJobMetricsDTO batchMetricsDto)
  {
	  fileDownloadBasePath = GenieProperties.getInstance().getValue("fileDownloadBasePath");
	  chromeDriverForSelenium = GenieProperties.getInstance().getValue("chromeDriverForSelenium");
	  this.mailService = mailService;
	  this.batchMetricsDto = batchMetricsDto;

  }
  
  public void setUp() throws Exception {
	  
	  System.setProperty("webdriver.chrome.driver", chromeDriverForSelenium); 

      ChromeOptions options = new ChromeOptions();
      options.addArguments("--test-type");
      options.addArguments("--headless");
      options.addArguments("--disable-extensions"); //to disable browser extension popup
      options.addArguments("--no-sandbox");

      
      HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
      chromePrefs.put("profile.default_content_settings.popups", 0);
      chromePrefs.put("download.default_directory", fileDownloadBasePath);
      options.setExperimentalOption("prefs", chromePrefs);	  	
      
      options.addArguments("--disable-dev-shm-usage");

	  driver = new ChromeDriver(options);
	  
    SourceSystemDTO sSys = RestApiUtilities.getSourceSystemDetails("StafferLink");
    jobsAdminEmailId = GenieProperties.getInstance().getValue("jobadminemail");
    
    baseUrl = sSys.getWebsiteURL();
    log.info("Base URL : " + baseUrl);
    userName = sSys.getUserName();
    password = sSys.getPassword();
    sourceSystemServerId = sSys.getId();
	  fileStatus = FILE_DOWNLOAD_NOT_STARTED;
    
    driver.manage().timeouts().implicitlyWait(3600, TimeUnit.SECONDS);
    createFolderIfNotExists(fileDownloadBasePath);
  }

	private void createFolderIfNotExists(String folderPath)
	{
	    File dir = new File(folderPath);
	    if (!dir.exists()) dir.mkdirs();
	}

  public void downloadFile()  {

	fileStatus = FILE_DOWNLOAD_NOT_STARTED;
	  
	uploadResponsesDtoList.clear();
	batchMetricsDto.setJobsFileCreationStatus("Creating");
	batchMetricsDto.setJobsFileCreateTriggerDateTime(ZonedDateTime.now().toString().toString());
	
	batchMetricsDto.setSourceSystemName("StafferLink");
	batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

	batchMetricsDto = RestApiUtilities.insertRecordIntoBatchJobMetrics(batchMetricsDto);
	  
	  PrintWriter printWriter = null;

	  
	  try{
		  setUp();
		  batchMetricsDto.setSourceSystemId(sourceSystemServerId);

	driver.get(baseUrl + "login.aspx");
    Thread.sleep(5000);
    driver.findElement(By.id("txtUsername")).clear();
    driver.findElement(By.id("txtUsername")).sendKeys(userName);
    
    driver.findElement(By.id("txtPassword")).clear();
    driver.findElement(By.id("txtPassword")).sendKeys(password);

    driver.findElement(By.xpath("//*[@id='cmdLogin']")).click();
    Thread.sleep(12000);


    WebDriverWait wait = new WebDriverWait(driver, 120000);
    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='mnuView']/a")));// instead of id u can use cssSelector or xpath of ur element.

    driver.findElement(By.xpath("//*[@id='mnuView']/a")).click();
    
    driver.findElement(By.xpath("//*[@id='Group5_Item2']/td/div[2]")).click();
    Thread.sleep(2000);
    
    driver.findElement(By.xpath("//*[@id='pcsPlaceholder']/div[1]/table/tbody/tr[2]/td/div[2]")).click();
    
    Thread.sleep(3000);

	int iterationCount = 0;


	
    while (true)
    {
    	if (iterationCount == 0)
    	{
    		log.info("Iteration Count is Zero");
    		fileStatus = FILE_DOWNLOAD_NOT_STARTED;
	        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	
	    	renamePath = fileDownloadBasePath + "StafferLinkJobs" + "_" + timestamp.getTime() + ".csv";
	    	renameFileName = "StafferLinkJobs" + "_" + timestamp.getTime() + ".csv";
	    	batchMetricsDto.setJobsFileCreatedName(renameFileName);
	    	
	    	log.info("Iteration Count Zero: File name is : " + renameFileName);
	        FileWriter fileWriter = new FileWriter(renamePath); 
	        printWriter = new PrintWriter(fileWriter);
    	}
    	
    	
        WebElement table = driver.findElement(By.xpath("//*[@id='ctl00_Main_GridMain']"));
        writeTableRecordsToFile(driver, printWriter);
        
    	try
        {
 
        	WebElement nextButton = driver.findElement(By.id("navNP"));
        	if (nextButton == null || nextButton.isEnabled() == false) {     		
        		printWriter.flush();
        		printWriter.close();
				batchMetricsDto.setJobsFileCreationStatus("Uploading");
				batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
        		uploadFile(fileDownloadBasePath, renamePath, renameFileName, sourceSystemServerId);

        		fileStatus = FILE_DOWNLOAD_SUCCESSFUL; break;
        	} else {
        		wait.until(ExpectedConditions.elementToBeClickable(nextButton));
        		nextButton.click(); 
        		Thread.sleep(2000); 
        		iterationCount++;
        		continue; 
        	}
        }
        catch (Exception ex)
        {
        	ex.printStackTrace();
        	break;
        }
    	
    }
    

    Thread.sleep(5000);

    log.info("Returned out of drivers navigate method: ");
    driver.close();

    try {
    	driver.quit();
    } catch (Exception ex) {
		  log.warn("Error in quitting the selenium driver after Setup. ", ex);
    }
	
	Thread.sleep(5000);
	
	  } catch (Exception ex) {
		    if (printWriter != null) printWriter.close();

		  log.error(ex.getMessage(), ex);
        
  		StringWriter sw = new StringWriter();
  		ex.printStackTrace(new PrintWriter(sw));
  		
  		String to = jobsAdminEmailId;
  		String subject = "Batch Jobs Application: StafferLink File Download Failed.";
  		String content = "Batch Jobs Application: StafferLink File Download Failed. The Error Details are: " + sw.toString();
  		
		batchMetricsDto.setJobsFileCreationStatus("Download Failed : " + sw.toString().substring(0, 200));
		batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString().toString());

		batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

		batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
  		
  		mailService.sendEmail(to, subject, content, false, false);
  	
	  }
	  
  	processAllUploadedFiles();

  }




  private void writeTableRecordsToFile(WebDriver driver, PrintWriter writer) {
	  List<WebElement> rows = driver.findElements(By.xpath("//*[@id='ctl00_Main_GridMain']/tbody/tr"));
	  int Row_count = rows.size();
	  log.info("Number Of Rows = "+Row_count);
	  
	  List<WebElement> allCells = driver.findElements(By.xpath("//*[@id='ctl00_Main_GridMain']/tbody/tr/td"));
	  
	  log.info("All Cells size : " + allCells.size());

	  int colsPerRecord = allCells.size() / Row_count;
	  
	  //int Col_count = cols.size();
	  	  
	  for (int rowIdx = 0; rowIdx < Row_count; rowIdx++)
	  {
		  JobDTO jobDto = new JobDTO();
		  
		  log.info("Row index: " + rowIdx);
			for (int colIdx1 = 0; colIdx1 < colsPerRecord; colIdx1++) {
				int colIdx = (rowIdx * colsPerRecord) + colIdx1;
				log.info("Value in all Cells List: " + allCells.get(colIdx).getText());
				log.info("Column Idx : " + colIdx1);
				log.info("cell idx: " + colIdx);
				if (colIdx % colsPerRecord == 2) {
					jobDto.setSourceJobID(allCells.get(colIdx).getText());
				} else if (colIdx % colsPerRecord == 5) {
					jobDto.setStartDate(allCells.get(colIdx).getText());
				} else if (colIdx % colsPerRecord == 6) {
					jobDto.setShift(allCells.get(colIdx).getText());
				} else if (colIdx % colsPerRecord == 7) {
					jobDto.setProfession(allCells.get(colIdx).getText());
				} else if (colIdx % colsPerRecord == 9) {
					jobDto.setCity(allCells.get(colIdx).getText());
				} else if (colIdx % colsPerRecord == 10) {
					jobDto.setStateName(allCells.get(colIdx).getText());
				} else if (colIdx % colsPerRecord == 12) {
					// log.info("Source Job ID: " + allCells.get(colIdx).getText());
					jobDto.setFacility(allCells.get(colIdx).getText());
				}
			}
		  writer.println(jobDto.toStringForFile());
	  }
	
	  writer.flush();
  }
  
private void selectFieldValueFromTableColumnValue(JobDTO jobDto, String table_data, int j) {
	// TODO Auto-generated method stub
	//Sts	Approval	ID	AgcID	Perm	Date	Time	Cls	Area	City	State	Zip	Facility	Registrant	Patient Name	Note	Subs	Rej	Pro	Action
	  //td[9] is City, td[10] is State, td[7] is Profession, Specialty is blank, Shift is blank, td[5] is startDate (last 8 chars), Modified date is Blank, 
	  //Job Description---- Blank,	  Rate           ---- Blank, td[12] is Facility, td[2] is JobID, Status is Blank
	switch (j)
	{
		case	1: break;
		case 	2: break;
		case	3: jobDto.setSourceJobID(table_data);break;
		case 	4: break;
		case 	5: break;
		case 	6: String startDate = null;
					startDate = (table_data.length() > 8) ? table_data.substring(table_data.length()-8) : table_data;
					String LeftPaddedString = StringUtils.leftPad(startDate.trim(),8,'0');

					jobDto.setStartDate(LeftPaddedString); break;
		case	7: break;
		case 	8: jobDto.setProfession(table_data);break;
		case 	9:	break;
		case 	10: jobDto.setCity(table_data);break;
		case 	11: jobDto.setStateName(table_data); break;
		case 	12: break;
		case 	13: jobDto.setFacility(table_data); break;
		default: break;
	}
	
}

private void uploadFile(String fileDir, String renamePath, String renameFileName, Long sourceSystemServerId) throws Exception{
	// TODO Auto-generated method stub
	  
	FileUploadResponseDTO uploadResponse = RestApiUtilities.uploadFile(fileDir + renameFileName, renameFileName, sourceSystemServerId);
	
	
	if (uploadResponse != null)
	{
		if (uploadResponse.isError())
		{
			log.error("StafferLink -- Downloading File has Failed. Error Details : " + uploadResponse.getErrorMessage());
	  		
	  		String to = jobsAdminEmailId;
	  		String subject = "Batch Jobs Application: StafferLink File Download Failed.";
	  		String content = "Batch Jobs Application: StafferLink File Download Failed. The Error Details are: " + uploadResponse.getErrorMessage();
	  			  					
			batchMetricsDto.setJobsFileCreationStatus(
					"Upload Failed: " + uploadResponse.getErrorMessage().substring(0, 200));
			batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString().toString());

			batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

			batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
	  		
	  		mailService.sendEmail(to, subject, content, false, false);


		} else {
			log.info("StafferLink -- Downloading File has Succeeded.");
			  fileStatus = FILE_DOWNLOAD_SUCCESSFUL;
			log.info(uploadResponse.toString());
			uploadResponsesDtoList.add(uploadResponse);
			
	  		String to = jobsAdminEmailId;
	  		String subject = "Batch Jobs Application: StafferLink File Download has been Successful.";
	  		String content = "Batch Jobs Application: StafferLink File Download has been Successful.";
	  		
	  		batchMetricsDto.setJobsFileCreationStatus("Success");
			batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString());

			batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

			batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);

	  		mailService.sendEmail(to, subject, content, false, false);

		}
	} else {
		log.info("StafferLink -- Downloading File has Failed as REST API request failed. The REST API reply is NULL.");
		
  		String to = jobsAdminEmailId;
  		String subject = "Batch Jobs Application: StafferLink File Download Failed.";
  		String content = "Batch Jobs Application: StafferLink File Download Failed. The Error Details are: StafferLink -- Downloading File has Failed as REST API request failed. The REST API reply is NULL.";
  			  					
  		batchMetricsDto.setJobsFileCreationStatus("Upload Failed: " + content);
		batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString());

		batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

		batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
  		
  		mailService.sendEmail(to, subject, content, false, false);

	}
}

private List<String> splitFile(String folderPath, String renamePath, String csvFileName) throws Exception{
	// TODO Auto-generated method stub
	
	String csvFileFullPath = null;
	
	if (renamePath.endsWith(".xls")) 
		csvFileFullPath = ExcelFileToCsvFileConvertor.convertXlsToCsvFile(renamePath, csvFileName);
	
	return CsvFileSplitter.splitCsvFile(folderPath, csvFileFullPath, csvFileName);
	
}


  	
	public void processDownloadedFile(FileUploadResponseDTO uploadResponse) {
		
		if (fileStatus != FILE_DOWNLOAD_SUCCESSFUL) {
			log.info("File Upload was not successful. So skipping processing of the file.");
			return;
		}
	  //String myFile = "";
		JobsUploadDTO jobsUploadDto = new JobsUploadDTO();

		
	  try
	  {
		log.info("Started processing downloaded excel spreadsheet");
		jobsUploadDto.setDateFormat("MM/dd/yyyy");
		
		Long[] dbColsArray = {new Long(1),new Long(2),new Long(3),new Long(4),new Long(5),new Long(6), new Long(7), new Long(8), new Long(9), new Long(10), new Long(11)};
		List<Long> dbColumnsList = Arrays.asList(dbColsArray);
		
		//jobsUploadDto.setDbColumnNames(dbColumnNames);
		jobsUploadDto.setDbColumnsList(dbColumnsList);
		
		Long[] excelMappedColsArray = {new Long(1),new Long(2),new Long(3),new Long(4),new Long(5),new Long(6), new Long(7), new Long(8), new Long(9), new Long(10), new Long(11)};;
		List<Long> excelMappedHeadersList = Arrays.asList(excelMappedColsArray);
		
		String fileFullPathOnServer = uploadResponse.getFileFullPathOnServer();
		
		jobsUploadDto.setExcelMappedHeadersList(excelMappedHeadersList);
		jobsUploadDto.setFileFullPathOnServer(fileFullPathOnServer);
		//jobsUploadDto.setFileName(fileFullPathOnServer.substring(fileFullPathOnServer.lastIndexOf("//") + 1).replaceAll("..", "."));
		jobsUploadDto.setFileName(renameFileName);

		jobsUploadDto.setJobSource(sourceSystemServerId);
		jobsUploadDto.setRemoveClosedJobs("Yes");
		
		ListenableFuture<ResponseEntity<String>> futureEntity = RestApiUtilities.processUploadedFile(jobsUploadDto);
		
		futureEntity.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
			@Override
			public void onSuccess(ResponseEntity<String> respDto)
			{
        		File localFile = new File(renamePath);
        		localFile.delete();
				if (respDto != null)
				{
					if (!respDto.getBody().toLowerCase().equals("ok"))
					{
						  log.error("Error while processing downloaded StafferLink file named: " + myFile + "; Exception Details: " + respDto.getBody());
						  
					  		String to = jobsAdminEmailId;
					  		String subject = "Batch Jobs Application: StafferLink Processing Contents of Uploaded File Failed.";
					  		String content = "Batch Jobs Application: StafferLink Processing Contents of Uploaded File Failed. The Error Details are: StafferLink -- REST API request failed. The REST API reply is NULL.";
					  			  					
					  		mailService.sendEmail(to, subject, content, false, false);
					}
					else
					{
						log.info("Processing of jobs in the uploaded file has been Successful.");
						
						fileStatus = FILE_PROCESSING_INITIATED;
						
				  		String to = jobsAdminEmailId;
				  		String subject = "Batch Jobs Application: StafferLink Processing Contents of Uploaded File has been Successful.";
				  		String content = "Batch Jobs Application: StafferLink Processing Contents of Uploaded File has been Successful.";

						mailService.sendEmail(to, subject, content, false, false);

				  	}
				}
				else
				{
					  log.error("Error while processing downloaded StafferLink file named: " + myFile + "; Exception Details: REST API request failed as response is NULL");
					  
				  		String to = jobsAdminEmailId;
				  		String subject = "Batch Jobs Application: StafferLink Processing Contents of Uploaded File Failed.";
				  		String content = "Batch Jobs Application: StafferLink Processing Contents of Uploaded File Failed. The Error Details are: StafferLink -- REST API request failed. The REST API reply is NULL.";
				  			  					
				  		mailService.sendEmail(to, subject, content, false, false);
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
        		File localFile = new File(renamePath);
        		localFile.delete();
				// TODO Auto-generated method stub
				log.error("Error in Listenable Future for File Processing Status.", arg0);
			}
		});
	  }
	  catch (Exception ex)
	  {
		  log.error("Error while processing downloaded StafferLink file named: " + myFile + "; Exception Details: ", ex);
		  //ex.printStackTrace();
		  
	  		StringWriter sw = new StringWriter();
	  		ex.printStackTrace(new PrintWriter(sw));
		  
	  		String to = jobsAdminEmailId;
	  		String subject = "Batch Jobs Application: StafferLink Processing Contents of Uploaded File Failed.";
	  		String content = "Batch Jobs Application: StafferLink Processing Contents of Uploaded File Failed. The Error Details are: " + sw.toString();
	  			  					
	  		mailService.sendEmail(to, subject, content, false, false);
	  }
	}
	
	public void processAllUploadedFiles()
	{
		for (FileUploadResponseDTO uploadFileResponseDto : uploadResponsesDtoList)
			processDownloadedFile(uploadFileResponseDto);
	}
	
	public static void main(String args[])
	{
		try
		{
			MailService mailService = new MailService();
			StafferLinkDataProcessor StafferLink = new StafferLinkDataProcessor(mailService, new BatchJobMetricsDTO());
			StafferLink.downloadFile();
			//Thread.sleep(5000);
			StafferLink.processAllUploadedFiles();
			

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
