/**
* The contents of this file are subject to the Mozilla Public License
* Version 1.1 (the "License"); you may not use this file except in
* compliance with the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
* License for the specific language governing rights and limitations under
* the License.
*
* The Original Code is OpenELIS code.
*
* Copyright (C) CIRG, University of Washington, Seattle WA.  All Rights Reserved.
*
*/
package us.mn.state.health.lims.reports.action.implementation.reportBeans;

import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.result.valueholder.Result;


public class ClinicalPatientData{

	private String patientName = "";
	private String nationalId;
	private String gender;
	private String dob;
	private String age;
	private String stNumber;
	private String subjectNumber;
	private String contactInfo;
	private String siteInfo;
	private String testName;
	private String testRefRange;
	private String result;
	private String note;
    private String conclusion;
	private String finishDate;
	private String accessionNumber;
	private String receivedDate;
    private String testDate;
    private String referralSentDate;
    private String referralTestName;
    private String referralResult;
    private String referralResultReportDate;
    private String referralReason;
    private String referralRefRange;
    private String referralNote;
    private String firstName = "";
    private String lastName = "" ;
    private String testSection;
    private String dept;
    private String commune;
    private String ward;
    private String district;
    private String healthDistrict = "";
    private String healthRegion = ""; 
    private int sectionSortOrder = 0;
    private int testSortOrder = 0;
    private boolean hasRangeAndUOM = false;
    private String labOrderType = "";
    private String uom;
    private String alerts;
    private String completeFlag;
    private String orderFinishDate;
    private String panelName;
    private Boolean separator = false;
    private Panel panel;
    private String orderDate;
    private String patientSiteNumber;
    private Boolean abnormalResult = Boolean.FALSE;
    private String collectionDateTime;
    private boolean correctedResult = false;
    private Result parentResult;
    private boolean parentMarker = false;
    private String billingNumber;
    private String sampleType;
    private String sampleId;
    private String analysisStatus;



    public ClinicalPatientData(){}
    public ClinicalPatientData( ClinicalPatientData data){
        patientName = data.getPatientName();
        nationalId  = data.getNationalId();
        gender = data.getGender();
        dob = data.getDob();
        age = data.getAge();
        stNumber = data.getStNumber();
        subjectNumber = data.getSubjectNumber();
        contactInfo = data.getContactInfo();
        siteInfo = data.getSiteInfo();
        testName = data.getTestName();
        testRefRange = data.getTestRefRange();
        result = data.getResult();
        note = data.getNote();
        conclusion = data.getConclusion();
        finishDate = data.getFinishDate();
        accessionNumber = data.getAccessionNumber();
        receivedDate = data.getReceivedDate();
        testDate = data.getTestDate();
        referralSentDate = data.getReferralSentDate();
        referralTestName = data.getReferralTestName();
        referralResult = data.getReferralResult();
        referralResultReportDate = data.getReferralResultReportDate();
        referralReason = data.getReferralReason();
        referralRefRange = data.getReferralRefRange();
        referralNote = data.getReferralNote();
        firstName = data.getFirstName();
        lastName = data.getLastName();
        testSection = data.getTestSection();
        dept = data.getDept();
        commune = data.getCommune();
        ward = data.getWard();
        district = data.getDistrict();
        healthDistrict = data.getHealthDistrict();
        healthRegion = data.getHealthRegion();
        sectionSortOrder = data.getSectionSortOrder();
        testSortOrder = data.getTestSortOrder();
        hasRangeAndUOM = data.getAbnormalResult();
        labOrderType = data.getLabOrderType();
        uom = data.getUom();
        alerts = data.getAlerts();
        completeFlag = data.getCompleteFlag();
        orderFinishDate = data.getOrderFinishDate();
        panelName = data.getPanelName();
        separator = data.getSeparator();
        panel = data.getPanel();
        orderDate = data.getOrderDate();
        patientSiteNumber = data.getPatientSiteNumber();
        abnormalResult = data.getAbnormalResult();
        collectionDateTime = data.getCollectionDateTime();
        correctedResult = data.isCorrectedResult();
        parentResult = data.getParentResult();
        parentMarker = data.getParentMarker();
        billingNumber = data.getBillingNumber();
        sampleType = data.getSampleType();
        sampleId = data.getSampleId();
        analysisStatus = data.getAnalysisStatus();
    }

    public String getReferralRefRange() {
        return referralRefRange;
    }

    public void setReferralRefRange(String referralRefRange) {
        this.referralRefRange = referralRefRange;
    }


	public String getTestRefRange() {
		return testRefRange;
	}

