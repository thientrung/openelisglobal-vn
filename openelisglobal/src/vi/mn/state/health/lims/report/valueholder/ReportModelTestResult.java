/**
 * 
 */
package vi.mn.state.health.lims.report.valueholder;

import java.sql.Timestamp;

/**
 * @author nhuql.gv
 * 
 */
public class ReportModelTestResult {
	private int stt;
	private String accessionNumber;
	private Timestamp collectionDate;
	private Timestamp receivedDate;
	private String description;
	private String sampleCondition;
	private String firstName;
	private Timestamp birthDate;
	private String age;
	private String gender;
	private String address;
	private String department;
	private String patientDiagnosis;
	private String patientBedNumber;
	private String patientRoomNumber;
	private String externalId;
	private String patientType;
	private String paymentType;
	private String number1;
	private String number2;
	private String chartNumber;
	private String emergency;
	private int testId;
	private String testName;
	private String testDescription;
	private String unit;
	private String testSectionDescription;
	private int testSectionId;
	private String result;
	private String resultType;
	private String projectId;
	private String organizationName;
	private String organizationLocalAbbrev;
	private int patientId;
    private Timestamp resultDate;
    private Timestamp releasedDate;
	private String testResultId;
	private String parentId;
	private String doctor;
	private String specimenCondition;
	private String originalResult;
	private String dob;
	private Timestamp illnessDate;
	private int organizationId;
	private String city;

	public Timestamp getIllnessDate() {
		return illnessDate;
	}

	public void setIllnessDate(Timestamp illnessDate) {
		this.illnessDate = illnessDate;
	}

	/**
	 * @return the stt
	 */
	public int getStt() {
		return stt;
	}

	/**
	 * @param stt
	 *            the stt to set
	 */
	public void setStt(int stt) {
		this.stt = stt;
	}

	/**
	 * @return the accessionNumber
	 */
	public String getAccessionNumber() {
		return accessionNumber;
	}

	/**
	 * @param accessionNumber
	 *            the accessionNumber to set
	 */
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	/**
	 * @return the collectionDate
	 */
	public Timestamp getCollectionDate() {
		return collectionDate;
	}

	/**
	 * @param collectionDate
	 *            the collectionDate to set
	 */
	public void setCollectionDate(Timestamp collectionDate) {
		this.collectionDate = collectionDate;
	}

	/**
	 * @return the receivedDate
	 */
	public Timestamp getReceivedDate() {
		return receivedDate;
	}

