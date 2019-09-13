package com.genie.job.portal;


import java.time.Instant;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the BatchJobMetrics entity.
 */
public class BatchJobMetricsDTO implements Serializable {

    private Long id;

    private String jobsFileCreateTriggerDateTime;
    
    private String jobsFileCreateCompletionDateTime;

    private String jobsFileCreationStatus;

    private String jobsFileCreatedName;

    private String jobsFileProcessTriggerDateTime;
    
    private String jobsFileProcessCompletionDateTime;

    private String jobFileProcessingStatus;

    private String jobFileProcessedName;

    private Long sourceSystemId;

    private String sourceSystemName;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private String lastModifiedDate;
	
	public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobsFileCreateTriggerDateTime() {
        return jobsFileCreateTriggerDateTime;
    }

    public void setJobsFileCreateTriggerDateTime(String jobsFileCreateTriggerDateTime) {
        this.jobsFileCreateTriggerDateTime = jobsFileCreateTriggerDateTime;
    }

    public String getJobsFileCreationStatus() {
        return jobsFileCreationStatus;
    }

    public void setJobsFileCreationStatus(String jobsFileCreationStatus) {
        this.jobsFileCreationStatus = jobsFileCreationStatus;
    }

    public String getJobsFileCreatedName() {
        return jobsFileCreatedName;
    }

    public void setJobsFileCreatedName(String jobsFileCreatedName) {
        this.jobsFileCreatedName = jobsFileCreatedName;
    }

    public String getJobsFileProcessTriggerDateTime() {
        return jobsFileProcessTriggerDateTime;
    }

    public void setJobsFileProcessTriggerDateTime(String jobsFileProcessTriggerDateTime) {
        this.jobsFileProcessTriggerDateTime = jobsFileProcessTriggerDateTime;
    }

    public String getJobFileProcessingStatus() {
        return jobFileProcessingStatus;
    }

    public void setJobFileProcessingStatus(String jobFileProcessingStatus) {
        this.jobFileProcessingStatus = jobFileProcessingStatus;
    }

    public String getJobFileProcessedName() {
        return jobFileProcessedName;
    }

    public void setJobFileProcessedName(String jobFileProcessedName) {
        this.jobFileProcessedName = jobFileProcessedName;
    }

    public Long getSourceSystemId() {
        return sourceSystemId;
    }

    public void setSourceSystemId(Long sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public String getSourceSystemName() {
        return sourceSystemName;
    }

    public void setSourceSystemName(String sourceSystemName) {
        this.sourceSystemName = sourceSystemName;
    }
    
    

    public String getJobsFileCreateCompletionDateTime() {
		return jobsFileCreateCompletionDateTime;
	}

	public void setJobsFileCreateCompletionDateTime(String jobsFileCreateCompletionDateTime) {
		this.jobsFileCreateCompletionDateTime = jobsFileCreateCompletionDateTime;
	}

	public String getJobsFileProcessCompletionDateTime() {
		return jobsFileProcessCompletionDateTime;
	}

	public void setJobsFileProcessCompletionDateTime(String jobsFileProcessCompletionDateTime) {
		this.jobsFileProcessCompletionDateTime = jobsFileProcessCompletionDateTime;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BatchJobMetricsDTO batchJobMetricsDTO = (BatchJobMetricsDTO) o;
        if(batchJobMetricsDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), batchJobMetricsDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
    
    
    @Override
    public String toString() {
        return "BatchJobMetricsDTO{" +
            "id=" + getId() +
            ", jobsFileCreateTriggerDateTime='" + getJobsFileCreateTriggerDateTime() + "'" +
            ", jobsFileCreateCompletionDateTime='" + getJobsFileCreateCompletionDateTime() + "'" +
            ", jobsFileCreationStatus='" + getJobsFileCreationStatus() + "'" +
            ", jobsFileCreatedName='" + getJobsFileCreatedName() + "'" +
            ", jobsFileProcessTriggerDateTime='" + getJobsFileProcessTriggerDateTime() + "'" +
            ", jobsFileProcessCompletionDateTime='" + getJobsFileProcessCompletionDateTime() + "'" +
            ", jobFileProcessingStatus='" + getJobFileProcessingStatus() + "'" +
            ", jobFileProcessedName='" + getJobFileProcessedName() + "'" +
            "}";
    }
}
