package com.genie.job.portal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
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
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.FirefoxBinary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.genie.job.portal.medefis.rest.dto.GetBearerTokenReplyDTO;
import com.genie.job.portal.medefis.rest.dto.GetBearerTokenRequestDTO;
import com.genie.job.portal.medefis.rest.dto.JobDTO;

@SuppressWarnings("unused")
public class MedefisRestApiDataProcessor{
	
  private static int FILE_DOWNLOAD_NOT_STARTED = 1;
  private static int FILE_DOWNLOAD_SUCCESSFUL = 2;
  private static int FILE_PROCESSING_INITIATED = 3;
  private static int FILE_PROCESSED = 4;

  private WebDriver driver;
  private String baseUrl;
  private String userName;
  private String password;
  private Long sourceSystemServerId;
  private int fileStatus;
  private String chromeDriverForSelenium;
  private BatchJobMetricsDTO batchMetricsDto;
  

  private static String fileDownloadBasePath = null;
  
  
  private final Logger log = LoggerFactory.getLogger(MedefisRestApiDataProcessor.class);
  private List<Long> fileSourceJobIDs;
  
  private FileUploadResponseDTO uploadResponse;
  private String myFile = null;
  
  private String fileName = null;
	private String awsBucketName;
	private MailService mailService;
	private String jobsAdminEmailId;
	private String serverFilePath = "";
  
  private static RestTemplate restTemplate = new RestTemplate();
 
  public MedefisRestApiDataProcessor(MailService mailService, BatchJobMetricsDTO batchMetricsDto)
  {
	  fileDownloadBasePath = GenieProperties.getInstance().getValue("fileDownloadBasePath");
	  chromeDriverForSelenium = GenieProperties.getInstance().getValue("chromeDriverForSelenium");
	  this.mailService = mailService;
	  this.batchMetricsDto = batchMetricsDto;
  }
  
  public void setUp() {
	  
	  restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
	  restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

    
	    SourceSystemDTO sSys = RestApiUtilities.getSourceSystemDetails("Medefis");

	    jobsAdminEmailId = GenieProperties.getInstance().getValue("jobadminemail");
	    
	    baseUrl = sSys.getWebsiteURL();
	    userName = sSys.getUserName();
	    password = sSys.getPassword();
	    sourceSystemServerId = sSys.getId();
	    awsBucketName = sSys.getAwsBucketName();
	    fileStatus = FILE_DOWNLOAD_NOT_STARTED;
	    
	    createFolderIfNotExists(fileDownloadBasePath);
	}
	
	private void createFolderIfNotExists(String folderPath)
	{
	    File dir = new File(folderPath);
	    if (!dir.exists()) dir.mkdirs();
	}
  
  public String getBearerToken() throws Exception 
  {
	  String url = "https://identity.medefis5.com/identity/connect/token";
	  
	  GetBearerTokenRequestDTO getBearerTokenRequestDTO = new GetBearerTokenRequestDTO();
	  getBearerTokenRequestDTO.setClient_id("httpAgencyRest");
	  getBearerTokenRequestDTO.setClient_secret("agency");
	  getBearerTokenRequestDTO.setGrant_type("password");
	  getBearerTokenRequestDTO.setScope("apiExternalAgency");
	  getBearerTokenRequestDTO.setUsername(userName);
	  getBearerTokenRequestDTO.setPassword(password);
	  
	  HttpHeaders headers = new HttpHeaders();
	  //headers.set("Content-Type", "application/x-www-form-urlencoded");      
	  headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	  
	  MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
	  map.add("client_id", "httpAgencyRest");
	  map.add("client_secret", "agency");
	  map.add("password", password); //Orange@95
	  map.add("grant_type", "password");
	  map.add("scope", "apiExternalAgency");
	  map.add("username", userName);
	  
	  HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

	  GetBearerTokenReplyDTO getBearerTokenReplyDTO = restTemplate.postForObject(url, request, GetBearerTokenReplyDTO.class);
	  
      return getBearerTokenReplyDTO.getAccess_token();
	 
  }
  
