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
* Copyright (C) The Minnesota Department of Health.  All Rights Reserved.
*/
package us.mn.state.health.lims.typeoftestresult.valueholder;

import org.apache.commons.validator.GenericValidator;
import us.mn.state.health.lims.common.valueholder.BaseObject;

public class TypeOfTestResult extends BaseObject {
    public enum ResultType{
        REMARK( "R"),
        DICTIONARY( "D"),
        TITER( "T"),
        NUMERIC( "N" ),
        ALPHA( "A"),
        MULTISELECT( "M"),
        CASCADING_MULTISELECT( "C");

        String DBValue;

        private ResultType(String dbValue){
            DBValue = dbValue;
        }

        public String getDBValue(){ return DBValue;}
        public boolean matches( String type){ return DBValue.equals( type );}
        public static boolean isDictionaryVariant( String type ){ return !GenericValidator.isBlankOrNull( type ) && "DMC".contains( type );}
        public static boolean isMultiSelectVariant( String type ){ return !GenericValidator.isBlankOrNull( type ) && "MC".contains( type );}
    }


    private String id;

	private String description;

	private String testResultType;

	private String hl7Value;

	public String getHl7Value() {
		return hl7Value;
	}

	public void setHl7Value(String hl7Value) {
		this.hl7Value = hl7Value;
	}

	public TypeOfTestResult() {
		super();
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTestResultType() {
		return testResultType;
	}

	public void setTestResultType(String testResultType) {
		this.testResultType = testResultType;
	}

}