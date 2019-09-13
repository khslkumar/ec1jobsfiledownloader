package com.genie.job.portal;


import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the SourceSystem entity.
 */
public class SourceSystemDTO implements Serializable {

    private Long id;

    private String name;

    private String websiteURL;

    private String userName;

    private String password;

    private String recruiterEmail;

    private String accountManagerEmail;

    private Boolean runBatch;

    private Long batchRunTimeId;

    private String batchRunTimeName;

    private Long batchRunDaysId;

    private String batchRunDaysName;
    
    private String dbColsMap;
    
    private String fileColsMap;

    private String awsBucketName;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public void setRecruiterEmail(String recruiterEmail) {
        this.recruiterEmail = recruiterEmail;
    }

    public String getAccountManagerEmail() {
        return accountManagerEmail;
    }

    public void setAccountManagerEmail(String accountManagerEmail) {
        this.accountManagerEmail = accountManagerEmail;
    }

    public Boolean isRunBatch() {
        return runBatch;
    }

    public void setRunBatch(Boolean runBatch) {
        this.runBatch = runBatch;
    }

    public Long getBatchRunTimeId() {
        return batchRunTimeId;
    }

    public void setBatchRunTimeId(Long batchRunTimeId) {
        this.batchRunTimeId = batchRunTimeId;
    }

    public String getBatchRunTimeName() {
        return batchRunTimeName;
    }

    public void setBatchRunTimeName(String batchRunTimeName) {
        this.batchRunTimeName = batchRunTimeName;
    }

    public Long getBatchRunDaysId() {
        return batchRunDaysId;
    }

    public void setBatchRunDaysId(Long batchRunDaysId) {
        this.batchRunDaysId = batchRunDaysId;
    }

    public String getBatchRunDaysName() {
        return batchRunDaysName;
    }

    public void setBatchRunDaysName(String batchRunDaysName) {
        this.batchRunDaysName = batchRunDaysName;
    }

    /**
	 * @return the dbColsMap
	 */
	public String getDbColsMap() {
		return dbColsMap;
	}

	/**
	 * @param dbColsMap the dbColsMap to set
	 */
	public void setDbColsMap(String dbColsMap) {
		this.dbColsMap = dbColsMap;
	}

	/**
	 * @return the fileColsMap
	 */
	public String getFileColsMap() {
		return fileColsMap;
	}

	/**
	 * @param fileColsMap the fileColsMap to set
	 */
	public void setFileColsMap(String fileColsMap) {
		this.fileColsMap = fileColsMap;
	}

	public String getAwsBucketName() {
		return awsBucketName;
	}

	public void setAwsBucketName(String awsBucketName) {
		this.awsBucketName = awsBucketName;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SourceSystemDTO sourceSystemDTO = (SourceSystemDTO) o;
        if(sourceSystemDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), sourceSystemDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "SourceSystemDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", websiteURL='" + getWebsiteURL() + "'" +
            ", userName='" + getUserName() + "'" +
            ", password='" + getPassword() + "'" +
            ", recruiterEmail='" + getRecruiterEmail() + "'" +
            ", accountManagerEmail='" + getAccountManagerEmail() + "'" +
            ", runBatch='" + isRunBatch() + "'" +
            ", awsBucketName='" + getAwsBucketName() + "'" +
            "}";
    }
}
