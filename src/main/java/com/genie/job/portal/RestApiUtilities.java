package com.genie.job.portal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRequestCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.genie.job.portal.FileUploadResponseDTO;

public class RestApiUtilities {

    private static final Logger log = LoggerFactory.getLogger(RestApiUtilities.class);
    private static SourceSystemDTO[] sourceSystemsList = null;
    
	private static RestTemplate restTemplate = new RestTemplate();
	private static AsyncRestTemplate asyncTempl = new AsyncRestTemplate();

	private static String jobsPortalBaseUrl = GenieProperties.getInstance().getValue("webSiteBaseUri");

	private static SystemPreferencesDTO[] sysPrefsList;
	
    public static void fetchAllSourceSystems()
    {
        //restTemplate = new RestTemplate();
    	log.info("Fetching All Source Systems");

    	sourceSystemsList = restTemplate.getForObject("http://" + jobsPortalBaseUrl + "/api/source-systems/getAllSourceSystems", SourceSystemDTO[].class);
    }
    
    public static SourceSystemDTO getSourceSystemDetails(String name)
    {
    	if (sourceSystemsList == null) fetchAllSourceSystems();
    	
    	for (SourceSystemDTO ssys: sourceSystemsList)
    	{
    		if (ssys.getName().trim().equalsIgnoreCase(name)) return ssys;
    		//log.info("Returning existing ID for specialty: " + spec.getName() + ", ID is " + spec.getId());
    	}
    	
    	return null;
    }
    
    public static SystemPreferencesDTO getSystemPreferences(String name)
    {
    	if (sysPrefsList == null) fetchAllSystemPreferences();
    	
    	for (SystemPreferencesDTO sysPrefs: sysPrefsList)
    	{
    		if (sysPrefs.getName().trim().equalsIgnoreCase(name)) return sysPrefs;
    		//log.info("Returning existing ID for specialty: " + spec.getName() + ", ID is " + spec.getId());
    	}
    	
    	return null;
    }
    
        
    private static void fetchAllSystemPreferences() {

    	log.info("Fetching All System Preferences");

    	sysPrefsList = restTemplate.getForObject("http://" + jobsPortalBaseUrl + "/api/system-preferences", SystemPreferencesDTO[].class);
	}

    public static BatchJobMetricsDTO insertRecordIntoBatchJobMetrics(BatchJobMetricsDTO batchJobMetricsDto) {
    	log.info("Inserting Record into Batch Job Metrics");
    	BatchJobMetricsDTO respDto = restTemplate.postForObject("http://" + jobsPortalBaseUrl + "/api/batch-job-metrics", batchJobMetricsDto, BatchJobMetricsDTO.class);
    	
    	return respDto;
    }
    
    public static BatchJobMetricsDTO updateRecordIntoBatchJobMetrics(BatchJobMetricsDTO batchJobMetricsDto) {
    	log.info("Inserting Record into Batch Job Metrics");
    	HttpEntity<BatchJobMetricsDTO> entity = new HttpEntity<BatchJobMetricsDTO>(batchJobMetricsDto);
    	ResponseEntity<BatchJobMetricsDTO> respDto = restTemplate.exchange("http://" + jobsPortalBaseUrl + "/api/batch-job-metrics", HttpMethod.PUT, entity, BatchJobMetricsDTO.class);
    	return respDto.getBody();
    }

