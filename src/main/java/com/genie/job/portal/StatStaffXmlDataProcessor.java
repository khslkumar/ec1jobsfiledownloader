package com.genie.job.portal;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

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


@SuppressWarnings("unused")
public class StatStaffXmlDataProcessor{
  private static final String DATE_ASAP = "ASAP";
private WebDriver driver;
  private String baseUrl;
  private String userName;
  private String password;
  private Long sourceSystemServerId;
	private MailService mailService;
	private String jobsAdminEmailId;
  
	
	  private static int FILE_DOWNLOAD_NOT_STARTED = 1;
	  private static int FILE_DOWNLOAD_SUCCESSFUL = 2;
	  private static int FILE_PROCESSING_INITIATED = 3;
	  private static int FILE_PROCESSED = 4;
	  
	  private int fileStatus;
	  private String chromeDriverForSelenium;

	 
  private static String fileDownloadBasePath = null;
  
  private final Logger log = LoggerFactory.getLogger(StatStaffXmlDataProcessor.class);
  private List<Long> fileSourceJobIDs;
  
  private String myFile = null;
  
  private String fileName = null;
  private FileUploadResponseDTO uploadResponse;
  private String serverFilePath;
  private BatchJobMetricsDTO batchMetricsDto;
  
  private static RestTemplate restTemplate = new RestTemplate();
 
  public StatStaffXmlDataProcessor(MailService mailService, BatchJobMetricsDTO batchMetricsDto) {
	  fileDownloadBasePath = GenieProperties.getInstance().getValue("fileDownloadBasePath");
	  chromeDriverForSelenium = GenieProperties.getInstance().getValue("chromeDriverForSelenium");
	  this.mailService = mailService;
	  this.batchMetricsDto = batchMetricsDto;
  }
  
  public void setUp() { 
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
	
	    
	    SourceSystemDTO sSys = RestApiUtilities.getSourceSystemDetails("Stat Staff");

	    jobsAdminEmailId = GenieProperties.getInstance().getValue("jobadminemail");
	    
	    sourceSystemServerId = sSys.getId();
	    fileStatus = FILE_DOWNLOAD_NOT_STARTED;
	    createFolderIfNotExists(fileDownloadBasePath);
  }
  
  private void createFolderIfNotExists(String folderPath)
  {
	    File dir = new File(folderPath);
	    if (!dir.exists()) dir.mkdirs();
  }
  	