  public boolean pingGetOpenJobs(String bearerToken) throws Exception 
  {
	  String url = "https://agencyapi.medefis5.com/api/AgencyOpenJob/Ping";
	  
	  HttpHeaders headers = new HttpHeaders();
	  headers.add("Authorization", "Bearer " + bearerToken);

	  HttpEntity<?> entity = new HttpEntity<>(headers);

	  String reply = restTemplate.postForObject(url, entity, String.class);
	  return reply.contains("200");
  }
  
  public boolean pingGetClosedJobs(String bearerToken) throws Exception 
  {
	  String url = "https://agencyapi.medefis5.com/api/AgencyClosedJob/Ping";
	  
	  HttpHeaders headers = new HttpHeaders();
	  headers.add("Authorization", "Bearer " + bearerToken);

	  HttpEntity<?> entity = new HttpEntity<>(headers);

	  String reply = restTemplate.postForObject(url, entity, String.class);
	  return reply.contains("200");
  }
  
  public boolean pingGetCancelledJobs(String bearerToken) throws Exception 
  {
	  String url = "https://agencyapi.medefis5.com/api/AgencyCancelledJob/Ping";
	  
	  HttpHeaders headers = new HttpHeaders();
	  headers.add("Authorization", "Bearer " + bearerToken);

	  HttpEntity<?> entity = new HttpEntity<>(headers);

	  String reply = restTemplate.postForObject(url, entity, String.class);
	  return reply.contains("200");
  }
  
  public JobDTO[] getOpenJobs(String bearerToken) throws Exception 
  {
	  String url = "https://agencyapi.medefis5.com/api/AgencyOpenJob/GetOpenJobs";
	  HttpHeaders headers = new HttpHeaders();
	  headers.add("Authorization", "Bearer " + bearerToken);

	  MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
	  
	  HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
	  JobDTO[] jobs = restTemplate.postForObject(url, request, JobDTO[].class);

	  return jobs;

  }
  
  public JobDTO[] getClosedJobs(String bearerToken) throws Exception 
  { 
	  String url = "https://agencyapi.medefis5.com/api/AgencyClosedJob/GetClosedJobs";
	  HttpHeaders headers = new HttpHeaders();
	  headers.add("Authorization", "Bearer " + bearerToken);

	  MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();

	  
	  HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
	  JobDTO[] jobs = restTemplate.postForObject(url, request, JobDTO[].class);

	  return jobs;

  }

  public JobDTO[] getCancelledJobs(String bearerToken) throws Exception 
  {
	  String url = "https://agencyapi.medefis5.com/api/AgencyCancelledJob/GetCancelledJobs";
	  HttpHeaders headers = new HttpHeaders();
	  headers.add("Authorization", "Bearer " + bearerToken);

	  MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();

	  
	  HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
	  JobDTO[] jobs = restTemplate.postForObject(url, request, JobDTO[].class);

	  return jobs;

  }
  
  public void appendJobsToLines(JobDTO[] jobs, List<String> lines) throws Exception 
  {
	  for (JobDTO job : jobs)
		  lines.add(job.toString());
  }
  