	public static FileUploadResponseDTO uploadFile(String fileFullPath, String fileName, Long sourceSysId)
	{
		System.out.println("File Full Path: " + fileFullPath);
		System.out.println("File Name: " + fileName);
		System.out.println("SourceSysId: " + sourceSysId);

		Path path = Paths.get(fileFullPath);
		String name = fileName;
		String originalFileName = fileName;
		
		String contentType = null;
		
		if (fileName.toLowerCase().trim().endsWith(".xlsx")) contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		else if (fileName.toLowerCase().trim().endsWith(".xls")) contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		else 
		{
			if (fileName.toLowerCase().trim().endsWith(".csv")) contentType = "text/csv";
			else contentType = "text/plain";
		}
		
		byte[] content = null;
		
		try {
		    content = Files.readAllBytes(path);
		}
		catch (final IOException e) {
			log.error("IO Exception occured while reading bytes from the uploaded File. Exception Details: " , e);
		}
		
		//MultipartFile mFile = new MockMultipartFile(name, originalFileName, contentType, content);

		/*
		AwsBucketFileExchanger buckEx = new AwsBucketFileExchanger();
		buckEx.putFileInBucket(awsS3bucketName, name, mFile);
		//buckEx.getFileFromBucket(awsS3bucketName, name);
		 */
		
		//public ResponseEntity<FileUploadResponseDTO> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName, @RequestParam("sourceSystemId") Long sourceSysId) {

		  HttpHeaders headers = new HttpHeaders();
		  //headers.set("Content-Type", "application/x-www-form-urlencoded");      
		  //headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		  
		  MultiValueMap<String, Object> map= new LinkedMultiValueMap<String, Object>();
		  map.add("file", new FileSystemResource(fileFullPath));
		  map.add("fileName", originalFileName);
		  map.add("sourceSystemId", sourceSysId.toString()); //Orange@95
		  
		  HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(map, headers);

		  FileUploadResponseDTO respDto = restTemplate.postForObject("http://" + jobsPortalBaseUrl + "/api/jobs/fileUpload", request, FileUploadResponseDTO.class);
		  return respDto;

	}
    
    public static ListenableFuture<ResponseEntity<String>> processUploadedFile(JobsUploadDTO jobsUploadDto) throws RestClientException
    {
    	// StateDTO result = restTemplate.postForObject("http://geniejobportal-nadipelly.boxfuse.io:8080/api/states", newState, StateDTO.class);

    	System.out.println("sending rest api call to process upload file: " + jobsUploadDto.toString() + "@ " + ZonedDateTime.now());
    	log.info("sending rest api call to process upload file: " + jobsUploadDto.toString() + "@ " + ZonedDateTime.now());

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
    	
    	HttpEntity entity = new HttpEntity(jobsUploadDto, headers);
    	    	
    	ListenableFuture<ResponseEntity<String>> respDto = asyncTempl.postForEntity("http://" + jobsPortalBaseUrl + "/api/jobs/processUpload", entity, String.class);

    	System.out.println("reply received from rest api call to process upload file: " + respDto.toString() + "@ " + ZonedDateTime.now());
    	log.info("reply received from rest api call to process upload file: " + respDto.toString() + "@ " + ZonedDateTime.now());

    	return respDto;
    }
    
    public static void main(String args[])
    {
  		BatchJobMetricsDTO batchMetricsDto = new BatchJobMetricsDTO();

  		batchMetricsDto.setJobFileProcessedName("Hello.World.csv");
  		batchMetricsDto.setJobFileProcessingStatus("Error: Job Failed.");
  		batchMetricsDto.setJobsFileProcessTriggerDateTime(ZonedDateTime.now().toString());
  		batchMetricsDto.setSourceSystemId(4L);
  		batchMetricsDto.setSourceSystemName("Medefis");
  		batchMetricsDto.setLastModifiedDate(ZonedDateTime.now().toInstant().toString());
  		RestApiUtilities.insertRecordIntoBatchJobMetrics(batchMetricsDto);
    }

/*	public static String checkFileProcessingStatus(JobsUploadDTO jobsUploadDto) {
		// TODO Auto-generated method stub
    	System.out.println("sending rest api call to check file processing status: " + jobsUploadDto.toString() + "@ " + ZonedDateTime.now());
    	log.info("sending rest api call to check file processing status: " + jobsUploadDto.toString() + "@ " + ZonedDateTime.now());

    	String fileProcessingStatus = restTemplate.getForObject("http://" + jobsPortalBaseUrl + "/api/jobs/checkFileProcessingStatus", String.class);
    	System.out.println("reply received from rest api call to check file processing status: " + fileProcessingStatus + "@ " + ZonedDateTime.now());
    	log.info("reply received from rest api call to check file processing status: " + fileProcessingStatus + "@ " + ZonedDateTime.now());

		return fileProcessingStatus;
	}*/
}
