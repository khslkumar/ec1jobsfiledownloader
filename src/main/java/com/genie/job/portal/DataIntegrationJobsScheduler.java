package com.genie.job.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("unused")
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = "com.genie.job.portal")
public class DataIntegrationJobsScheduler{
  private AtomicInteger counter = new AtomicInteger(0);
  /*private MedefisRestApiDataProcessor medifisDataProcessor;
  private AyaConnectDataProcessor ayaConnectDataProcessor;
  private StatStaffXmlDataProcessor statStaffXmlDataProcessor;
  private StafferLinkDataProcessor stafferLinkDataProcessor;
  private FocusOneDataProcessor focusOneDataProcessor;*/
  
  private MailService mailService = new MailService();
  
  private final Logger log = LoggerFactory.getLogger(DataIntegrationJobsScheduler.class);

  public DataIntegrationJobsScheduler()
  {
	  System.setProperty("log4j.configurationFile", "resources/log4j2.xml");
      System.setProperty("logFilename", "logs/geniebatchjobs.log");
  }

  @Bean
  public TaskScheduler taskScheduler() {
      final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
      scheduler.setPoolSize(20);

      return scheduler;
  }
  
  
  @Scheduled(cron = "0 0/15 * * * ?", zone = "EST")
  public void downloadMedifis5DataFile() {
	  
		  int jobId = counter.incrementAndGet();
	      log.info("Medifis5 - Download Data File, Job @ cron " + new Date() + ", jobId: " + jobId);
	      
	      MedefisRestApiDataProcessor medifisDataProcessor = new MedefisRestApiDataProcessor(mailService, new BatchJobMetricsDTO());
	      medifisDataProcessor.downloadFile();

  }
  
  @Scheduled(cron = "0 10/15 * * * ?", zone = "EST")
  public void downloadAyaConnectDataFile() {
	  	  int jobId = counter.incrementAndGet();
	      log.info("AyaConnect - Download Data File, Job @ cron " + new Date() + ", jobId: " + jobId);
	      AyaConnectDataProcessor ayaConnectDataProcessor = new AyaConnectDataProcessor(mailService, new BatchJobMetricsDTO());

		  ayaConnectDataProcessor.downloadFile();
 }
  
  @Scheduled(cron = "0 15/15 * * * ?", zone = "EST")
  public void downloadStatStaffDataFile() {
	  
		  int jobId = counter.incrementAndGet();
	      log.info("StatStaff - Download Data File, Job @ cron " + new Date() + ", jobId: " + jobId);
	      StatStaffXmlDataProcessor statStaffXmlDataProcessor = new StatStaffXmlDataProcessor(mailService, new BatchJobMetricsDTO());

	      statStaffXmlDataProcessor.downloadFile();
  }
  
  @Scheduled(cron = "0 10/15 * * * ?", zone = "EST")
  public void downloadStafferLinkDataFile() {
	 
		  int jobId = counter.incrementAndGet();
	      log.info("StatStaff - Download Data File, Job @ cron " + new Date() + ", jobId: " + jobId);
	      StafferLinkDataProcessor stafferLinkDataProcessor = new StafferLinkDataProcessor(mailService, new BatchJobMetricsDTO());

	      stafferLinkDataProcessor.downloadFile();
	    
  }
 
  public static void main(String args[])
  {
		try{
			SpringApplication.run(DataIntegrationJobsScheduler.class, args);
	    }
		catch (Exception ex)
		{
		  ex.printStackTrace();
		}
   }
}
