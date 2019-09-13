package com.genie.job.portal;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import com.genie.job.portal.medefis.rest.dto.GetBearerTokenRequestDTO;
public class Test {
	  private static RestTemplate restTemplate = new RestTemplate();

	  public static String getBearerToken() throws Exception 
	  {
		  String url = "http://geniejobportal-test-nadipelly.boxfuse.io/api/authenticate";
		  
          /*vm.credentials = {
                  username: null,
                  password: null,
                  rememberMe: true
              };*/
		  
		  GetBearerTokenRequestDTO getBearerTokenRequestDTO = new GetBearerTokenRequestDTO();

		  getBearerTokenRequestDTO.setUsername("genieadmin");
		  getBearerTokenRequestDTO.setPassword("admin104");
		  
		  HttpHeaders headers = new HttpHeaders();
		  //headers.set("Content-Type", "application/x-www-form-urlencoded");      
		  headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		  String getBearerTokenReplyDTO = restTemplate.postForObject(url, getBearerTokenRequestDTO, String.class);
		  
	      return getBearerTokenReplyDTO;
		 
	  }
	  
	  public static void getAllOpenJobs(String bearerToken) throws Exception 
	  {
		  //eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJnZW5pZWFkbWluIiwiYXV0aCI6IlJPTEVfQURNSU4sUk9MRV9VU0VSIiwiZXhwIjoxNTI3NDE4ODk5fQ.M-xhQZkawXJFBzONUe24EsmzD4tGxlqLDMmp-ndbAxJo8KKtrKTfmjXR2KdDd7UPoSnFj5maIfWg5DsMDwmQ0g
		  //String url = "http://geniejobportal-test-nadipelly.boxfuse.io/api/current-open-jobs" + bearerToken;

    	//JobPublicFieldsDTO[] jobsList = restTemplate.getForObject(url, JobPublicFieldsDTO[].class);

    	//System.out.println(jobsList[0].getCity());

	  	  //HttpHeaders headers = new HttpHeaders();
	  	  //headers.add("Authorization", "Bearer " + bearerToken);
	
	  	  //MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
	  	  //map.add("Candidate", "null");
	  	  //map.add("StateCode", "null");
	  	  
	  	  //HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		  //JobPublicFieldsDTO[] jobs = restTemplate.postForObject(url, null, JobPublicFieldsDTO[].class);
		  
		  RequestCallback requestCallback = request -> request.getHeaders()
			        .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));

			// Streams the response instead of loading it all in memory
			ResponseExtractor<Void> responseExtractor = response -> {
			    // Here I write the response to a file but do what you like
			    Path path = Paths.get("C:\\PVengala\\hello.txt");
			    Files.copy(response.getBody(), path);
			    return null;
			};
			restTemplate.execute(URI.create("http://geniejobportal-test-nadipelly.boxfuse.io/api/current-open-jobs"), HttpMethod.POST, requestCallback, responseExtractor);

	  }

		 /*public static void main(String[] args) 
		 {
			 String bearerToken = null;
			 
			 try
			 {
				 bearerToken = getBearerToken();
				 System.out.println(bearerToken);

				 bearerToken = bearerToken.substring(13, bearerToken.length() - 2);
				 System.out.println(bearerToken);
				 getAllOpenJobs(bearerToken);
			 }
			 catch (Exception ex)
			 {
				 ex.printStackTrace();
			 }
			 
			 ObjectMapper mapper = new ObjectMapper();
			 JobsUploadDTO obj = new JobsUploadDTO();
			 obj.setDateFormat("mm/dd/yyyy");
			 obj.setFileFullPathOnServer("c:\\praveen\\");
			 //Object to JSON in file
			 try {
				 String jsonInString = mapper.writeValueAsString(obj);
				 System.out.println(jsonInString);
				//mapper.writeValue(new File("c:\\file.json"), obj);
			} catch (Exception ex)
			 {
				ex.printStackTrace();
			}

			 //Object to JSON in String
			 String jsonInString = mapper.writeValueAsString(obj);
			  
		 }*/
	  
	  public static void main(String args[])
	  {
		  /*
		   * host: email-smtp.us-west-2.amazonaws.com
        port: 587
        username: AKIAJJLQLQOO2T573H4A
        password: AiWbi/6iW1UuMamWzzsGdh+4yMg+cfiyi3mGUXTiJapc
        protocol: smtp
        tls: true
		   * 
		   */
		  
		  MailService mailService = new MailService();
		  mailService.sendEmail("praveen.vengala@cloudrayinc.com", "Hello..How are you?", "Test Email.", false, false);
	  }

}
