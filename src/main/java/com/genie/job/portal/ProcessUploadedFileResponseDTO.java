package com.genie.job.portal;

import java.io.Serializable;

public class ProcessUploadedFileResponseDTO implements Serializable {
	private boolean isError=false;
	private String errorMessage=null;
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
	
	@Override
	public String toString()
	{
		return "ProcessUploadedFileResponseDTO {" +
	            "isError = '" + (isError() ? "true" : "false") + "'" +
				", Error Message = '" + getErrorMessage() + "'}"; 

	}
}
