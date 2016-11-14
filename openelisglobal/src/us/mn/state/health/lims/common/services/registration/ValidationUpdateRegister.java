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
package us.mn.state.health.lims.common.services.registration;

import java.util.ArrayList;
import java.util.List;

import us.mn.state.health.lims.common.services.registration.interfaces.IResultUpdate;
import us.mn.state.health.lims.common.util.ConfigurationProperties;
import us.mn.state.health.lims.common.util.ConfigurationProperties.Property;
import us.mn.state.health.lims.dataexchange.resultreporting.ResultReportingUpdate;

public class ValidationUpdateRegister {
	static public List<IResultUpdate> getRegisteredUpdaters(){
		List<IResultUpdate> updaters = new ArrayList<IResultUpdate>();
		
		//kluge at this point, should be discoverable
		if( shouldReport(Property.reportResults)){
			updaters.add( new ResultReportingUpdate());
		}
		
		/***** BIG WARNING FLASHING LIGHT *********
		 * If another listener is added then the method addResultSets() needs to be modified in ResultValidationSaveAction
		 * 
		 * There is code that only looks at patient reports when deciding what is a new value and what is an old value
		 */
		return updaters;
	}
	
	static private boolean shouldReport(Property property) {
		String reportResults = ConfigurationProperties.getInstance().getPropertyValueLowerCase(property);
		return ( "true".equals(reportResults) || "enable".equals(reportResults) );
	}

}
