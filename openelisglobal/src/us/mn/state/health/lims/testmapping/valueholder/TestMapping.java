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
                                                                                           
package us.mn.state.health.lims.testmapping.valueholder;

import java.sql.Timestamp;

import us.mn.state.health.lims.common.valueholder.BaseObject;

public class TestMapping extends BaseObject{
   
	private static final long serialVersionUID = 1L;
	
	private String id;
    private String externalTestId;
    private String externalTestName;
    private String internalTestId;
    private String internalTestName;
    private Timestamp lastUpdated;
    
    public TestMapping(){
    }
    
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
	public String getExternalTestId(){
		return externalTestId;
	}
	public void setExternalTestId(String externalTestId){
		this.externalTestId = externalTestId;
	}
	public String getExternalTestName(){
        return externalTestName;
    }
    public void setExternalTestName(String externalTestName){
        this.externalTestName = externalTestName;
    }
    public String getInternalTestId(){
        return internalTestId;
    }
    public void setInternalTestId(String internalTestId){
        this.internalTestId = internalTestId;
    }
    public String getInternalTestName(){
        return internalTestName;
    }
    public void setInternalTestName(String internalTestName){
        this.internalTestName = internalTestName;
    }
	public Timestamp getLastUpdated(){
		return lastUpdated;
	}
	public void setLastUpdated(Timestamp lastUpdated){
		this.lastUpdated = lastUpdated;
	}
	
}