	/**
	 * @param receivedDate
	 *            the receivedDate to set
	 */
	public void setReceivedDate(Timestamp receivedDate) {
		this.receivedDate = receivedDate;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the sampleCondition
	 */
	public String getSampleCondition() {
		return sampleCondition;
	}

	/**
	 * @param sampleCondition
	 *            the sampleCondition to set
	 */
	public void setSampleCondition(String sampleCondition) {
		this.sampleCondition = sampleCondition;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName
	 *            the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the birthDate
	 */
	public Timestamp getBirthDate() {
		return birthDate;
	}

	/**
	 * @param birthDate
	 *            the birthDate to set
	 */
	public void setBirthDate(Timestamp birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * @return the age
	 */
	public String getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(String age) {
		this.age = age;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the department
	 */
	public String getDepartment() {
		return department;
	}

	/**
	 * @param department
	 *            the department to set
	 */
	public void setDepartment(String department) {
		this.department = department;
	}

	/**
	 * @return the patientDiagnosis
	 */
	public String getPatientDiagnosis() {
		return patientDiagnosis;
	}

	/**
	 * @param patientDiagnosis
	 *            the patientDiagnosis to set
	 */
	public void setPatientDiagnosis(String patientDiagnosis) {
		this.patientDiagnosis = patientDiagnosis;
	}

	/**
	 * @return the patientBedNumber
	 */
	public String getPatientBedNumber() {
		return patientBedNumber;
	}

	/**
	 * @param patientBedNumber
	 *            the patientBedNumber to set
	 */
	public void setPatientBedNumber(String patientBedNumber) {
		this.patientBedNumber = patientBedNumber;
	}

	/**
	 * @return the patientRoomNumber
	 */
	public String getPatientRoomNumber() {
		return patientRoomNumber;
	}

	/**
	 * @param patientRoomNumber
	 *            the patientRoomNumber to set
	 */
	public void setPatientRoomNumber(String patientRoomNumber) {
		this.patientRoomNumber = patientRoomNumber;
	}

	/**
	 * @return the externalId
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @param externalId
	 *            the externalId to set
	 */
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	/**
	 * @return the patientType
	 */
	public String getPatientType() {
		return patientType;
	}

	/**
	 * @param patientType
	 *            the patientType to set
	 */
	public void setPatientType(String patientType) {
		this.patientType = patientType;
	}

	/**
	 * @return the paymentType
	 */
	public String getPaymentType() {
		return paymentType;
	}

	/**
	 * @param paymentType
	 *            the paymentType to set
	 */
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * @return the number1
	 */
	public String getNumber1() {
		return number1;
	}

	/**
	 * @param number1
	 *            the number1 to set
	 */
	public void setNumber1(String number1) {
		this.number1 = number1;
	}

	/**
	 * @return the number2
	 */
	public String getNumber2() {
		return number2;
	}

	/**
	 * @param number2
	 *            the number2 to set
	 */
	public void setNumber2(String number2) {
		this.number2 = number2;
	}

	/**
	 * @return the chartNumber
	 */
	public String getChartNumber() {
		return chartNumber;
	}

	/**
	 * @param chartNumber
	 *            the chartNumber to set
	 */
	public void setChartNumber(String chartNumber) {
		this.chartNumber = chartNumber;
	}

	/**
	 * @return the emergency
	 */
	public String getEmergency() {
		return emergency;
	}

	/**
	 * @param emergency
	 *            the emergency to set
	 */
	public void setEmergency(String emergency) {
		this.emergency = emergency;
	}

	/**
	 * @return the testId
	 */
	public int getTestId() {
		return testId;
	}

	/**
	 * @param testId
	 *            the testId to set
	 */
	public void setTestId(int testId) {
		this.testId = testId;
	}

	/**
	 * @return the testName
	 */
	public String getTestName() {
		return testName;
	}

	/**
	 * @param testName
	 *            the testName to set
	 */
	public void setTestName(String testName) {
		this.testName = testName;
	}

	/**
	 * @return the testDescription
	 */
	public String getTestDescription() {
		return testDescription;
	}

	/**
	 * @param testDescription
	 *            the testDescription to set
	 */
	public void setTestDescription(String testDescription) {
		this.testDescription = testDescription;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit
	 *            the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the testSectionDescription
	 */
	public String getTestSectionDescription() {
		return testSectionDescription;
	}

	/**
	 * @param testSectionDescription
	 *            the testSectionDescription to set
	 */
	public void setTestSectionDescription(String testSectionDescription) {
		this.testSectionDescription = testSectionDescription;
	}

	/**
	 * @return the testSectionId
	 */
	public int getTestSectionId() {
		return testSectionId;
	}

	/**
	 * @param testSectionId
	 *            the testSectionId to set
	 */
	public void setTestSectionId(int testSectionId) {
		this.testSectionId = testSectionId;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @return the resultType
	 */
	public String getResultType() {
		return resultType;
	}

	/**
	 * @param resultType
	 *            the resultType to set
	 */
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	/**
	 * @return the projectId
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId
	 *            the projectId to set
	 */
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	/**
	 * @return the organizationName
	 */
	public String getOrganizationName() {
		return organizationName;
	}

	/**
	 * @param organizationName
	 *            the organizationName to set
	 */
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	/**
	 * @return the organizationLocalAbbrev
	 */
	public String getOrganizationLocalAbbrev() {
		return organizationLocalAbbrev;
	}

	/**
	 * @param organizationLocalAbbrev
	 *            the organizationLocalAbbrev to set
	 */
	public void setOrganizationLocalAbbrev(String organizationLocalAbbrev) {
		this.organizationLocalAbbrev = organizationLocalAbbrev;
	}

	/**
	 * @return the patientId
	 */
	public int getPatientId() {
		return patientId;
	}

	/**
	 * @param patientId
	 *            the patientId to set
	 */
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	/**
	 * @return the resultDate
	 */
	public Timestamp getResultDate() {
		return resultDate;
	}

	/**
	 * @param resultDate
	 *            the resultDate to set
	 */
	public void setResultDate(Timestamp resultDate) {
		this.resultDate = resultDate;
	}

	/**
     * Get releasedDate 
     *
     * @return releasedDate
     */
    public Timestamp getReleasedDate() {
        return releasedDate;
    }

    /**
     * Set releasedDate 
     *
     * @param releasedDate the releasedDate to set
     */
    public void setReleasedDate(Timestamp releasedDate) {
        this.releasedDate = releasedDate;
    }

    /**
	 * @return the testResultId
	 */
	public String getTestResultId() {
		return testResultId;
	}

	/**
	 * @param testResultId
	 *            the testResultId to set
	 */
	public void setTestResultId(String testResultId) {
		this.testResultId = testResultId;
	}

	/**
	 * @return the parentId
	 */
	public String getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 *            the parentId to set
	 */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the doctor
	 */
	public String getDoctor() {
		return doctor;
	}

	/**
	 * @param doctor
	 *            the doctor to set
	 */
	public void setDoctor(String doctor) {
		this.doctor = doctor;
	}

	public String getSpecimenCondition() {
		return specimenCondition;
	}

	public void setSpecimenCondition(String specimenCondition) {
		this.specimenCondition = specimenCondition;
	}

	public String getOriginalResult() {
		return originalResult;
	}

	public void setOriginalResult(String originalResult) {
		this.originalResult = originalResult;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public int getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}

    /**
     * Get city 
     *
     * @return city
     */
    public String getCity() {
        return city;
    }

    /**
     * Set city 
     *
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }
}
