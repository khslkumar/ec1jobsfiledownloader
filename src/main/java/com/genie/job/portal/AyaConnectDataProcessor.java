package com.genie.job.portal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.firefox.FirefoxBinary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;

@SuppressWarnings("unused")
public class AyaConnectDataProcessor{
  private WebDriver driver;
  private String baseUrl;
  private String userName;
  private String password;
  private Long sourceSystemServerId;
  
  private static int FILE_DOWNLOAD_NOT_STARTED = 1;
  private static int FILE_DOWNLOAD_SUCCESSFUL = 2;
  private static int FILE_PROCESSING_INITIATED = 3;
  private static int FILE_PROCESSED = 4;
  
  private int fileStatus;
  private String chromeDriverForSelenium;
  private BatchJobMetricsDTO batchMetricsDto;


  private static String fileDownloadBasePath = null;

  private static final String geckoDriverExeFullPath = "/usr/bin/geckodriver";
  
  
  private final Logger log = LoggerFactory.getLogger(AyaConnectDataProcessor.class);
  private List<Long> fileSourceJobIDs;
  private RestTemplate restTemplate;
  private String myFile;
  private final MailService mailService;
	private String jobsAdminEmailId;

  
  private FileUploadResponseDTO uploadResponse;
private String serverFilePath = "";
 
  public AyaConnectDataProcessor(MailService mailService, BatchJobMetricsDTO batchMetricsDto)
  {
	  fileDownloadBasePath = GenieProperties.getInstance().getValue("fileDownloadBasePath");
	  chromeDriverForSelenium = GenieProperties.getInstance().getValue("chromeDriverForSelenium");
	  this.mailService = mailService;
	  this.batchMetricsDto = batchMetricsDto;

  }
  
  public void setUp() throws Exception {
	  
	  //System.setProperty("webdriver.chrome.driver", "/opt/selenium/chromedriver/chromedriver"); 
	  System.setProperty("webdriver.chrome.driver", chromeDriverForSelenium); 

	 
      ChromeOptions options = new ChromeOptions();
      options.addArguments("--test-type");
      options.addArguments("--headless");
      options.addArguments("--disable-extensions"); //to disable browser extension popup
      options.addArguments("--no-sandbox");
      options.addArguments("--disable-dev-shm-usage");

      HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
      chromePrefs.put("profile.default_content_settings.popups", 0);
      chromePrefs.put("download.default_directory", fileDownloadBasePath);
      options.setExperimentalOption("prefs", chromePrefs);	 	
      
      
      ChromeDriverService driverService = ChromeDriverService.createDefaultService();
      driver = new ChromeDriver(driverService, options);

      Map<String, Object> commandParams = new HashMap<>();
      commandParams.put("cmd", "Page.setDownloadBehavior");
      Map<String, String> params = new HashMap<>();
      params.put("behavior", "allow");
      params.put("downloadPath", fileDownloadBasePath);
      commandParams.put("params", params);
      
      ObjectMapper objectMapper = new ObjectMapper();
      HttpClient httpClient = HttpClientBuilder.create().build();
      String command = objectMapper.writeValueAsString(commandParams);
      String u = driverService.getUrl().toString() + "/session/" + ((RemoteWebDriver) driver).getSessionId() + "/chromium/send_command";
      HttpPost request = new HttpPost(u);
      request.addHeader("content-type", "text/csv");
      request.setEntity(new StringEntity(command));
      httpClient.execute(request);

    
    SourceSystemDTO sSys = RestApiUtilities.getSourceSystemDetails("AyaConnect");
    //SystemPreferencesDTO sysPrefs = RestApiUtilities.getSystemPreferences("JobsAdminEmailID");
    //jobsAdminEmailId = sysPrefs.getValue();
    jobsAdminEmailId = GenieProperties.getInstance().getValue("jobadminemail");
    
    //baseUrl = "https://www.ayaconnect.com";
    baseUrl = sSys.getWebsiteURL();
    userName = sSys.getUserName();
    password = sSys.getPassword();
    //password = "acb123";
    sourceSystemServerId = sSys.getId();
	  fileStatus = FILE_DOWNLOAD_NOT_STARTED;
    
    driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
    //restTemplate = new RestTemplate();
    //createFolderIfNotExists(fileDownloadBasePath);
}

private void createFolderIfNotExists(String folderPath)
{
    File dir = new File(folderPath);
    if (!dir.exists()) dir.mkdirs();
}

