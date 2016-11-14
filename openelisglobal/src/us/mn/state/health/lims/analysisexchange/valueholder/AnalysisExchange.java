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
 * Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
                                                                                           
package us.mn.state.health.lims.analysisexchange.valueholder;

import java.sql.Timestamp;

import us.mn.state.health.lims.common.valueholder.BaseObject;

public class AnalysisExchange extends BaseObject{
   
	private static final long serialVersionUID = 1L;
	
	private String id;
    private String assignedCode;
    private String externalTestId;
    private String externalAnalysisId;  //not use for now
    private String internalAnalysisId;
    private String gotBy;
    private String exchangedBy;
    private String status;
	private String medicalNumber;
    private Timestamp getDate;
    private Timestamp exchangedDate;
    private Timestamp lastUpdated;
    
    public AnalysisExchange(){
    }
    
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
    public String getAssignedCode(){
        return assignedCode;
    }
    public void setAssignedCode(String assignedCode){
        this.assignedCode = assignedCode;
    }
	public String getExternalTestId(){
		return externalTestId;
	}
	public void setExternalTestId(String externalTestId){
		this.externalTestId = externalTestId;
	}
    public String getExternalAnalysisId(){
        return externalAnalysisId;
    }
    public void setExternalAnalysisId(String externalAnalysisId){
        this.externalAnalysisId = externalAnalysisId;
    }
    public String getInternalAnalysisId(){
        return internalAnalysisId;
    }
    public void setInternalAnalysisId(String internalAnalysisId){
        this.internalAnalysisId = internalAnalysisId;
    }
    public String getGotBy(){
        return gotBy;
    }
    public void setGotBy(String gotBy){
        this.gotBy = gotBy;
    }
    public String getExchangedBy(){
        return exchangedBy;
    }
    public void setExchangedBy(String exchangedBy){
        this.exchangedBy = exchangedBy;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getMedicalNumber() {
		return medicalNumber;
	}
	public void setMedicalNumber(String medicalNumber) {
		this.medicalNumber = medicalNumber;
	}
    public Timestamp getGetDate() {
        return getDate;
    }
    public void setGetDate(Timestamp getDate) {
        this.getDate = getDate;
    }
    public Timestamp getExchangedDate() {
        return exchangedDate;
    }
    public void setExchangedDate(Timestamp exchangedDate) {
        this.exchangedDate = exchangedDate;
    }
	public Timestamp getLastUpdated(){
		return lastUpdated;
	}
	public void setLastUpdated(Timestamp lastUpdated){
		this.lastUpdated = lastUpdated;
	}
	
}
