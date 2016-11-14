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
package us.mn.state.health.lims.common.formfields;

import java.util.HashMap;

import us.mn.state.health.lims.common.formfields.AdminFormFields.Field;

public class VN_APHLAdminFormFields implements IAdminFormFieldsForImplementation {

	public HashMap<AdminFormFields.Field, Boolean> getImplementationAttributes() {
		HashMap<AdminFormFields.Field, Boolean> settings = new HashMap<AdminFormFields.Field, Boolean>();

		settings.put(Field.OrganizationMenu,  Boolean.TRUE);
		settings.put(Field.SiteInformationMenu,  Boolean.TRUE);
		settings.put(Field.ResultInformationMenu,  Boolean.TRUE);
		settings.put(Field.SampleLabelMenu,  Boolean.TRUE);
		settings.put(Field.TestUsageAggregatation, Boolean.TRUE);
		settings.put(Field.RESULT_REPORTING_CONFIGURATION, Boolean.TRUE);
		settings.put(Field.DictionaryMenu, Boolean.TRUE);
		
		// Added by Mark 2016-08-12 09:41PM
		// Default Admin modules shown in masterpageLeft list
		settings.put(Field.PanelMenu,  Boolean.TRUE);
		settings.put(Field.PanelItemMenu,  Boolean.TRUE);
		settings.put(Field.SystemUserSectionMenu, Boolean.TRUE);
		settings.put(Field.SampleEntryMenu, Boolean.TRUE);
		settings.put( Field.PATIENT_ENTRY_CONFIGURATION, Boolean.TRUE );
		settings.put( Field.TEST_MANAGEMENT, Boolean.TRUE);
		settings.put(Field.PRINTED_REPORTS_CONFIGURATION, Boolean.TRUE);
		settings.put(Field.NON_CONFORMITY_CONFIGURATION, Boolean.TRUE);
		settings.put(Field.WORKPLAN_CONFIGURATION, Boolean.TRUE);
		settings.put(Field.ProjectMenu,  Boolean.TRUE);
		settings.put(Field.ProgramMenu,  Boolean.TRUE);
		settings.put(Field.CityMenu,  Boolean.TRUE);
		settings.put(Field.DistrictMenu,  Boolean.TRUE);

		return settings;
	}

}
