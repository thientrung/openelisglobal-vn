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
package us.mn.state.health.lims.common.util;

import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.log.LogEvent;
import us.mn.state.health.lims.siteinformation.dao.SiteInformationDAO;
import us.mn.state.health.lims.siteinformation.daoimpl.SiteInformationDAOImpl;
import us.mn.state.health.lims.siteinformation.valueholder.SiteInformation;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultConfigurationProperties extends ConfigurationProperties {

	private static String propertyFile = "/SystemConfiguration.properties";
	private java.util.Properties properties = null;
	protected static Map<ConfigurationProperties.Property, KeyDefaultPair> propertiesFileMap;
	protected static Map<String, ConfigurationProperties.Property> dbNamePropertiesMap;
	private boolean databaseLoaded = false;

	{
		//config from SystemConfiguration.properties
		propertiesFileMap  = new HashMap<ConfigurationProperties.Property, KeyDefaultPair>();
		propertiesFileMap.put(Property.AmbiguousDateValue, new KeyDefaultPair("date.ambiguous.date.value", "01") );
		propertiesFileMap.put(Property.AmbiguousDateHolder , new KeyDefaultPair("date.ambiguous.date.holder", "X") );
		propertiesFileMap.put(Property.ReferingLabParentOrg , new KeyDefaultPair("organization.reference.lab.parent", null) );
		propertiesFileMap.put(Property.resultsResendTime , new KeyDefaultPair("results.send.retry.time", "30") );
/*		propertiesFileMap.put(Property. , new KeyDefaultPair() );

	*/
		//config from site_information table
		dbNamePropertiesMap  = new HashMap<String, ConfigurationProperties.Property>();
		setDBPropertyMappingAndDefault(Property.SiteCode, "siteNumber", "" );
		setDBPropertyMappingAndDefault(Property.TrainingInstallation, "TrainingInstallation", "false");
		setDBPropertyMappingAndDefault(Property.PatientSearchURL, "patientSearchURL" , "");
		setDBPropertyMappingAndDefault(Property.PatientSearchUserName, "patientSearchLogOnUser" , "" );
		setDBPropertyMappingAndDefault(Property.PatientSearchPassword, "patientSearchPassword", "" );
		setDBPropertyMappingAndDefault(Property.UseExternalPatientInfo, "useExternalPatientSource" , "false");
		setDBPropertyMappingAndDefault(Property.labDirectorName, "lab director" , "");
		setDBPropertyMappingAndDefault(Property.languageSwitch, "allowLanguageChange", "false" );
		setDBPropertyMappingAndDefault(Property.resultReportingURL, "resultReportingURL", "");
		setDBPropertyMappingAndDefault(Property.reportResults, "resultReporting", "false");
		setDBPropertyMappingAndDefault(Property.malariaSurveillanceReportURL, "malariaSurURL", "");
		setDBPropertyMappingAndDefault(Property.malariaSurveillanceReport, "malariaSurReport", "false");
		setDBPropertyMappingAndDefault(Property.malariaCaseReport, "malariaCaseReport", "false");
		setDBPropertyMappingAndDefault(Property.malariaCaseReportURL, "malariaCaseURL", "");
		setDBPropertyMappingAndDefault(Property.testUsageReportingURL, "testUsageAggregationUrl", "");
		setDBPropertyMappingAndDefault(Property.testUsageReporting, "testUsageReporting", "false");
		setDBPropertyMappingAndDefault(Property.roleRequiredForModifyResults, "modify results role" , "false");
		setDBPropertyMappingAndDefault(Property.notesRequiredForModifyResults, "modify results note required", "false" );
		setDBPropertyMappingAndDefault(Property.resultTechnicianName, "ResultTechnicianName", "false");
		setDBPropertyMappingAndDefault(Property.allowResultRejection, "allowResultRejection", "false");
		setDBPropertyMappingAndDefault(Property.autoFillTechNameBox, "autoFillTechNameBox", "false");
		setDBPropertyMappingAndDefault(Property.autoFillTechNameUser, "autoFillTechNameUser", "false");
		setDBPropertyMappingAndDefault(Property.failedValidationMarker, "showValidationFailureIcon", "true");
		setDBPropertyMappingAndDefault(Property.SiteName, "SiteName", "");
		setDBPropertyMappingAndDefault(Property.PasswordRequirments , "passwordRequirements", "MINN");
		setDBPropertyMappingAndDefault(Property.FormFieldSet , "setFieldForm", IActionConstants.FORM_FIELD_SET_HAITI);
		setDBPropertyMappingAndDefault(Property.StringContext , "stringContext","");
		setDBPropertyMappingAndDefault(Property.StatusRules , "statusRules", "CI");
		setDBPropertyMappingAndDefault(Property.ReflexAction , "reflexAction", "Haiti");
		setDBPropertyMappingAndDefault(Property.AccessionFormat , "acessionFormat", "SITEYEARNUM"); //spelled wrong in DB
		setDBPropertyMappingAndDefault(Property.TRACK_PATIENT_PAYMENT, "trackPayment", "false");
		setDBPropertyMappingAndDefault(Property.ALERT_FOR_INVALID_RESULTS, "alertWhenInvalidResult", "false");
		setDBPropertyMappingAndDefault(Property.DEFAULT_DATE_LOCALE, "default date locale", "fr-FR");
		setDBPropertyMappingAndDefault(Property.DEFAULT_LANG_LOCALE, "default language locale", "fr-FR");
		setDBPropertyMappingAndDefault(Property.configurationName, "configuration name", "not set");
		setDBPropertyMappingAndDefault(Property.CONDENSE_NFS_PANEL, "condenseNFS", "false");
		setDBPropertyMappingAndDefault(Property.PATIENT_DATA_ON_RESULTS_BY_ROLE, "roleForPatientOnResults", "false");
		setDBPropertyMappingAndDefault(Property.USE_PAGE_NUMBERS_ON_REPORTS, "reportPageNumbers", "true");
		setDBPropertyMappingAndDefault(Property.QA_SORT_EVENT_LIST, "sortQaEvents", "true");
		setDBPropertyMappingAndDefault(Property.USE_SAMPLE_TYPE_AUTOCOMPLETE, "useSampleTypeAutocomplete", "false");
		setDBPropertyMappingAndDefault(Property.USE_SAMPLE_SOURCE, "useSampleSource", "false");
		setDBPropertyMappingAndDefault(Property.USE_SAMPLE_SOURCE_AUTOCOMPLETE, "useSampleSourceAutocomplete", "false");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_ACCESSION, "Master labels: accession #", "true");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_COLLECTION_DATE, "Master labels: collection date", "true");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_COLLECTION_TIME, "Master labels: collection time", "true");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_PATIENT_ID, "Master labels: patient ID", "false");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_NATIONAL_ID, "Master labels: national ID", "false");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_PATIENT_MRN, "Master labels: patient MRN", "false");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_PATIENT_NAME, "Master labels: patient name", "false");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_PATIENT_DOB, "Master labels: patient DOB", "false");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_PATIENT_AGE, "Master labels: patient age", "false");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_PATIENT_SEX, "Master labels: patient gender", "false");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_LAB_SECTIONS, "Master labels: lab section(s)", "false");
		setDBPropertyMappingAndDefault(Property.MASTER_LABELS_SPECIMEN_TYPE, "Master labels: specimen type", "false");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_ACCESSION, "Master prefix: accession #", "");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_COLLECTION_DATE, "Master prefix: collection date", "Collect Date: ");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_COLLECTION_TIME, "Master prefix: collection time", "Collect Time: ");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_PATIENT_ID, "Master prefix: patient ID", "Pat. ID: ");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_NATIONAL_ID, "Master prefix: national ID", "Nat. ID: ");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_PATIENT_MRN, "Master prefix: patient MRN", "MRN: ");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_PATIENT_NAME, "Master prefix: patient name", "Name: ");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_PATIENT_DOB, "Master prefix: patient DOB", "DOB: ");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_PATIENT_AGE, "Master prefix: patient age", "Age: ");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_PATIENT_SEX, "Master prefix: patient gender", "Sex: ");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_LAB_SECTIONS, "Master prefix: lab section(s)", "Section(s): ");
		setDBPropertyMappingAndDefault(Property.MASTER_PREFIX_SPECIMEN_TYPE, "Master prefix: specimen type", "Type: ");
		setDBPropertyMappingAndDefault(Property.USE_SPECIMEN_LABELS, "useSpecimenLabels", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_ACCESSION, "Specimen labels: accession #", "true");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_COLLECTION_DATE, "Specimen labels: collection date", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_COLLECTION_TIME, "Specimen labels: collection time", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_PATIENT_ID, "Specimen labels: patient ID", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_NATIONAL_ID, "Specimen labels: national ID", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_PATIENT_MRN, "Specimen labels: patient MRN", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_PATIENT_NAME, "Specimen labels: patient name", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_PATIENT_DOB, "Specimen labels: patient DOB", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_PATIENT_AGE, "Specimen labels: patient age", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_PATIENT_SEX, "Specimen labels: patient gender", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_LAB_SECTIONS, "Specimen labels: lab section(s)", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_LABELS_SPECIMEN_TYPE, "Specimen labels: specimen type", "false");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_ACCESSION, "Specimen prefix: accession #", "");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_COLLECTION_DATE, "Specimen prefix: collection date", "Collect Date: ");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_COLLECTION_TIME, "Specimen prefix: collection time", "Collect Time: ");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_PATIENT_ID, "Specimen prefix: patient ID", "Pat. ID: ");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_NATIONAL_ID, "Specimen prefix: national ID", "Nat. ID: ");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_PATIENT_MRN, "Specimen prefix: patient MRN", "MRN: ");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_PATIENT_NAME, "Specimen prefix: patient name", "Name: ");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_PATIENT_DOB, "Specimen prefix: patient DOB", "DOB: ");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_PATIENT_AGE, "Specimen prefix: patient age", "Age: ");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_PATIENT_SEX, "Specimen prefix: patient gender", "Sex: ");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_LAB_SECTIONS, "Specimen prefix: lab section(s)", "Section(s): ");
		setDBPropertyMappingAndDefault(Property.SPECIMEN_PREFIX_SPECIMEN_TYPE, "Specimen prefix: specimen type", "Type: ");
		setDBPropertyMappingAndDefault(Property.ALWAYS_VALIDATE_RESULTS, "validate all results", "true");
		setDBPropertyMappingAndDefault(Property.ADDITIONAL_SITE_INFO, "additional site info", "");
		setDBPropertyMappingAndDefault(Property.SUBJECT_ON_WORKPLAN, "subject on workplan", "false");
		setDBPropertyMappingAndDefault(Property.NEXT_VISIT_DATE_ON_WORKPLAN, "next visit on workplan", "false");
		setDBPropertyMappingAndDefault(Property.ACCEPT_EXTERNAL_ORDERS, "external orders", "false");
		setDBPropertyMappingAndDefault(Property.SIGNATURES_ON_NONCONFORMITY_REPORTS, "non-conformity signature", "false");
		setDBPropertyMappingAndDefault(Property.AUTOFILL_COLLECTION_DATE, "auto-fill collection date/time", "true");
		setDBPropertyMappingAndDefault(Property.RESULTS_ON_WORKPLAN, "results on workplan", "false");
		setDBPropertyMappingAndDefault(Property.NONCONFORMITY_RECEPTION_AS_UNIT, "Reception as unit", "true");
		setDBPropertyMappingAndDefault(Property.NONCONFORMITY_SAMPLE_COLLECTION_AS_UNIT, "Collection as unit", "false");
        setDBPropertyMappingAndDefault(Property.ACCESSION_NUMBER_PREFIX, "Accession number prefix", "");
        setDBPropertyMappingAndDefault(Property.NOTE_EXTERNAL_ONLY_FOR_VALIDATION, "validationOnlyNotesAreExternal", "false");
        setDBPropertyMappingAndDefault(Property.PHONE_FORMAT, "phone format", "(ddd) dddd-dddd");
        setDBPropertyMappingAndDefault(Property.VALIDATE_PHONE_FORMAT, "validate phone format", "true");
        setDBPropertyMappingAndDefault( Property.ALLOW_DUPLICATE_SUBJECT_NUMBERS, "Allow duplicate subject number", "true" );
        setDBPropertyMappingAndDefault( Property.VALIDATE_REJECTED_TESTS, "validateTechnicalRejection", "false" );
        setDBPropertyMappingAndDefault( Property.TEST_NAME_AUGMENTED, "augmentTestNameWithType", "true" );
        setDBPropertyMappingAndDefault( Property.USE_BILLING_REFERENCE_NUMBER, "billingRefNumber", "false" );
        setDBPropertyMappingAndDefault( Property.BILLING_REFERENCE_NUMBER_LABEL, "billingRefNumberLocalization", "-1" );
        setDBPropertyMappingAndDefault( Property.ORDER_PROGRAM, "Program", "false" );
        setDBPropertyMappingAndDefault( Property.BANNER_TEXT, "bannerHeading", "-1" );
        setDBPropertyMappingAndDefault( Property.CLOCK_24, "24 hour clock", "true" );
	}

	private void setDBPropertyMappingAndDefault(Property property, String dbName, String defaultValue) {
		dbNamePropertiesMap.put(dbName, property);
		propertiesValueMap.put(property, defaultValue);
	}

	protected DefaultConfigurationProperties(){
		loadFromPropertiesFile();
		loadSpecial();
	}


	protected void loadIfPropertyValueNeeded(Property property){
		if( !databaseLoaded && dbNamePropertiesMap.containsValue(property)){
			loadFromDatabase();
		}
	}

	protected void loadFromDatabase() {
		SiteInformationDAO siteInformationDAO = new SiteInformationDAOImpl();
		List<SiteInformation> siteInformationList = siteInformationDAO.getAllSiteInformation();

		for( SiteInformation siteInformation : siteInformationList){
			Property property = dbNamePropertiesMap.get(siteInformation.getName());
			if( property != null){
				propertiesValueMap.put(property, siteInformation.getValue());
			}
		}

		databaseLoaded = true;
	}

	protected void loadFromPropertiesFile() {
		InputStream propertyStream = null;

		try {
			propertyStream = this.getClass().getResourceAsStream(propertyFile);

			// Now load a java.util.Properties object with the properties
			properties = new java.util.Properties();

			properties.load(propertyStream);

		} catch (Exception e) {
			LogEvent.logError("DefaultConfigurationProperties","",e.toString());
		} finally {
			if (null != propertyStream) {
				try {
					propertyStream.close();
					propertyStream = null;
				} catch (Exception e) {
			        LogEvent.logError("DefaultConfigurationProperties","",e.toString());
				}
			}

		}

		for( Property property : propertiesFileMap.keySet()){
			KeyDefaultPair pair = propertiesFileMap.get(property);
			String value = properties.getProperty( pair.key, pair.defaultValue);
			propertiesValueMap.put(property, value);
		}
	}
	
	private void loadSpecial() {
		propertiesValueMap.put(Property.releaseNumber, Versioning.getReleaseNumber());
		propertiesValueMap.put(Property.buildNumber, Versioning.getBuildNumber());
	}

	protected class KeyDefaultPair{
		public final String key;
		public final String defaultValue;

		public KeyDefaultPair( String key, String defaultValue){
			this.key = key;
			this.defaultValue = defaultValue;
		}
	}
}
