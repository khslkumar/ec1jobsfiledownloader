package com.genie.job.portal.statstaff.xml.dto;

public class JobDTO {

	private String  jobId;
	private String  jobName;
	private String  jobPostedDate;
	private String  jobStatus;
	private String  affiliation;
	private String  modality;
	private String  subModality;
	private String  hotJob;
	private String  jobType;
	private String  contractLength;
	private String  startDate;
	private String  description;
	private String  facilityName;
	private String  facilityCity;
	private String  stateCode;
	private String  zipCode;
	private String  bedSize;
	private String  traumaLevel;
	private String  interviewDates;
	private String  positions;
	private String  submissions;
	private String  stateLicenseDetails;
	private String  shift;
	private String  rateType;
	private String  overtimeType;
	private String  holidayRate;
	private String  overtimeRate;
	private String  callBackRate;
	private String  callBackMinimum;
	private String  facilityRate;
	private String  facilityHourlyRate;
	private String  chargeRateDifferential;
	private String  shiftRateDifferential;
	private String  onCallRate;
	private String  onCallRateType;
	private String  fromRate;
	private String  toRate;
	private String  callBackFromRate;
	private String  callBackToRate;
	private String  patientContactHours;
	private String  guaranteedHoursByWeek;
	private String  guaranteedHoursByDay;
	private String  shiftOvertimeRateBase;
	private String  shiftOvertimeRateFromRate;
	private String  shiftOvertimeRateToRate;
	private String  shiftOvertimeRateExtended;
	private String  shiftOvertimeRateExtendedFromRate;
	private String  shiftOvertimeRateExtendedToRate;
	private String  overtimeBaseStartHour;
	private String  overtimeBaseEndHour;
	private String  agencyFee;
	private String  feeType;
	private String  completionBonus;
	private String  holidayBonus;
	private String  overCasesBonus;
	private String  relocationBonus;
	private String  retainerFeeBonus;
	private String  signOnBonus;
	private String  weekendBonus;
	private String  airfareExpense;
	private String  housingExpense;
	private String  otherExpense;
	private String  perDiemExpense;
	private String  rentalCarExpense;
	/**
	 * @return the jobId
	 */
	public String getJobId() {
		return jobId;
	}
	/**
	 * @param jobId the jobId to set
	 */
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}
	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	/**
	 * @return the jobPostedDate
	 */
	public String getJobPostedDate() {
		return jobPostedDate;
	}
	/**
	 * @param jobPostedDate the jobPostedDate to set
	 */
	public void setJobPostedDate(String jobPostedDate) {
		this.jobPostedDate = jobPostedDate;
	}
	/**
	 * @return the jobStatus
	 */
	public String getJobStatus() {
		return jobStatus;
	}
	/**
	 * @param jobStatus the jobStatus to set
	 */
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	/**
	 * @return the affiliation
	 */
	public String getAffiliation() {
		return affiliation;
	}
	/**
	 * @param affiliation the affiliation to set
	 */
	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}
	/**
	 * @return the modality
	 */
	public String getModality() {
		return modality;
	}
	/**
	 * @param modality the modality to set
	 */
	public void setModality(String modality) {
		this.modality = modality;
	}
	/**
	 * @return the subModality
	 */
	public String getSubModality() {
		return subModality;
	}
	/**
	 * @param subModality the subModality to set
	 */
	public void setSubModality(String subModality) {
		this.subModality = subModality;
	}
	/**
	 * @return the hotJob
	 */
	public String getHotJob() {
		return hotJob;
	}
	/**
	 * @param hotJob the hotJob to set
	 */
	public void setHotJob(String hotJob) {
		this.hotJob = hotJob;
	}
	/**
	 * @return the jobType
	 */
	public String getJobType() {
		return jobType;
	}
	/**
	 * @param jobType the jobType to set
	 */
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	/**
	 * @return the contractLength
	 */
	public String getContractLength() {
		return contractLength;
	}
	/**
	 * @param contractLength the contractLength to set
	 */
	public void setContractLength(String contractLength) {
		this.contractLength = contractLength;
	}
	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		//String val = description.replaceAll("^ | $|\\n ", "");
		String val = description == null ? null : description.replaceAll("\\t", " ").replaceAll("\\n", "; ");
		
		this.description = val == null ? null : (val.length() <= 200 ? val : val.substring(0, 200));
	}
	/**
	 * @return the facilityName
	 */
	public String getFacilityName() {
		return facilityName;
	}
	/**
	 * @param facilityName the facilityName to set
	 */
	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}
	/**
	 * @return the facilityCity
	 */
	public String getFacilityCity() {
		return facilityCity;
	}
	/**
	 * @param facilityCity the facilityCity to set
	 */
	public void setFacilityCity(String facilityCity) {
		this.facilityCity = facilityCity;
	}
	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		return stateCode;
	}
	/**
	 * @param stateCode the stateCode to set
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
	/**
	 * @return the zipCode
	 */
	public String getZipCode() {
		return zipCode;
	}
	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	/**
	 * @return the bedSize
	 */
	public String getBedSize() {
		return bedSize;
	}
	/**
	 * @param bedSize the bedSize to set
	 */
	public void setBedSize(String bedSize) {
		this.bedSize = bedSize;
	}
	/**
	 * @return the traumaLevel
	 */
	public String getTraumaLevel() {
		return traumaLevel;
	}
	/**
	 * @param traumaLevel the traumaLevel to set
	 */
	public void setTraumaLevel(String traumaLevel) {
		this.traumaLevel = traumaLevel;
	}
	/**
	 * @return the interviewDates
	 */
	public String getInterviewDates() {
		return interviewDates;
	}
	/**
	 * @param interviewDates the interviewDates to set
	 */
	public void setInterviewDates(String interviewDates) {
		this.interviewDates = interviewDates;
	}
	/**
	 * @return the positions
	 */
	public String getPositions() {
		return positions;
	}
	/**
	 * @param positions the positions to set
	 */
	public void setPositions(String positions) {
		this.positions = positions;
	}
	/**
	 * @return the submissions
	 */
	public String getSubmissions() {
		return submissions;
	}
	/**
	 * @param submissions the submissions to set
	 */
	public void setSubmissions(String submissions) {
		this.submissions = submissions;
	}
	/**
	 * @return the stateLicenseDetails
	 */
	public String getStateLicenseDetails() {
		return stateLicenseDetails;
	}
	/**
	 * @param stateLicenseDetails the stateLicenseDetails to set
	 */
	public void setStateLicenseDetails(String stateLicenseDetails) {
		this.stateLicenseDetails = stateLicenseDetails;
	}
	/**
	 * @return the shift
	 */
	public String getShift() {
		return shift;
	}
	/**
	 * @param shift the shift to set
	 */
	public void setShift(String shift) {
		
		//String val = description.replaceAll("^ | $|\\n ", "");
		String val = shift == null ? null : shift.replaceAll("\\t", " ").replaceAll("\\n", "; ");
		
		this.shift = val == null ? null : (val.length() <= 200 ? val : val.substring(0, 200));
	}
	/**
	 * @return the rentalCarExpense
	 */
	public String getRentalCarExpense() {
		return rentalCarExpense;
	}
	/**
	 * @param rentalCarExpense the rentalCarExpense to set
	 */
	public void setRentalCarExpense(String rentalCarExpense) {
		this.rentalCarExpense = rentalCarExpense;
	}
	/**
	 * @return the perDiemExpense
	 */
	public String getPerDiemExpense() {
		return perDiemExpense;
	}
	/**
	 * @param perDiemExpense the perDiemExpense to set
	 */
	public void setPerDiemExpense(String perDiemExpense) {
		this.perDiemExpense = perDiemExpense;
	}
	/**
	 * @return the otherExpense
	 */
	public String getOtherExpense() {
		return otherExpense;
	}
	/**
	 * @param otherExpense the otherExpense to set
	 */
	public void setOtherExpense(String otherExpense) {
		this.otherExpense = otherExpense;
	}
	/**
	 * @return the housingExpense
	 */
	public String getHousingExpense() {
		return housingExpense;
	}
	/**
	 * @param housingExpense the housingExpense to set
	 */
	public void setHousingExpense(String housingExpense) {
		this.housingExpense = housingExpense;
	}
	/**
	 * @return the airfareExpense
	 */
	public String getAirfareExpense() {
		return airfareExpense;
	}
	/**
	 * @param airfareExpense the airfareExpense to set
	 */
	public void setAirfareExpense(String airfareExpense) {
		this.airfareExpense = airfareExpense;
	}
	/**
	 * @return the weekendBonus
	 */
	public String getWeekendBonus() {
		return weekendBonus;
	}
	/**
	 * @param weekendBonus the weekendBonus to set
	 */
	public void setWeekendBonus(String weekendBonus) {
		this.weekendBonus = weekendBonus;
	}
	/**
	 * @return the signOnBonus
	 */
	public String getSignOnBonus() {
		return signOnBonus;
	}
	/**
	 * @param signOnBonus the signOnBonus to set
	 */
	public void setSignOnBonus(String signOnBonus) {
		this.signOnBonus = signOnBonus;
	}
	/**
	 * @return the retainerFeeBonus
	 */
	public String getRetainerFeeBonus() {
		return retainerFeeBonus;
	}
	/**
	 * @param retainerFeeBonus the retainerFeeBonus to set
	 */
	public void setRetainerFeeBonus(String retainerFeeBonus) {
		this.retainerFeeBonus = retainerFeeBonus;
	}
	/**
	 * @return the relocationBonus
	 */
	public String getRelocationBonus() {
		return relocationBonus;
	}
	/**
	 * @param relocationBonus the relocationBonus to set
	 */
	public void setRelocationBonus(String relocationBonus) {
		this.relocationBonus = relocationBonus;
	}
	/**
	 * @return the overCasesBonus
	 */
	public String getOverCasesBonus() {
		return overCasesBonus;
	}
	/**
	 * @param overCasesBonus the overCasesBonus to set
	 */
	public void setOverCasesBonus(String overCasesBonus) {
		this.overCasesBonus = overCasesBonus;
	}
	/**
	 * @return the holidayBonus
	 */
	public String getHolidayBonus() {
		return holidayBonus;
	}
	/**
	 * @param holidayBonus the holidayBonus to set
	 */
	public void setHolidayBonus(String holidayBonus) {
		this.holidayBonus = holidayBonus;
	}
	/**
	 * @return the completionBonus
	 */
	public String getCompletionBonus() {
		return completionBonus;
	}
	/**
	 * @param completionBonus the completionBonus to set
	 */
	public void setCompletionBonus(String completionBonus) {
		this.completionBonus = completionBonus;
	}
	/**
	 * @return the feeType
	 */
	public String getFeeType() {
		return feeType;
	}
	/**
	 * @param feeType the feeType to set
	 */
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	/**
	 * @return the agencyFee
	 */
	public String getAgencyFee() {
		return agencyFee;
	}
	/**
	 * @param agencyFee the agencyFee to set
	 */
	public void setAgencyFee(String agencyFee) {
		this.agencyFee = agencyFee;
	}
	/**
	 * @return the overtimeBaseEndHour
	 */
	public String getOvertimeBaseEndHour() {
		return overtimeBaseEndHour;
	}
	/**
	 * @param overtimeBaseEndHour the overtimeBaseEndHour to set
	 */
	public void setOvertimeBaseEndHour(String overtimeBaseEndHour) {
		this.overtimeBaseEndHour = overtimeBaseEndHour;
	}
	/**
	 * @return the overtimeBaseStartHour
	 */
	public String getOvertimeBaseStartHour() {
		return overtimeBaseStartHour;
	}
	/**
	 * @param overtimeBaseStartHour the overtimeBaseStartHour to set
	 */
	public void setOvertimeBaseStartHour(String overtimeBaseStartHour) {
		this.overtimeBaseStartHour = overtimeBaseStartHour;
	}
	/**
	 * @return the shiftOvertimeRateExtendedToRate
	 */
	public String getShiftOvertimeRateExtendedToRate() {
		return shiftOvertimeRateExtendedToRate;
	}
	/**
	 * @param shiftOvertimeRateExtendedToRate the shiftOvertimeRateExtendedToRate to set
	 */
	public void setShiftOvertimeRateExtendedToRate(String shiftOvertimeRateExtendedToRate) {
		this.shiftOvertimeRateExtendedToRate = shiftOvertimeRateExtendedToRate;
	}
	/**
	 * @return the shiftOvertimeRateExtendedFromRate
	 */
	public String getShiftOvertimeRateExtendedFromRate() {
		return shiftOvertimeRateExtendedFromRate;
	}
	/**
	 * @param shiftOvertimeRateExtendedFromRate the shiftOvertimeRateExtendedFromRate to set
	 */
	public void setShiftOvertimeRateExtendedFromRate(String shiftOvertimeRateExtendedFromRate) {
		this.shiftOvertimeRateExtendedFromRate = shiftOvertimeRateExtendedFromRate;
	}
	/**
	 * @return the shiftOvertimeRateExtended
	 */
	public String getShiftOvertimeRateExtended() {
		return shiftOvertimeRateExtended;
	}
	/**
	 * @param shiftOvertimeRateExtended the shiftOvertimeRateExtended to set
	 */
	public void setShiftOvertimeRateExtended(String shiftOvertimeRateExtended) {
		this.shiftOvertimeRateExtended = shiftOvertimeRateExtended;
	}
	/**
	 * @return the shiftOvertimeRateToRate
	 */
	public String getShiftOvertimeRateToRate() {
		return shiftOvertimeRateToRate;
	}
	/**
	 * @param shiftOvertimeRateToRate the shiftOvertimeRateToRate to set
	 */
	public void setShiftOvertimeRateToRate(String shiftOvertimeRateToRate) {
		this.shiftOvertimeRateToRate = shiftOvertimeRateToRate;
	}
	/**
	 * @return the shiftOvertimeRateFromRate
	 */
	public String getShiftOvertimeRateFromRate() {
		return shiftOvertimeRateFromRate;
	}
	/**
	 * @param shiftOvertimeRateFromRate the shiftOvertimeRateFromRate to set
	 */
	public void setShiftOvertimeRateFromRate(String shiftOvertimeRateFromRate) {
		this.shiftOvertimeRateFromRate = shiftOvertimeRateFromRate;
	}
	/**
	 * @return the shiftOvertimeRateBase
	 */
	public String getShiftOvertimeRateBase() {
		return shiftOvertimeRateBase;
	}
	/**
	 * @param shiftOvertimeRateBase the shiftOvertimeRateBase to set
	 */
	public void setShiftOvertimeRateBase(String shiftOvertimeRateBase) {
		this.shiftOvertimeRateBase = shiftOvertimeRateBase;
	}
	/**
	 * @return the guaranteedHoursByDay
	 */
	public String getGuaranteedHoursByDay() {
		return guaranteedHoursByDay;
	}
	/**
	 * @param guaranteedHoursByDay the guaranteedHoursByDay to set
	 */
	public void setGuaranteedHoursByDay(String guaranteedHoursByDay) {
		this.guaranteedHoursByDay = guaranteedHoursByDay;
	}
	/**
	 * @return the guaranteedHoursByWeek
	 */
	public String getGuaranteedHoursByWeek() {
		return guaranteedHoursByWeek;
	}
	/**
	 * @param guaranteedHoursByWeek the guaranteedHoursByWeek to set
	 */
	public void setGuaranteedHoursByWeek(String guaranteedHoursByWeek) {
		this.guaranteedHoursByWeek = guaranteedHoursByWeek;
	}
	/**
	 * @return the patientContactHours
	 */
	public String getPatientContactHours() {
		return patientContactHours;
	}
	/**
	 * @param patientContactHours the patientContactHours to set
	 */
	public void setPatientContactHours(String patientContactHours) {
		this.patientContactHours = patientContactHours;
	}
	/**
	 * @return the callBackToRate
	 */
	public String getCallBackToRate() {
		return callBackToRate;
	}
	/**
	 * @param callBackToRate the callBackToRate to set
	 */
	public void setCallBackToRate(String callBackToRate) {
		this.callBackToRate = callBackToRate;
	}
	/**
	 * @return the callBackFromRate
	 */
	public String getCallBackFromRate() {
		return callBackFromRate;
	}
	/**
	 * @param callBackFromRate the callBackFromRate to set
	 */
	public void setCallBackFromRate(String callBackFromRate) {
		this.callBackFromRate = callBackFromRate;
	}
	/**
	 * @return the toRate
	 */
	public String getToRate() {
		return toRate;
	}
	/**
	 * @param toRate the toRate to set
	 */
	public void setToRate(String toRate) {
		this.toRate = toRate;
	}
	/**
	 * @return the fromRate
	 */
	public String getFromRate() {
		return fromRate;
	}
	/**
	 * @param fromRate the fromRate to set
	 */
	public void setFromRate(String fromRate) {
		this.fromRate = fromRate;
	}
	/**
	 * @return the onCallRateType
	 */
	public String getOnCallRateType() {
		return onCallRateType;
	}
	/**
	 * @param onCallRateType the onCallRateType to set
	 */
	public void setOnCallRateType(String onCallRateType) {
		this.onCallRateType = onCallRateType;
	}
	/**
	 * @return the onCallRate
	 */
	public String getOnCallRate() {
		return onCallRate;
	}
	/**
	 * @param onCallRate the onCallRate to set
	 */
	public void setOnCallRate(String onCallRate) {
		this.onCallRate = onCallRate;
	}
	/**
	 * @return the shiftRateDifferential
	 */
	public String getShiftRateDifferential() {
		return shiftRateDifferential;
	}
	/**
	 * @param shiftRateDifferential the shiftRateDifferential to set
	 */
	public void setShiftRateDifferential(String shiftRateDifferential) {
		this.shiftRateDifferential = shiftRateDifferential;
	}
	/**
	 * @return the chargeRateDifferential
	 */
	public String getChargeRateDifferential() {
		return chargeRateDifferential;
	}
	/**
	 * @param chargeRateDifferential the chargeRateDifferential to set
	 */
	public void setChargeRateDifferential(String chargeRateDifferential) {
		this.chargeRateDifferential = chargeRateDifferential;
	}
	/**
	 * @return the facilityHourlyRate
	 */
	public String getFacilityHourlyRate() {
		return facilityHourlyRate;
	}
	/**
	 * @param facilityHourlyRate the facilityHourlyRate to set
	 */
	public void setFacilityHourlyRate(String facilityHourlyRate) {
		this.facilityHourlyRate = facilityHourlyRate;
	}
	/**
	 * @return the facilityRate
	 */
	public String getFacilityRate() {
		return facilityRate;
	}
	/**
	 * @param facilityRate the facilityRate to set
	 */
	public void setFacilityRate(String facilityRate) {
		this.facilityRate = facilityRate;
	}
	/**
	 * @return the callBackMinimum
	 */
	public String getCallBackMinimum() {
		return callBackMinimum;
	}
	/**
	 * @param callBackMinimum the callBackMinimum to set
	 */
	public void setCallBackMinimum(String callBackMinimum) {
		this.callBackMinimum = callBackMinimum;
	}
	/**
	 * @return the callBackRate
	 */
	public String getCallBackRate() {
		return callBackRate;
	}
	/**
	 * @param callBackRate the callBackRate to set
	 */
	public void setCallBackRate(String callBackRate) {
		this.callBackRate = callBackRate;
	}
	/**
	 * @return the overtimeRate
	 */
	public String getOvertimeRate() {
		return overtimeRate;
	}
	/**
	 * @param overtimeRate the overtimeRate to set
	 */
	public void setOvertimeRate(String overtimeRate) {
		this.overtimeRate = overtimeRate;
	}
	/**
	 * @return the holidayRate
	 */
	public String getHolidayRate() {
		return holidayRate;
	}
	/**
	 * @param holidayRate the holidayRate to set
	 */
	public void setHolidayRate(String holidayRate) {
		this.holidayRate = holidayRate;
	}
	/**
	 * @return the overtimeType
	 */
	public String getOvertimeType() {
		return overtimeType;
	}
	/**
	 * @param overtimeType the overtimeType to set
	 */
	public void setOvertimeType(String overtimeType) {
		this.overtimeType = overtimeType;
	}
	/**
	 * @return the rateType
	 */
	public String getRateType() {
		return rateType;
	}
	/**
	 * @param rateType the rateType to set
	 */
	public void setRateType(String rateType) {
		this.rateType = rateType;
	}
	
	public String getHeader()
	{
		return "FacilityCity, StateCode, Modality, SubModality, Shift, StartDate, JobPostedDate, Description, FacilityRate, FacilityName, JobId, Status";

	}
	
	public String toString() {
		String retval = "";
		//Header: FacilityCity, StateCode, Modality, SubModality, Shift, StartDate, JobPostedDate, Description, FacilityRate, FacilityName, JobId, Status
		retval += "\"" + getFacilityCity() + "\",";
		retval += "\"" + getStateCode() + "\",";
		retval += "\"" + getModality() + "\",";
		retval += "\"" + getSubModality() + "\",";
		retval += "\"" + getShift() + "\",";
		retval += "\"" + getStartDate() + "\",";
		retval += "\"" + getJobPostedDate() + "\",";
		retval += "\"" + getDescription() + "\",";
		retval += "\"" + getFacilityRate() + "\",";
		retval += "\"" + getFacilityName() + "\",";
		retval += "\"" + getJobId() + "\",";
		retval += "\"" + getJobStatus() + "\"";
		
		return retval;
	}
	
	/**public ServerJobDTO getServerJobDTO()
	{
		ServerJobDTO s = new ServerJobDTO();
		s.setCity(getFacilityCity());
		s.setStateName(getStateCode());
		s.setSpecialtyName(getModality());
		s.setProfession(getSubModality());
		s.setShift(getShift());
		s.setStartDate(getStartDate());
		s.setModifiedDate(getJobPostedDate());
		s.setJobDescription(getDescription());
		s.setRate(getFacilityRate());
		s.setFacility(getFacilityName());
		s.setSourceJobID(getJobId());
		s.setStatus(getJobStatus());
		
		return s;
	}*/

}