  public void downloadFile() {
  
	try
	{
		batchMetricsDto.setJobsFileCreationStatus("Creating");
		batchMetricsDto.setJobsFileCreateTriggerDateTime(ZonedDateTime.now().toString().toString());
		
		batchMetricsDto.setSourceSystemName("Medefis");
		batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());
		
		batchMetricsDto = RestApiUtilities.insertRecordIntoBatchJobMetrics(batchMetricsDto);
		 setUp();
		 batchMetricsDto.setSourceSystemId(sourceSystemServerId);

	
	  List<String> lines = new ArrayList<String>();
	  lines.add(new JobDTO().getHeader());
	  	
	  String authToken = getBearerToken();

		  JobDTO[] openJobs = getOpenJobs(authToken);
		  log.info("# of Open Jobs Downloaded : " + openJobs.length);

		  appendJobsToLines(openJobs, lines);
		  log.info("Size of Lines is : " + lines.size());

	  
	  
	    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		myFile = fileDownloadBasePath + "MedefisRestApiJobs" + "_" + timestamp.getTime() + ".csv";
		fileName = "MedefisRestApiJobs" + "_" + timestamp.getTime() + ".csv";
	  
	  Path file = Paths.get(myFile);
	  Files.write(file, lines, Charset.forName("UTF-8"));
		batchMetricsDto.setJobsFileCreationStatus("Uploading");
		batchMetricsDto.setJobsFileCreatedName(fileName);
		batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);

	  uploadResponse = RestApiUtilities.uploadFile(myFile, fileName, sourceSystemServerId);
	  
	  serverFilePath = uploadResponse.getFileFullPathOnServer();
	  
		if (uploadResponse != null) {
			if (uploadResponse.isError()) {
				log.error("Medefis REST API - Jobs File Creation / AWS S3 upload has Failed. Error Details : " + uploadResponse.getErrorMessage().substring(0,200));
		  		
		  		String to = jobsAdminEmailId;
		  		String subject = "Batch Jobs Application: Medefis File Download Failed.";
		  		String content = "Batch Jobs Application: Medefis File Download Failed. The Error Details are: " + uploadResponse.getErrorMessage().substring(0,200);
		  		
				batchMetricsDto.setJobsFileCreationStatus(
						"Upload Failed: " + uploadResponse.getErrorMessage().substring(0, 200));
				batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString().toString());

				batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

				batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
		  		
		  		mailService.sendEmail(to, subject, content, false, false);
			} else {
				log.info("Medefis REST API - Jobs File Creation / AWS S3 upload has Succeeded.");
				fileStatus = FILE_DOWNLOAD_SUCCESSFUL;
		  		
				String to = jobsAdminEmailId;
		  		String subject = "Batch Jobs Application: Medefis File Download has been Successful.";
		  		String content = "Batch Jobs Application: Medefis File Download has been Successful.";
		  		
		  		batchMetricsDto.setJobsFileCreationStatus("Success");
				batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString());

				batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

				batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
		  		
