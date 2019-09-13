package com.genie.job.portal;
 import java.time.Instant;
import java.time.ZonedDateTime;
import java.io.Serializable;
import java.util.Objects;
 import org.apache.commons.lang3.StringUtils;
 /**
 * A DTO for the Job entity.
 */
public class JobDTO implements Serializable {
     private Long id;
     private String city;
     private String profession;
     private String shift;
     private String startDate;
     private String modifiedDate;
     private String jobDescription;
     private String rate;
     private String facility;
     private String jobSource;
    
    private String sourceJobID;
     private Long stateId;
     private String stateName;
     private Long specialtyId;
     private String specialtyName;
    
    private String status;
     private Boolean currentUserApplied;
    
    private String createdBy;
     private Instant createdDate;
     private String lastModifiedBy;
     private Instant lastModifiedDate;
	
	public String getCreatedBy() {
        return createdBy;
    }
     public Instant getCreatedDate() {
        return createdDate;
    }
     public String getLastModifiedBy() {
        return lastModifiedBy;
    }
     public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }
     public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    
    public Long getId() {
        return id;
    }
     public void setId(Long id) {
        this.id = id;
    }
     public String getCity() {
        return city;
    }
     public void setCity(String city) {
        this.city = city;
    }
     public String getProfession() {
        return profession;
    }
     public void setProfession(String profession) {
        this.profession = profession;
    }
     public String getShift() {
        return shift;
    }
     public void setShift(String shift) {
        this.shift = shift;
    }
     public String getStartDate() {
        return startDate;
    }
     public void setStartDate(String startDate1) {
    	
    	String startDate = null;
    	startDate = (startDate1.length() > 8) && startDate1.contains("-") ? startDate1.split("-")[0] : startDate1;
		//startDate = (startDate1.length() > 8) ? startDate1.substring(0,startDate1.length()-8) : startDate1;
		String LeftPaddedString = StringUtils.leftPad(startDate.trim(),8,'0');
        this.startDate = LeftPaddedString;
    }
     public String getModifiedDate() {
        return modifiedDate;
    }
     public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }
     public String getJobDescription() {
        return jobDescription;
    }
     public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
     public String getRate() {
        return rate;
    }
     public void setRate(String rate) {
        this.rate = rate;
    }
     public String getFacility() {
        return facility;
    }
     public void setFacility(String facility) {
        this.facility = facility;
    }
     public String getJobSource() {
        return jobSource;
    }
     public void setJobSource(String jobSource) {
        this.jobSource = jobSource;
    }
     public String getSourceJobID() {
        return sourceJobID;
    }
     public void setSourceJobID(String sourceJobID) {
        this.sourceJobID = sourceJobID;
    }
     public Long getStateId() {
        return stateId;
    }
     public void setStateId(Long stateId) {
        this.stateId = stateId;
    }
     public String getStateName() {
        return stateName;
    }
     public void setStateName(String stateName) {
        this.stateName = stateName;
    }
     public Long getSpecialtyId() {
        return specialtyId;
    }
     public void setSpecialtyId(Long specialtyId) {
        this.specialtyId = specialtyId;
    }
     public String getSpecialtyName() {
        return specialtyName;
    }
     public void setSpecialtyName(String specialtyName) {
        this.specialtyName = specialtyName;
    }
     @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
         JobDTO jobDTO = (JobDTO) o;
        if(jobDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), jobDTO.getId());
    }
     @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
     @Override
    public String toString() {
        return "JobDTO{" +
            "id=" + getId() +
            ", city='" + getCity() + "'" +
            ", profession='" + getProfession() + "'" +
            ", specialty='" + getSpecialtyName() + "'" +
            ", shift='" + getShift() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", modifiedDate='" + getModifiedDate() + "'" +
            ", jobDescription='" + getJobDescription() + "'" +
            ", rate='" + getRate() + "'" +
            ", facility='" + getFacility() + "'" +
            ", jobSource='" + getJobSource() + "'" +
            ", sourceJobID='" + getSourceJobID() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
    
    public String toStringForFile() {
		return "\"" + getCity() + "\"," +  "\"" + getStateName() + "\"," + "\"" + getProfession() + "\"," + "\"" + "" + "\"," +
				"\"" + getShift() + "\"," +  "\"" + getStartDate() + "\"," + "\"" + "" + "\"," + "\"" + "" + "\"," +
				"\"" + "" + "\"," + "\"" + getFacility() + "\"," + "\"" + getSourceJobID() + "\"," + "\"" + "" + "\",";
    }
 	public String getStatus() {
		return status;
	}
 	public void setStatus(String status) {
		this.status = status;
	}
 	public Boolean getCurrentUserApplied() {
		return currentUserApplied;
	}
 	public void setCurrentUserApplied(Boolean currentUserApplied) {
		this.currentUserApplied = currentUserApplied;
	}
}