	public void setTestRefRange(String testRefRange) {
		this.testRefRange = testRefRange;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getNationalId() {
		return nationalId;
	}

	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getStNumber() {
		return stNumber;
	}

	public void setStNumber(String stNumber) {
		this.stNumber = stNumber;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getTestName() {
		return testName;
	}

	public void setReceivedDate(String recievedDate) {
		this.receivedDate = recievedDate;
	}

	// in case of typo
	public String getRecievedDate() {
	    return getReceivedDate();
	}

	public String getReceivedDate() {
		return receivedDate;
	}

	public void setConclusion(String conclusioned) {
		conclusion = conclusioned;
	}

	public String getConclusion() {
		return conclusion;
	}

	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	public String getContactInfo() {
		return contactInfo;
	}

	public void setSiteInfo(String siteInfo) {
		this.siteInfo = siteInfo;
	}

	public String getSiteInfo() {
		return siteInfo;
	}

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public String getTestDate() {
        return testDate;
    }

    public String getReferralSentDate() {
        return referralSentDate;
    }

    public void setReferralSentDate(String referralSentDate) {
        this.referralSentDate = referralSentDate;
    }

    public String getReferralTestName() {
        return referralTestName;
    }

    public void setReferralTestName(String referralTestName) {
        this.referralTestName = referralTestName;
    }

    public String getReferralResult() {
        return referralResult;
    }

    public void setReferralResult(String referralResult) {
        this.referralResult = referralResult;
    }

    public void setReferralResultReportDate(String referralResultReportDate) {
        this.referralResultReportDate = referralResultReportDate;
    }

    public String getReferralResultReportDate() {
        return referralResultReportDate;
    }

    public void setReferralReason(String referralReason) {
        this.referralReason = referralReason;
    }

    public String getReferralReason() {
        return referralReason;
    }

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getUom() {
		return uom;
	}
	
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
    
    public String getReferralNote() {
        return referralNote;
    }

    public void setReferralNote(String referralNote) {
        this.referralNote = referralNote;
    }

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getTestSection() {
		return testSection;
	}

	public void setTestSection(String testSection) {
		this.testSection = testSection;
	}

	public boolean isHasRangeAndUOM() {
		return hasRangeAndUOM;
	}

	public void setHasRangeAndUOM(boolean hasRangeAndUOM) {
		this.hasRangeAndUOM = hasRangeAndUOM;
	}

	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public String getCommune() {
		return commune;
	}

	public void setCommune(String commune) {
		this.commune = commune;
	}

	public String getWard() {
		return ward;
	}

	public void setWard(String ward) {
		this.ward = ward;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}


	public int getTestSortOrder() {
		return testSortOrder;
	}

	public void setTestSortOrder(int testSortOrder) {
		this.testSortOrder = testSortOrder;
	}

	public int getSectionSortOrder() {
		return sectionSortOrder;
	}

	public void setSectionSortOrder(int sectionSortOrder) {
		this.sectionSortOrder = sectionSortOrder;
	}

	public String getSubjectNumber() {
		return subjectNumber;
	}

	public void setSubjectNumber(String subjectNumber) {
		this.subjectNumber = subjectNumber;
	}

	public String getHealthDistrict() {
		return healthDistrict;
	}

	public void setHealthDistrict(String healthDistrict) {
		this.healthDistrict = healthDistrict;
	}

	public String getHealthRegion() {
		return healthRegion;
	}

	public void setHealthRegion(String healthRegion) {
		this.healthRegion = healthRegion;
	}

	public String getLabOrderType() {
		return labOrderType;
	}

	public void setLabOrderType(String labOrderType) {
		this.labOrderType = labOrderType;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getCompleteFlag() {
		return completeFlag;
	}

	public void setCompleteFlag(String completeFlag) {
		this.completeFlag = completeFlag;
	}

	public String getAlerts() {
		return alerts;
	}

	public void setAlerts(String alerts) {
		this.alerts = alerts;
	}

	public String getPanelName() {
		return panelName;
	}

	public void setPanelName(String panelName) {
		this.panelName = panelName;
	}

	public Boolean getSeparator() {
		return separator;
	}

	public void setSeparator(Boolean separator) {
		this.separator = separator;
	}

	public Panel getPanel() {
		return panel;
	}

	public void setPanel(Panel panel) {
		this.panel = panel;
	}

	public String getOrderFinishDate() {
		return orderFinishDate;
	}

	public void setOrderFinishDate(String orderFinishDate) {
		this.orderFinishDate = orderFinishDate;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getPatientSiteNumber() {
		return patientSiteNumber;
	}

	public void setPatientSiteNumber(String patientSiteNumber) {
		this.patientSiteNumber = patientSiteNumber;
	}

	public Boolean getAbnormalResult(){
		return abnormalResult;
	}

	public void setAbnormalResult(Boolean abnormalResult){
		this.abnormalResult = abnormalResult;
	}

	public String getCollectionDateTime(){
		return collectionDateTime;
	}

	public void setCollectionDateTime(String collectionDateTime){
		this.collectionDateTime = collectionDateTime;
	}

    public boolean isCorrectedResult(){
        return correctedResult;
    }

    public void setCorrectedResult( boolean correctedResult ){
        this.correctedResult = correctedResult;
    }

    public Result getParentResult(){
        return parentResult;
    }

    public void setParentResult( Result parentResult ){
        this.parentResult = parentResult;
    }

    public boolean getParentMarker(){
        return parentMarker;
    }

    public void setParentMarker( boolean isParentMarker ){
        this.parentMarker = isParentMarker;
    }

    public String getBillingNumber(){
        return billingNumber;
    }

    public void setBillingNumber( String billingNumber ){
        this.billingNumber = billingNumber;
    }

    public String getSampleType(){
        return sampleType;
    }

    public void setSampleType( String sampleType ){
        this.sampleType = sampleType;
    }

    public String getSampleId(){
        return sampleId;
    }

    public void setSampleId( String sampleId ){
        this.sampleId = sampleId;
    }

    public String getAnalysisStatus(){
        return analysisStatus;
    }

    public void setAnalysisStatus( String analysisStatus ){
        this.analysisStatus = analysisStatus;
    }
}
