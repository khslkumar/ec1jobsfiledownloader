package com.genie.job.portal;

import java.io.Serializable;
import java.util.List;

public class FileUploadResponseDTO implements Serializable{
	private List<String> excelColHeaders;
	private String fileFullPathOnServer;
	private List<Long> dbColsMap;
	private List<Long> fileColsMap;
	private boolean isError=false;
	private String errorMessage=null;
	
	public List<String> getExcelColHeaders() {
		return excelColHeaders;
	}
	public void setExcelColHeaders(List<String> excelColHeaders) {
		this.excelColHeaders = excelColHeaders;
	}
	
	@Override
	public String toString()
	{
		return "FileUploadResponseDTO {" +
				"excel Column Headers=" + getExcelColHeaders() +
	            ", File Full Path On Server='" + getFileFullPathOnServer() + "'" +
				", Db Cols Map = '" + getDbColsMap() + "'" +
	            ", File Cols Map = '" + getFileColsMap() + "'" +
	            ", isError = '" + (isError() ? "true" : "false") + "'" +
				", Error Message = '" + getErrorMessage() + "'}"; 

	}
	public String getFileFullPathOnServer() {
		return fileFullPathOnServer;
	}
	public void setFileFullPathOnServer(String fileFullPathOnServer) {
		this.fileFullPathOnServer = fileFullPathOnServer;
	}
	/**
	 * @return the dbColsMap
	 */
	public List<Long> getDbColsMap() {
		return dbColsMap;
	}
	/**
	 * @param dbColsMap the dbColsMap to set
	 */
	public void setDbColsMap(List<Long> dbColsMap) {
		this.dbColsMap = dbColsMap;
	}
	/**
	 * @return the fileColsMap
	 */
	public List<Long> getFileColsMap() {
		return fileColsMap;
	}
	/**
	 * @param fileColsMap the fileColsMap to set
	 */
	public void setFileColsMap(List<Long> fileColsMap) {
		this.fileColsMap = fileColsMap;
	}
	public boolean isError() {
		return isError;
	}
	public void setError(boolean isError) {
		this.isError = isError;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