	public void processDownloadedFile() {
	 // String myFile = "";
		if (fileStatus != FILE_DOWNLOAD_SUCCESSFUL) {
			log.debug("File Upload was not successful. So skipping processing of the file.");
			return;
		}
		
		JobsUploadDTO jobsUploadDto = new JobsUploadDTO();

	  try
	  {
		log.debug("Started processing the created file.");
		
		jobsUploadDto.setDateFormat("MM/dd/yyyy");
		
		Long[] dbColsArray = {new Long(1),new Long(2),new Long(3),new Long(4),new Long(5),new Long(6), new Long(7), new Long(8), new Long(9), new Long(10), new Long(11)};
		List<Long> dbColumnsList = Arrays.asList(dbColsArray);
		
		jobsUploadDto.setDbColumnsList(dbColumnsList);
		
		Long[] excelMappedColsArray = {new Long(1), new Long(2), new Long(4), new Long(3) , new Long(5), new Long(6), null, null, new Long(8), new Long(9), new Long(10)};
		List<Long> excelMappedHeadersList = Arrays.asList(excelMappedColsArray);
		
		jobsUploadDto.setExcelMappedHeadersList(excelMappedHeadersList);
		jobsUploadDto.setFileFullPathOnServer(serverFilePath);
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
						log.error("Error while processing downloaded Statstaff file named: " + myFile + "; Exception Details: " + respDto.getBody());
						  
				  		String to = jobsAdminEmailId;
				  		String subject = "Batch Jobs Application: StatStaff Processing Contents of Uploaded File Failed.";
				  		String content = "Batch Jobs Application: StatStaff Processing Contents of Uploaded File Failed. The Error Details are: " + respDto.getBody();
				  			  					
				  		mailService.sendEmail(to, subject, content, false, false);	  
					}
					else
					{
						log.debug("Processing of jobs in the uploaded Statstaff file has been Successful.");

					    fileStatus = FILE_PROCESSING_INITIATED;

						String to = jobsAdminEmailId;
				  		String subject = "Batch Jobs Application: StatStaff Processing Contents of Uploaded File has been Successful.";
				  		String content = "Batch Jobs Application: StatStaff Processing Contents of Uploaded File has been Successful.";
				  		
				  		mailService.sendEmail(to, subject, content, false, false);

				  	}
				}
				else
				{
					log.error("Error while processing downloaded Statstaff file named: " + myFile + "; Exception Details: REST API request failed as response is NULL");
					  
			  		String to = jobsAdminEmailId;
			  		String subject = "Batch Jobs Application: StatStaff Processing Contents of Uploaded File Failed.";
			  		String content = "Batch Jobs Application: StatStaff Processing Contents of Uploaded File Failed. The Error Details are: StatStaff -- REST API request failed. The REST API reply is NULL.";
			  			  					
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
		  log.error("Error while processing created StatStaff file named: " + myFile + ";; Exception Details: " + ex.getMessage(), ex);
		  //ex.printStackTrace(System.out);
		  
  		StringWriter sw = new StringWriter();
  		ex.printStackTrace(new PrintWriter(sw));
  		
	  		String to = jobsAdminEmailId;
	  		String subject = "Batch Jobs Application: StatStaff Processing Contents of Uploaded File Failed.";
	  		String content = "Batch Jobs Application: StatStaff Processing Contents of Uploaded File Failed. The Error Details are: " + sw.toString();
	  			  					
	  		mailService.sendEmail(to, subject, content, false, false);
	  }
	}
	
	public final String getEventTypeString(int eventType) {
	    switch (eventType) {
	        case XMLEvent.START_ELEMENT:
	            return "START_ELEMENT";

	        case XMLEvent.END_ELEMENT:
	            return "END_ELEMENT";

	        case XMLEvent.PROCESSING_INSTRUCTION:
	            return "PROCESSING_INSTRUCTION";

	        case XMLEvent.CHARACTERS:
	            return "CHARACTERS";

	        case XMLEvent.COMMENT:
	            return "COMMENT";

	        case XMLEvent.START_DOCUMENT:
	            return "START_DOCUMENT";

	        case XMLEvent.END_DOCUMENT:
	            return "END_DOCUMENT";

	        case XMLEvent.ENTITY_REFERENCE:
	            return "ENTITY_REFERENCE";

	        case XMLEvent.ATTRIBUTE:
	            return "ATTRIBUTE";

	        case XMLEvent.DTD:
	            return "DTD";

	        case XMLEvent.CDATA:
	            return "CDATA";

	        case XMLEvent.SPACE:
	            return "SPACE";
	    }
	    return "UNKNOWN_EVENT_TYPE , " + eventType;
	}
	
	   private void printEventType(int eventType) {        
	        log.debug("EVENT TYPE("+eventType+") = " + getEventTypeString(eventType));
	    }
	    
	   private void printStartDocument(XMLStreamReader xmlr){
		          if(xmlr.START_DOCUMENT == xmlr.getEventType()){
	            log.debug("<?xml version=\"" + xmlr.getVersion() + "\"" + " encoding=\"" + xmlr.getCharacterEncodingScheme() + "\"" + "?>");
	        }
	    }
	    
	   private void printComment(XMLStreamReader xmlr){
		          if(xmlr.getEventType() == xmlr.COMMENT){
	            log.info("<!--" + xmlr.getText() + "-->");
	        }
	    }
	            
		      private void printText(XMLStreamReader xmlr){
		          if(xmlr.hasText()){
	            log.info(xmlr.getText());
	        }
	    }
	    
		      private void printPIData(XMLStreamReader xmlr){
		          if (xmlr.getEventType() == XMLEvent.PROCESSING_INSTRUCTION){
	            log.info("<?" + xmlr.getPITarget() + " " + xmlr.getPIData() + "?>") ;
	        }
	    }
	    
		      private void printStartElement(XMLStreamReader xmlr){
		          if(xmlr.isStartElement()){
	            log.info("<" + xmlr.getName().toString());
	            printAttributes(xmlr);
	            log.info(">");
	        }
	    }
	    
		      private void printEndElement(XMLStreamReader xmlr){
		          if(xmlr.isEndElement()){
	            log.info("</" + xmlr.getName().toString() + ">");
	        }
	    }
	    
		      private void printAttributes(XMLStreamReader xmlr){
	        int count = xmlr.getAttributeCount() ;
		          if(count > 0){
		              for(int i = 0 ; i < count ; i++) {
	                log.info(" ");
	                log.info(xmlr.getAttributeName(i).toString());
	                log.info("=");
	                log.info("\"");
	                log.info(xmlr.getAttributeValue(i));
	                log.info("\"");
	            }            
	        }
	        
	        count = xmlr.getNamespaceCount();
		          if(count > 0){
		              for(int i = 0 ; i < count ; i++) {
	                log.info(" ");
	                log.info("xmlns");
		                  if(xmlr.getNamespacePrefix(i) != null ){
		                	  log.info(":" + xmlr.getNamespacePrefix(i));
	                }                
	                log.info("=");
	                log.info("\"");
	                log.info(xmlr.getNamespaceURI(i));
	                log.info("\"");
	            }            
	        }
	    }
		      
		private String removeCData(String in)
		{
			return in.substring(8,in.length()-2);
		}
		
		public String removeWhiteCharsAndResizeString(String in) {
			//String val = description.replaceAll("^ | $|\\n ", "");
			String val = in == null ? null : in.replaceAll("\\t", " ").replaceAll("\\n", "; ").replaceAll(",",".");
			
			String retval = val == null ? null : (val.length() <= 200 ? val : val.substring(0, 200));
			return retval;
		}
		
	public void downloadFile()
	{

	    fileStatus = FILE_DOWNLOAD_NOT_STARTED;
		batchMetricsDto.setJobsFileCreationStatus("Creating");
		batchMetricsDto.setJobsFileCreateTriggerDateTime(ZonedDateTime.now().toString().toString());
		
		batchMetricsDto.setSourceSystemName("Stat Staff");
		batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());
		batchMetricsDto = RestApiUtilities.insertRecordIntoBatchJobMetrics(batchMetricsDto);
		
		try
		{
			  setUp();
			  batchMetricsDto.setSourceSystemId(sourceSystemServerId);

			List<String> lines = new ArrayList<String>();
			lines.add("City, StateCode, Specialty, Profession, Shift, StartDate, Description, Rate, FacilityName, SourceJobId");
			  
			InputStream inputStream = new URL("https://statstafflogin.com/files/publish/open_contracts_000117.xml").openStream();

			XMLInputFactory f = XMLInputFactory.newInstance();
			XMLStreamReader xmlr = f.createXMLStreamReader(inputStream);
			int eventType = 0;
	
		  
	        while(xmlr.hasNext()){
	        	String city = null;
	        	String state = null;
	        	String specialty = null;
	        	String profession = null;
	        	String shift = null;
	        	String startDate = null;
	        	String modifiedDate = null;
	        	String description = null;
	        	String rate = null;
	        	String facility = null;
	        	String sourceJobID = null;
	        	String status = null;
	        	String startAsap = null;
	        	
                eventType = xmlr.next();  
                String jobDataLine = "";
                
                if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("job"))
                {
                	log.debug("New Job Details ...");
                	eventType = xmlr.next();        eventType = xmlr.next();
                	log.debug("XML Reader Element at Cursor: " + xmlr.getLocalName());

                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("jobid"))
                    	{sourceJobID = xmlr.getElementText();
                    	log.debug("New Job's ID :" + sourceJobID);}

                	eventType = xmlr.next();        eventType = xmlr.next();
                	log.debug("XML Reader Element at Cursor: " + xmlr.getLocalName());

                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("jobtitle"))
                    	log.debug("New Job's jobtitle :" + removeCData(xmlr.getElementText()));
                    
                	eventType = xmlr.next();        eventType = xmlr.next();
                	
                	log.debug("XML Reader Element at Cursor: " + xmlr.getLocalName());
                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("city"))
                    	{city = xmlr.getElementText();
                    	log.debug("New Job's city :" + city);}
                    
                	eventType = xmlr.next();        eventType = xmlr.next();

                	log.debug("XML Reader Element at Cursor: " + xmlr.getLocalName());
                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("state"))
                    	{state = xmlr.getElementText();
                    	log.debug("New Job's state :" + state);}
                    
                	eventType = xmlr.next();        eventType = xmlr.next();

                	log.debug("XML Reader Element at Cursor: " + xmlr.getLocalName());
                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("license"))
                    	{profession = xmlr.getElementText();
                    	log.debug("New Job's license :" + profession);}
                    
                	eventType = xmlr.next();        eventType = xmlr.next();

                	log.debug("XML Reader Element at Cursor: " + xmlr.getLocalName());
                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("specialty"))
                    	{specialty = xmlr.getElementText();
                    	log.debug("New Job's specialty :" + specialty);}
                    
                	eventType = xmlr.next();        eventType = xmlr.next();

                	log.debug("XML Reader Element at Cursor: " + xmlr.getLocalName());
                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("shift"))
                    	{shift  = xmlr.getElementText();
                    	log.debug("New Job's shift :" + shift);}
                    
                	eventType = xmlr.next();        eventType = xmlr.next();

                	log.debug("XML Reader Element at Cursor: " + xmlr.getLocalName());
                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("startdate"))
                    	{startDate = xmlr.getElementText();
                    		if (startDate != null && startDate.trim().length()!=0) startDate = startDate.substring(0,6) + "20" + startDate.substring(6);
                    	log.debug("New Job's startdate :" + startDate);}
                    
                	eventType = xmlr.next();        eventType = xmlr.next();

                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("enddate"))
                    	log.debug("New Job's enddate :" + xmlr.getElementText());
                    
                    //start-asap
                	eventType = xmlr.next();        eventType = xmlr.next();

                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("start-asap"))
                	{
                    	startAsap = xmlr.getElementText();
                    	log.debug("New Job's start-asap :" + startAsap);
                    }
                    
                	eventType = xmlr.next();        eventType = xmlr.next();
                	eventType = xmlr.next();        eventType = xmlr.next();
                	eventType = xmlr.next();        eventType = xmlr.next();
                	eventType = xmlr.next();        eventType = xmlr.next();
                	eventType = xmlr.next();        eventType = xmlr.next();

                	log.debug("XML Reader Element at Cursor: " + xmlr.getLocalName());
                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("facility"))
                    	{facility  = xmlr.getElementText();
                    	log.debug("New Job's facility :" + facility);}
                    
                    moveToNextStartElement(xmlr, 1);
                	log.debug("XML Reader Element at Cursor: " + xmlr.getLocalName());
                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("bill-rate"))
                    	{rate  = xmlr.getElementText();
                    	log.debug("New Job's bill-rate :" + rate);}
                    
                    moveToNextStartElement(xmlr, 5);

                	
                	log.debug("XML Reader Element at Cursor: " + xmlr.getLocalName());
                    if (xmlr.getEventType() == XMLEvent.START_ELEMENT && xmlr.getLocalName().equals("description"))
                    	{description  = removeCData(xmlr.getElementText());
                    	log.debug("New Job's description :" + description);}
      	        	//Header: "City, StateCode, Specialty, Profession, Shift, StartDate, Description, Rate, FacilityName, SourceJobId";

                    jobDataLine = city + "," + state + "," + ( (specialty==null) || (specialty.trim().length()==0) ? profession : specialty) + "," + profession +  "," + shift + "," + ( ( ( (startDate==null) || (startDate.trim().length()==0) ) && startAsap.trim().equalsIgnoreCase("true")) ? DATE_ASAP : startDate) + "," + removeWhiteCharsAndResizeString(description)  + "," + rate + "," + facility + "," + sourceJobID; 
                    log.debug("Job DATA line: " + jobDataLine);
                    lines.add(jobDataLine);
                }
                
   

            }
		    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			
			myFile = fileDownloadBasePath + "StatStaffXmlFileJobs" + "_" + timestamp.getTime() + ".csv";
			fileName = "StatStaffXmlFileJobs" + "_" + timestamp.getTime() + ".csv";
			batchMetricsDto.setJobsFileCreatedName(fileName);
			
	        Path file = Paths.get(myFile);
      	  	Files.write(file, lines, Charset.forName("UTF-8"));
      	  	
      	  	//Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
			batchMetricsDto.setJobsFileCreationStatus("Uploading");
			batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);

      	  	uploadResponse = RestApiUtilities.uploadFile(myFile, fileName,sourceSystemServerId);

      	  	serverFilePath = uploadResponse.getFileFullPathOnServer();
	  		
  		if (uploadResponse != null)
  		{
  			if (uploadResponse.isError())
  			{
  				log.error("Statstaff Jobs File Creation / AWS S3 upload has Failed. Error Details : " + uploadResponse.getErrorMessage());
  				
  		  		
  		  		String to = jobsAdminEmailId;
  		  		String subject = "Batch Jobs Application: StatStaff File Download Failed.";
  		  		String content = "Batch Jobs Application: StatStaff File Download Failed. The Error Details are: " + uploadResponse.getErrorMessage();
  		  		
  				batchMetricsDto.setJobsFileCreationStatus(
  						"Upload Failed: " + uploadResponse.getErrorMessage().substring(0, 200));
  				batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString().toString());

  				batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

  				batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
  		  		
  		  		mailService.sendEmail(to, subject, content, false, false);
  			}
  			else
  			{
  				log.debug("Statstaff Jobs File Creation / AWS S3 upload has Succeeded.");
  			    fileStatus = FILE_DOWNLOAD_SUCCESSFUL;
  		  		
  		  		String to = jobsAdminEmailId;
  		  		String subject = "Batch Jobs Application: StatStaff File Download has been Successful.";
  		  		String content = "Batch Jobs Application: StatStaff File Download has been Successful.";
  		  		
  		  		batchMetricsDto.setJobsFileCreationStatus("Success");
  				batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString());

  				batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

  				batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
  		  		mailService.sendEmail(to, subject, content, false, false);

  			}
  		}
  		else
  		{
  			log.debug("Statstaff Jobs File Creation / AWS S3 upload failed. The REST API reply is NULL.");
  		
  	  		String to = jobsAdminEmailId;
  	  		String subject = "Batch Jobs Application: StatStaff File Download Failed.";
  	  		String content = "Batch Jobs Application: StatStaff File Download Failed. The Error Details are: The REST API reply is NULL.";
  	  		
  	  		batchMetricsDto.setJobsFileCreationStatus("Upload Failed: " + content);
  			batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString());

  			batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

  			batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
  	  		
  	  		mailService.sendEmail(to, subject, content, false, false);
  		}
  		
        } catch(Exception ex) {
            log.error(ex.getMessage(), ex);
            
        		StringWriter sw = new StringWriter();
        		ex.printStackTrace(new PrintWriter(sw));
        		
        		String to = jobsAdminEmailId;
        		String subject = "Batch Jobs Application: StatStaff File Download Failed.";
        		String content = "Batch Jobs Application: StatStaff File Download Failed. The Error Details are: " + sw.toString();
        		mailService.sendEmail(to, subject, content, false, false);
        		
        		batchMetricsDto.setJobsFileCreationStatus("Download Failed : " + sw.toString().substring(0, 200));
        		batchMetricsDto.setJobsFileCreateCompletionDateTime(ZonedDateTime.now().toString().toString());

        		batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());

        		batchMetricsDto = RestApiUtilities.updateRecordIntoBatchJobMetrics(batchMetricsDto);
          		
          		mailService.sendEmail(to, subject, content, false, false);
        }
		  
	
	
		processDownloadedFile();
	}
	
	
	private void moveToNextStartElement(XMLStreamReader xmlr, int elementCount) throws XMLStreamException {
		// TODO Auto-generated method stub
		for (int i=0; i < elementCount; i++)
		{		
			xmlr.next();
			while (xmlr.getEventType() != XMLEvent.START_ELEMENT)	
				xmlr.next();
		}
		
	}

	public static void main(String args[]){
		try
		{
			MailService mailService = new MailService();

			StatStaffXmlDataProcessor stat = new StatStaffXmlDataProcessor(mailService, new BatchJobMetricsDTO());
			stat.downloadFile();
			Thread.sleep(10000);
			stat.processDownloadedFile();
		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.out);
		}
	}
}
