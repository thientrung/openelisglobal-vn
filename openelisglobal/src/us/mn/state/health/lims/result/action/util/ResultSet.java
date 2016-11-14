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
package us.mn.state.health.lims.result.action.util;

import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.result.valueholder.Result;
import us.mn.state.health.lims.result.valueholder.ResultInventory;
import us.mn.state.health.lims.result.valueholder.ResultSignature;
import us.mn.state.health.lims.sample.valueholder.Sample;

import java.util.List;
import java.util.Map;

public class ResultSet {
	public final Result result;
	public final ResultSignature signature;
	public final ResultInventory testKit;
	public final Patient patient;
	public final Sample sample;
	public final Map<String,List<String>> triggersToSelectedReflexesMap;
	public final boolean alwaysInsertSignature;
    public final boolean multipleResultsForAnalysis;

	public ResultSet(Result result, ResultSignature signature, ResultInventory testKit, Patient patient, Sample sample,
                     Map<String,List<String>> triggersToSelectedReflexesMap, boolean multipleResultsForAnalysis) {
		this.result = result;
		this.signature = signature;
		this.testKit = testKit;
		this.patient = patient;
		this.sample = sample;
		this.triggersToSelectedReflexesMap = triggersToSelectedReflexesMap;
        this.multipleResultsForAnalysis = multipleResultsForAnalysis;
		alwaysInsertSignature = signature != null && signature.getId() == null;
	}
}

