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
*  
* Contributor(s): CIRG, University of Washington, Seattle WA.
*/
package us.mn.state.health.lims.note.valueholder;

import us.mn.state.health.lims.common.valueholder.BaseObject;
import us.mn.state.health.lims.referencetables.valueholder.ReferenceTables;
import us.mn.state.health.lims.systemuser.valueholder.SystemUser;

public class Note extends BaseObject {
    public static final String EXTERNAL = "E";
    public static final String INTERNAL = "I";
    public static final String REJECT_REASON = "R";
    public static final String NON_CONFORMITY = "N";

	private String id;

    private SystemUser systemUser;
    
    private String systemUserId;
    
	private String referenceId;
	
	private String referenceTableId;

	private String noteType;

	private String subject;

	private String text;

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getNoteType() {
		return noteType;
	}

	public void setNoteType(String noteType) {
		this.noteType = noteType;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getReferenceTableId() {
		return referenceTableId;
	}

	public void setReferenceTableId(String referenceTableId) {
		this.referenceTableId = referenceTableId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setSystemUser(SystemUser systemUser) {
		this.systemUser = systemUser;
	}
	
	public SystemUser getSystemUser() {
		return this.systemUser;
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSystemUserId() {
		return systemUserId;
	}

	public void setSystemUserId(String systemUserId) {
		this.systemUserId = systemUserId;
	}

	public void setReferenceTables(ReferenceTables referenceTables) {
		if( referenceTables != null){
			setReferenceTableId(referenceTables.getId());
		}
	}


}

