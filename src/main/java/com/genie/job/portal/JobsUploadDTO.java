package com.genie.job.portal;

import java.io.Serializable;
import java.util.List;

public class JobsUploadDTO implements Serializable {
	private String fileName;
	private String fileFullPathOnServer;
	private Long jobSource;
	private List<String> dbColumnNames;
	private List<Long> dbColumnsList;
	private List<Long> excelMappedHeadersList;
	private String removeClosedJobs;
	private String deleteAllJobsInDbFirst;
	private String dateFormat;
	
	public String getFileFullPathOnServer()
	{
		return fileFullPathOnServer;
	}
	
	public void setFileFullPathOnServer(String fileFullPath)
	{
		this.fileFullPathOnServer = fileFullPath;
	}
	
	public Long getJobSource()
	{
		return jobSource;
	}
	
	public void setJobSource(Long jobSource)
	{
		this.jobSource = jobSource;
	}
	
	public String getFileName()
	{
		return fileName;
	}
	
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public List<Long> getDbColumnsList()
	{
		return dbColumnsList;
	}
	
	public void setDbColumnsList(List<Long> dbColumnsList)
	{
		this.dbColumnsList = dbColumnsList;
	}
	
	public List<Long> getExcelMappedHeadersList()
	{
		return excelMappedHeadersList;
	}
	
	public void setExcelMappedHeadersList(List<Long> excelMappedHeadersList)
	{
		this.excelMappedHeadersList = excelMappedHeadersList;
	}
	
	public String getRemoveClosedJobs()
	{
		return removeClosedJobs;
	}
	
	public void setRemoveClosedJobs(String removeClosedJobs)
	{
		this.removeClosedJobs = removeClosedJobs;
	}
	
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	@Override
	public String toString()
	{
		return "JobsUploadDTO {" +
				"fileName=" + getFileName() +
	            ", dbColumnsList='" + getDbColumnsList() + "'" +
	            ", excelMappedHeadersList='" + getExcelMappedHeadersList() + "'" +
	            ", removeClosedJobs='" + getRemoveClosedJobs() + "'" +
	            ", deleteAllJobsInDbFirst='" + getDeleteAllJobsInDbFirst() + "'" +	            
	            ", dateFormat='" + getDateFormat() + "'" +
	            ", dbColumnNames='" + getDbColumnNames() + "'" +
	            ", jobSource='" + getJobSource() + "'" +
	            ", fileFullPathOnServer ='" + getFileFullPathOnServer() + "'"; 
	}

	public List<String> getDbColumnNames() {
		return dbColumnNames;
	}

	public void setDbColumnNames(List<String> dbColumnNames) {
		this.dbColumnNames = dbColumnNames;
	}

	public String getDeleteAllJobsInDbFirst() {
		if (deleteAllJobsInDbFirst == null || deleteAllJobsInDbFirst.trim().length() == 0)
			return "No";
		else		
			return deleteAllJobsInDbFirst;
	}

	public void setDeleteAllJobsInDbFirst(String deleteAllJobsInDbFirst) {
		this.deleteAllJobsInDbFirst = deleteAllJobsInDbFirst;
	}
}