 public void downloadFile()  {
	  fileStatus = FILE_DOWNLOAD_NOT_STARTED;
	  
	  
	    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		String renamePath = fileDownloadBasePath + "AyaConnectJobs" + "_" + timestamp.getTime() + ".csv";
		String renameFileName = "AyaConnectJobs" + "_" + timestamp.getTime() + ".csv";
		 
		log.info("Starting file Download");
		batchMetricsDto.setJobsFileCreatedName(renameFileName);
		batchMetricsDto.setJobsFileCreationStatus("Creating");
		batchMetricsDto.setJobsFileCreateTriggerDateTime(ZonedDateTime.now().toString().toString());
		
		batchMetricsDto.setSourceSystemName("AyaConnect");
		batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());
		batchMetricsDto = RestApiUtilities.insertRecordIntoBatchJobMetrics(batchMetricsDto);
	  
	  try {
		  	setUp();
		  	batchMetricsDto.setSourceSystemId(sourceSystemServerId);
			driver.get(baseUrl);
		    Thread.sleep(10000);
		    driver.findElement(By.id("username")).clear();
		    //driver.findElement(By.id("email")).sendKeys("venkat.nadipelly@geniehealthcare.com");
		    driver.findElement(By.id("username")).sendKeys(userName);
		    
		    driver.findElement(By.id("password")).clear();
		    //driver.findElement(By.id("password")).sendKeys("vngenie23");
		    driver.findElement(By.id("password")).sendKeys(password);
		
		    driver.findElements(By.tagName("button")).get(0).click();
		    //Thread.sleep(50000);
		    
		    WebDriverWait wait = new WebDriverWait(driver, 120000);
		    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"content\"]/div[4]/div/section/section/div/div[1]/div/div[2]/div/button")));// instead of id u can use cssSelector or xpath of ur element.
		
		    // //*[@id="content"]/div[4]/div/section/section/div/div[1]/div/div[2]/div/button
		    WebElement btn = driver.findElement(By.xpath("//*[@id=\"content\"]/div[4]/div/section/section/div/div[1]/div/div[2]/div/button"));
		    log.info(btn.getAttribute("id"));
		    btn.click();
		    
		    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"content\"]/div[4]/div/section/section/div/div[1]/div/div[2]/div/ul/li[1]")));// instead of id u can use cssSelector or xpath of ur element.
		    //Thread.sleep(3000);
		    // //*[@id="content"]/div[4]/div/section/section/div/div[1]/div/div[2]/div/ul/li[1]
		    WebElement li = driver.findElement(By.xpath("//*[@id=\"content\"]/div[4]/div/section/section/div/div[1]/div/div[2]/div/ul/li[1]"));
		    log.info(li.getAttribute("ng-click"));
		    li.click();
		    
		    //Thread.sleep(20000);
		    
		    long totalTimeoutInMillis = 50000;
			FluentWait<WebDriver> wait1 = new FluentWait(driver)
		            .withTimeout(totalTimeoutInMillis, TimeUnit.MILLISECONDS)
		            .pollingEvery(200, TimeUnit.MILLISECONDS);
			
		    File fileToCheck = new File(fileDownloadBasePath + "Jobs.csv");
		
		    wait1.until(new Predicate<WebDriver>() {
		        public boolean apply(WebDriver d) {
		            return fileToCheck.exists();
		        }
		    });
		    
		    log.info("Returned out of drivers navigate method: ");
		    driver.close();
		
		    try {
		    	driver.quit();
		    } catch (Exception ex) {
				  log.warn("Error in quitting the selenium driver after Setup. ", ex);
		    }
		    
		    
			myFile = fileDownloadBasePath + getFileNameOfLastDownloadedFile(fileDownloadBasePath) ;
			log.info("myFile: " + myFile);
		
			File f = new File(myFile);
			boolean fileRenamed = f.renameTo(new File(renamePath));
			log.info("File Renamed: " + fileRenamed);
		
			myFile = renamePath;
			
			log.info("myFile after rename: " + myFile);
			String fileName = f.getName();
			batchMetricsDto.setJobsFileCreationStatus("Uploading");
			batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
			
			uploadResponse = RestApiUtilities.uploadFile(renamePath, renameFileName, sourceSystemServerId);
			
			if (uploadResponse != null) {
				if (uploadResponse.isError()) {
					String to = jobsAdminEmailId;
			  		String subject = "Batch Jobs Application: AyaConnect File Download Failed.";
			  		String content = "Batch Jobs Application: AyaConnect File Download Failed. The Error Details are: " + uploadResponse.getErrorMessage().substring(0,200);
			  		
					log.error("AyaConnect -- Downloading File has Failed. Error Details : " + uploadResponse.getErrorMessage());
					
					batchMetricsDto
							.setJobsFileCreationStatus("Upload Failed: " + uploadResponse.getErrorMessage().substring(0, 200));
					batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString().toString());
		
					batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());
		
					batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
			  		
			  		mailService.sendEmail(to, subject, content, false, false);
			  		
				} else {
					log.info("AyaConnect -- Downloading File has Succeeded.");
					  fileStatus = FILE_DOWNLOAD_SUCCESSFUL;
					  serverFilePath = uploadResponse.getFileFullPathOnServer();
					  
					String to = jobsAdminEmailId;
			  		String subject = "Batch Jobs Application: AyaConnect File Download has been Successful.";
			  		String content = "Batch Jobs Application: AyaConnect File Download has been Successful.";
					
					batchMetricsDto.setJobsFileCreationStatus("Success");
					batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString());
		
					batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());
		
					batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
			  		
			  		mailService.sendEmail(to, subject, content, false, false);
		
				}
			} else {
				log.info("AyaConnect -- Downloading File has Failed as REST API request failed. The REST API reply is NULL.");
				
		  		String to = jobsAdminEmailId;
		  		String subject = "Batch Jobs Application: AyaConnect File Download Failed.";
		  		String content = "Batch Jobs Application: AyaConnect File Download Failed. The Error Details are: The REST API reply is NULL.";
		  		
				batchMetricsDto.setJobsFileCreationStatus("Upload Failed: " + content);
				batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString());
		
				batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());
		
				batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
		  		
		  		mailService.sendEmail(to, subject, content, false, false);
		
			}
	  } catch (Exception ex) {
		  log.error(ex.getMessage(), ex);
          
  		StringWriter sw = new StringWriter();
  		ex.printStackTrace(new PrintWriter(sw));
  		
  		String to = jobsAdminEmailId;
  		String subject = "Batch Jobs Application: AyaConnect File Download Failed.";
  		String content = "Batch Jobs Application: AyaConnect File Download Failed. The Error Details are: " + sw.toString();
  		
  		batchMetricsDto.setJobsFileCreationStatus("Download Failed : " + sw.toString().substring(0, 200));
		batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString().toString());

		batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

		batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
  		
  		mailService.sendEmail(to, subject, content, false, false);
  	
	  }
	  
	  processDownloadedFile();
  }

  public String getFileNameOfLastDownloadedFile(String folderPath)
  	{
  		try {    
  	      
  	         // create new file
  	         File f = new File(folderPath);
  	                                 
  	        FileFilter ff = new FileFilter() {
  	            @Override
  	            public boolean accept(File pathname) {  	               
  	               return pathname.isFile() && pathname.getName().equalsIgnoreCase("jobs.csv");
  	            }
  	        };
  	        		
  	         // array of files and directory
  	         File[] files = f.listFiles(ff);
  	         
  	         Arrays.sort(files, new Comparator<File>(){
  	    	    public int compare(File f1, File f2)
  	    	    {
  	    	        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
  	    	    } });
  	       
  	         return files[files.length-1].getName();
  	      } catch(Exception e) {
  	         
  	         // if any error occurs
  	         e.printStackTrace();
  	         return null;
  	      }
  	}
  
	public void uploadAndProcessFile(String myFile1) {

		String myFilePath = fileDownloadBasePath + myFile1 ;
		myFile = myFilePath;
		String myFileName = myFile1;
		
		fileStatus = FILE_DOWNLOAD_SUCCESSFUL;
		
		uploadResponse = RestApiUtilities.uploadFile(myFilePath, myFileName, sourceSystemServerId);
		
		processDownloadedFile();
	}
    
  	
	public void processDownloadedFile() {
		

		if (fileStatus != FILE_DOWNLOAD_SUCCESSFUL) {
			log.info("File Upload was not successful. So skipping processing of the file.");
			return;
		}
	  //String myFile = "";

		JobsUploadDTO jobsUploadDto = new JobsUploadDTO();
		
	  try
	  {
		log.info("Started processing downloaded excel spreadsheet");
		// TODO Auto-generated method stub
		// Read the file at location: D:\WebDriverDownloads with extension XLSX
		
		//Get the last downloaded file by timestamp
		
		//myFile = fileDownloadBasePath + getFileNameOfLastDownloadedFile(fileDownloadBasePath) ;
		File f = new File(myFile);
		String fileName = f.getName();
		
		//var jobUploadRequest = {"fileName": vm.filename, "fileFullPathOnServer": vm.fileFullPathOnServer, "dateFormat": "MM/dd/yyyy", "jobSource": sourceSys, "removeClosedJobs": vm.removeClosedJobs,"dbColumnsList": vm.mapFieldsDatabase,"excelMappedHeadersList": vm.mapFieldsExcel};
		//vm.jobDbCols = [{"id":"1","name":"City"}, {"id":"2", "name":"State"}, {"id":"3","name":"Profession"}, {"id":"4","name":"Specialty"}, {"id":"5","name":"Shift"}, {"id":"6","name":"StartDate"},{"id":"7","name":"ModifiedDate"},{"id":"8","name":"JobDescription"},{"id":"9","name":"Rate"},{"id":"10","name":"Facility"},{"id":"11","name":"SourceJobID"}];
		//dbColumnsList='[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]', excelMappedHeadersList='[3, 4, 6, 7, 8, 12, 13, null, 11, 2, 1]s'
		
		jobsUploadDto.setDateFormat("MM/dd/yyyy");
		
		Long[] dbColsArray = {new Long(1),new Long(2),new Long(3),new Long(4),new Long(5),new Long(6), new Long(7), new Long(8), new Long(9), new Long(10), new Long(11)};
		List<Long> dbColumnsList = Arrays.asList(dbColsArray);
		
		//jobsUploadDto.setDbColumnNames(dbColumnNames);
		jobsUploadDto.setDbColumnsList(dbColumnsList);
		
		Long[] excelMappedColsArray = {new Long(3),new Long(4),new Long(6),new Long(9),new Long(10),new Long(14), new Long(15), null, new Long(13), new Long(2), new Long(1)};
		List<Long> excelMappedHeadersList = Arrays.asList(excelMappedColsArray);
		
		jobsUploadDto.setExcelMappedHeadersList(excelMappedHeadersList);
		jobsUploadDto.setFileFullPathOnServer(serverFilePath );
		jobsUploadDto.setFileName(fileName);
		jobsUploadDto.setJobSource(sourceSystemServerId);
		jobsUploadDto.setRemoveClosedJobs("Yes");
		
		ListenableFuture<ResponseEntity<String>> futureEntity = RestApiUtilities.processUploadedFile(jobsUploadDto);
		
		futureEntity.addCallback(new ListenableFutureCallback<ResponseEntity<String>>() {
			@Override
			public void onSuccess(ResponseEntity<String> respDto)
			{
				  File localFile = new File(myFile);
				  localFile.delete();
				  
				if (respDto != null)
				{
					if (!respDto.getBody().toLowerCase().equals("ok"))
					{
						  log.error("Error while processing downloaded AyaConnect file named: " + myFile + "; Exception Details: " + respDto.getBody());
						  
					  		String to = jobsAdminEmailId;
					  		String subject = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed.";
					  		String content = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed. The Error Details are: " + respDto.getBody();
					  			  					
					  		mailService.sendEmail(to, subject, content, false, false);	  
					}
					else
					{
						log.info("Processing of jobs in the uploaded file has been Successful.");
				  			  					
				  		String to = jobsAdminEmailId;
				  		String subject = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File has been Successful.";
				  		String content = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File has been Successful.";
				  		
				  	  fileStatus = FILE_PROCESSING_INITIATED;

				  		mailService.sendEmail(to, subject, content, false, false);

				  	}
				}
				else
				{
					  log.error("Error while processing downloaded AyaConnect file named: " + myFile + "; Exception Details: REST API request failed as response is NULL");
					  
				  		String to = jobsAdminEmailId;
				  		String subject = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed.";
				  		String content = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed. The Error Details are: REST API request failed as response is NULL";
				  			  					
				  		mailService.sendEmail(to, subject, content, false, false);
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				  File localFile = new File(myFile);
				  localFile.delete();
				  
				// TODO Auto-generated method stub
				log.error("Error in Listenable Future for File Processing Status.", arg0);
			}
		});
		
	  }
	  catch (Exception ex)
	  {
		  log.error("Error while processing downloaded AyaConnect file named: " + myFile + "; Exception Details: ", ex);
		  //ex.printStackTrace();
		  
	  		StringWriter sw = new StringWriter();
	  		ex.printStackTrace(new PrintWriter(sw));
		  
	  		String to = jobsAdminEmailId;
	  		String subject = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed.";
	  		String content = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed. The Error Details are: " + sw.toString();
	  		
	  		mailService.sendEmail(to, subject, content, false, false);
	  }
	}

/*	public void checkFileProcessingStatus() {
		// TODO Auto-generated method stub
		
		if (fileStatus != FILE_PROCESSING_INITIATED)
		{
			log.info("File Processing not initiated. So skipping status update of the file processing.");
			return;
		}
		
		JobsUploadDTO jobsUploadDto = new JobsUploadDTO();
		
		try
		  {
			log.info("Started processing downloaded excel spreadsheet");
			// TODO Auto-generated method stub
			// Read the file at location: D:\WebDriverDownloads with extension XLSX
			
			//Get the last downloaded file by timestamp
			
			//myFile = fileDownloadBasePath + getFileNameOfLastDownloadedFile(fileDownloadBasePath) ;
			File f = new File(myFile);
			String fileName = f.getName();
			
			//var jobUploadRequest = {"fileName": vm.filename, "fileFullPathOnServer": vm.fileFullPathOnServer, "dateFormat": "MM/dd/yyyy", "jobSource": sourceSys, "removeClosedJobs": vm.removeClosedJobs,"dbColumnsList": vm.mapFieldsDatabase,"excelMappedHeadersList": vm.mapFieldsExcel};
			//vm.jobDbCols = [{"id":"1","name":"City"}, {"id":"2", "name":"State"}, {"id":"3","name":"Profession"}, {"id":"4","name":"Specialty"}, {"id":"5","name":"Shift"}, {"id":"6","name":"StartDate"},{"id":"7","name":"ModifiedDate"},{"id":"8","name":"JobDescription"},{"id":"9","name":"Rate"},{"id":"10","name":"Facility"},{"id":"11","name":"SourceJobID"}];
			//dbColumnsList='[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]', excelMappedHeadersList='[3, 4, 6, 7, 8, 12, 13, null, 11, 2, 1]s'
			
			jobsUploadDto.setDateFormat("MM/dd/yyyy");
			
			Long[] dbColsArray = {new Long(1),new Long(2),new Long(3),new Long(4),new Long(5),new Long(6), new Long(7), new Long(8), new Long(9), new Long(10), new Long(11)};
			List<Long> dbColumnsList = Arrays.asList(dbColsArray);
			
			//jobsUploadDto.setDbColumnNames(dbColumnNames);
			jobsUploadDto.setDbColumnsList(dbColumnsList);
			
			Long[] excelMappedColsArray = {new Long(3),new Long(4),new Long(6),new Long(9),new Long(10),new Long(14), new Long(15), null, new Long(13), new Long(2), new Long(1)};
			List<Long> excelMappedHeadersList = Arrays.asList(excelMappedColsArray);
			
			jobsUploadDto.setExcelMappedHeadersList(excelMappedHeadersList);
			jobsUploadDto.setFileFullPathOnServer(uploadResponse.getFileFullPathOnServer());
			jobsUploadDto.setFileName(fileName);
			jobsUploadDto.setJobSource(sourceSystemServerId);
			jobsUploadDto.setRemoveClosedJobs("Yes");
			
			String respDto = RestApiUtilities.checkFileProcessingStatus(jobsUploadDto);

			if (respDto != null)
			{
				if (!respDto.toLowerCase().equals("success"))
				{
					  log.error("Error while processing downloaded AyaConnect file named: " + myFile + "; Exception Details: " + respDto);
					  
				  		String to = jobsAdminEmailId;
				  		String subject = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed.";
				  		String content = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed. The Error Details are: " + respDto;
				  			  					
				  		mailService.sendEmail(to, subject, content, false, false);	  
				}
				else
				{
					log.info("Processing of jobs in the uploaded file has been Successful.");
			  			  					
			  		String to = jobsAdminEmailId;
			  		String subject = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File has been Successful.";
			  		String content = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File has been Successful.";
			  		
			  	  fileStatus = FILE_PROCESSED;

			  		mailService.sendEmail(to, subject, content, false, false);

			  	}
			}
			else
			{
				  log.error("Error while processing downloaded AyaConnect file named: " + myFile + "; Exception Details: REST API request failed as response is NULL");
				  
			  		String to = jobsAdminEmailId;
			  		String subject = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed.";
			  		String content = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed. The Error Details are: REST API request failed as response is NULL";
			  			  					
			  		mailService.sendEmail(to, subject, content, false, false);
			}

		  }
		  catch (Exception ex)
		  {
			  log.error("Error while processing downloaded AyaConnect file named: " + myFile + "; Exception Details: ", ex);
			  //ex.printStackTrace();
			  
		  		StringWriter sw = new StringWriter();
		  		ex.printStackTrace(new PrintWriter(sw));
			  
		  		String to = jobsAdminEmailId;
		  		String subject = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed.";
		  		String content = "Batch Jobs Application: AyaConnect Processing Contents of Uploaded File Failed. The Error Details are: " + sw.toString();
		  		
		  		mailService.sendEmail(to, subject, content, false, false);
		  }
		
	}*/
	
	public static void main(String args[])
	{
		try
		{
			MailService mailService = new MailService();
			AyaConnectDataProcessor ayaConnect = new AyaConnectDataProcessor(mailService, new BatchJobMetricsDTO());
			ayaConnect.downloadFile();
			//log.info("File Downloaded");
			ayaConnect.processDownloadedFile();
			//Thread.sleep(120000);
			//log.info("File Processing Request being sent");
			//ayaConnect.setUp();
			//ayaConnect.uploadAndProcessFile("AyaConnectJobs_1555090294737.csv");
			
			//String myFile = fileDownloadBasePath + ayaConnect.getFileNameOfLastDownloadedFile(fileDownloadBasePath) ;
			//File f = new File(myFile);
			//String fileName = f.getName();
			
			//RestApiUtilities.uploadFile(myFile, fileName);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