		  		mailService.sendEmail(to, subject, content, false, false);
		  	}
		} else {
			log.error("Medefis REST API - Jobs File Creation / AWS S3 upload failed. The REST API reply is NULL.");
	  		
	  		String to = jobsAdminEmailId;
	  		String subject = "Batch Jobs Application: Medefis File Download Failed.";
	  		String content = "Batch Jobs Application: Medefis File Download Failed. The Error Details are: The REST API reply is NULL.";
	  		
	  		batchMetricsDto.setJobsFileCreationStatus("Upload Failed: " + content);
			batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString());
	
			batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

			batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
	  		
	  		mailService.sendEmail(to, subject, content, false, false);
		}	  
	} catch (Exception ex) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		
		String to = jobsAdminEmailId;
		String subject = "Batch Jobs Application: Medefis File Download Failed.";
		String content = "Batch Jobs Application: Medefis File Download Failed. The Error Details are: " + sw.toString();
  		
		batchMetricsDto.setJobsFileCreationStatus("Download Failed : " + sw.toString().substring(0, 200));
		batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString().toString());

		batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

		batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
  		
  		mailService.sendEmail(to, subject, content, false, false);
	}
	
	processDownloadedFile();
	
  }
  	
	public void processDownloadedFile() {
		
		if (fileStatus != FILE_DOWNLOAD_SUCCESSFUL) 
		{
			log.info("File Upload was not successful. So skipping processing of the file.");
			return;
		}
	 // String myFile = "";
		
		JobsUploadDTO jobsUploadDto = new JobsUploadDTO();

		
	  try
	  {
		log.info("Started processing the created file.");
		
			
		jobsUploadDto.setDateFormat("MM/dd/yyyy");
		
		Long[] dbColsArray = {new Long(1),new Long(2),new Long(3),new Long(4),new Long(5),new Long(6), new Long(7), new Long(8), new Long(9), new Long(10), new Long(11), new Long(12)};
		List<Long> dbColumnsList = Arrays.asList(dbColsArray);
		
		jobsUploadDto.setDbColumnsList(dbColumnsList);
		

		Long[] excelMappedColsArray = {new Long(1),new Long(2),new Long(3),new Long(4),new Long(5),new Long(6), new Long(7), new Long(8), new Long(9), new Long(10), new Long(11), new Long(12)};
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
				Path file = Paths.get(myFile);
				  file.toFile().delete();
				if (respDto != null)
				{
					if (!respDto.getBody().toLowerCase().equals("ok"))
					{
						log.error("Error while processing downloaded Medefis REST API created file named: " + myFile + "; Exception Details: " + respDto.getBody());
						  
				  		String to = jobsAdminEmailId;
				  		String subject = "Batch Jobs Application: Medefis Processing Contents of Uploaded File Failed.";
				  		String content = "Batch Jobs Application: Medefis Processing Contents of Uploaded File Failed. The Error Details are: " + respDto.getBody();
				  			  					
				  		mailService.sendEmail(to, subject, content, false, false);  
					}
					else
					{
						log.info("Processing of jobs in the uploaded file has been Successful.");
						
						String to = jobsAdminEmailId;
				  		String subject = "Batch Jobs Application: Medefis Processing Contents of Uploaded File has been Successful.";
				  		String content = "Batch Jobs Application: Medefis Processing Contents of Uploaded File has been Successful.";
				  		
				  		fileStatus = FILE_PROCESSING_INITIATED;
				  		
				  		mailService.sendEmail(to, subject, content, false, false);

				  	}
				}
				else
				{
					  log.error("Error while processing downloaded Medefis REST API created file named: " + myFile + "; Exception Details: REST API request failed as response is NULL.");
					  
				  		String to = jobsAdminEmailId;
				  		String subject = "Batch Jobs Application: Medefis Processing Contents of Uploaded File Failed.";
				  		String content = "Batch Jobs Application: Medefis Processing Contents of Uploaded File Failed. The Error Details are: REST API request failed as response is NULL.";
				  			  					
				  		mailService.sendEmail(to, subject, content, false, false);
				}
			}

			@Override
			public void onFailure(Throwable arg0) {
				Path file = Paths.get(myFile);
				  file.toFile().delete();
				  log.error("Error in Listenable Future for File Processing Status.", arg0);
			}
		});
	  }
	  catch (Exception ex)
	  {
		  log.error("Error while processing created Medefis file named: " + myFile + ";; Exception Details: ", ex);
		  //ex.printStackTrace(System.out);
	  		StringWriter sw = new StringWriter();
	  		ex.printStackTrace(new PrintWriter(sw));
		  
	  		String to = jobsAdminEmailId;
	  		String subject = "Batch Jobs Application: Medefis Processing Contents of Uploaded File Failed.";
	  		String content = "Batch Jobs Application: Medefis Processing Contents of Uploaded File Failed. The Error Details are: " + sw.toString();
	  			  					
	  		mailService.sendEmail(to, subject, content, false, false);
	  }
	}
	
	
public static void main(String args[])
{
	try
	{
		MailService mailService = new MailService();
		MedefisRestApiDataProcessor m = new MedefisRestApiDataProcessor(mailService, new BatchJobMetricsDTO());
		m.downloadFile();
		Thread.sleep(5000);
		m.processDownloadedFile();
		

	}
	catch (Exception ex)
	{
		ex.printStackTrace();
	}
}

}
