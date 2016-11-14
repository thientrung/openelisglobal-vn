/**
 * @(#) OrderDataDTO.java 01-00 May 9, 2016
 * 
 * Copyright 2016 by Global CyberSoft VietNam Inc.
 * 
 * Last update May 9, 2016
 */
package vi.mn.state.health.lims.dataexchange.dto;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * @author thonghh
 * 
 */
public class OrderDataDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6551935364405768731L;

    // 1. "collection_date": "2016-05-06T23:30:00-04:00",
    @SerializedName("collection_date")
    private String collectionDate;

    // 2. "collector": "Steve Frost",
    private String collector;

    // 3. "sample_type": "3",
    @SerializedName("sample_type")
    private String sampleType;

    // 4. "diagnosis": "DiagnosisValue",
    private String diagnosis;

    // 5. "doctor": "Dr. Frost",
    private String doctor;

    // 6. "external_id": 166,
    @SerializedName("external_id")
    private String externalId;

    // 7. "illness_date": "2016-05-02T00:00:00-04:00",
    @SerializedName("illness_date")
    private String illnessDate;

    // 8. "received_date": "2016-05-05T00:00:00-04:00",
    @SerializedName("received_date")
    private String receivedDate;

    // 9. "result": "Negative",
    private String result;

    // 10. "age": "26",
    private String age;

    // 11. "birth_date": "1990-06-12T00:00:00-04:00",
    @SerializedName("birth_date")
    private String birthDate;

    // 12. "city": "TP HCM",
    private String city;

    // 13. "district": "Quận 3",
    private String district;

    // 14. "gender": "146",
    private String gender;

    // 15. "org_id": "1",
    @SerializedName("org_id")
    private String orgId;

    // 16. "patient_id": "166",
    @SerializedName("patient_id")
    private String patientId;

    // 17. "patient_name": "FullName",
    @SerializedName("patient_name")
    private String patientName;

    // 18. "street_address": "121 Lý Chính Thắng",
    @SerializedName("street_address")
    private String streetAddress;

    // 19. "username": "admin",
    private String username;

    // 20. "user_id": "01",
    @SerializedName("user_id")
    private String userId;

    // 21. "ward": "06",
    private String ward;

    // 22. "test_id": "341",
    @SerializedName("test_id")
    private String testId;

    // 23. "patient_gender":"M",
    @SerializedName("patient_gender")
    private String patientGender;

    // 24. "patient_ward":"A",
    @SerializedName("patient_ward")
    private String patientWard;

    // 25. "test_selection_id":"1",
    @SerializedName("test_selection_id")
    private String testSelectionId;

    // 26. "validate":"B"
    @SerializedName("tevalidatest_id")
    private String tevalidatestId;

    // 27. result_id
    @SerializedName("result_id")
    private String resultId;

    // 28. List<sample_type>
    private List<SampleDTO> samples;

    private String Etor_id;

    /**
     * Default constructor
     */
    public OrderDataDTO() {
    }

    /**
     * Get collectionDate
     * 
     * @return collectionDate
     */
    public String getCollectionDate() {
        return collectionDate;
    }

    /**
     * Set collectionDate
     * 
     * @param collectionDate
     *            the collectionDate to set
     */
    public void setCollectionDate(String collectionDate) {
        this.collectionDate = collectionDate;
    }

    /**
     * Get collector
     * 
     * @return collector
     */
    public String getCollector() {
        return collector;
    }

    /**
     * Set collector
     * 
     * @param collector
     *            the collector to set
     */
    public void setCollector(String collector) {
        this.collector = collector;
    }

    /**
     * Get sampleType
     * 
     * @return sampleType
     */
    public String getSampleType() {
        return sampleType;
    }

    /**
     * Set sampleType
     * 
     * @param sampleType
     *            the sampleType to set
     */
    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    /**
     * Get diagnosis
     * 
     * @return diagnosis
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * Set diagnosis
     * 
     * @param diagnosis
     *            the diagnosis to set
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    /**
     * Get doctor
     * 
     * @return doctor
     */
    public String getDoctor() {
        return doctor;
    }

    /**
     * Set doctor
     * 
     * @param doctor
     *            the doctor to set
     */
    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    /**
     * Get externalId
     * 
     * @return externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * Set externalId
     * 
     * @param externalId
     *            the externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * Get illnessDate
     * 
     * @return illnessDate
     */
    public String getIllnessDate() {
        return illnessDate;
    }

    /**
     * Set illnessDate
     * 
     * @param illnessDate
     *            the illnessDate to set
     */
    public void setIllnessDate(String illnessDate) {
        this.illnessDate = illnessDate;
    }

    /**
     * Get receivedDate
     * 
     * @return receivedDate
     */
    public String getReceivedDate() {
        return receivedDate;
    }

    /**
     * Set receivedDate
     * 
     * @param receivedDate
     *            the receivedDate to set
     */
    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }

    /**
     * Get result
     * 
     * @return result
     */
    public String getResult() {
        return result;
    }

    /**
     * Set result
     * 
     * @param result
     *            the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Get age
     * 
     * @return age
     */
    public String getAge() {
        return age;
    }

    /**
     * Set age
     * 
     * @param age
     *            the age to set
     */
    public void setAge(String age) {
        this.age = age;
    }

    /**
     * Get birthDate
     * 
     * @return birthDate
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * Set birthDate
     * 
     * @param birthDate
     *            the birthDate to set
     */
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
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
     * @param city
     *            the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Get district
     * 
     * @return district
     */
    public String getDistrict() {
        return district;
    }

    /**
     * Set district
     * 
     * @param district
     *            the district to set
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * Get gender
     * 
     * @return gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Set gender
     * 
     * @param gender
     *            the gender to set
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Get orgId
     * 
     * @return orgId
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * Set orgId
     * 
     * @param orgId
     *            the orgId to set
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * Get patientId
     * 
     * @return patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Set patientId
     * 
     * @param patientId
     *            the patientId to set
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * Get patientName
     * 
     * @return patientName
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * Set patientName
     * 
     * @param patientName
     *            the patientName to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * Get streetAddress
     * 
     * @return streetAddress
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Set streetAddress
     * 
     * @param streetAddress
     *            the streetAddress to set
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * Get username
     * 
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set username
     * 
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get userId
     * 
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Set userId
     * 
     * @param userId
     *            the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Get ward
     * 
     * @return ward
     */
    public String getWard() {
        return ward;
    }

    /**
     * Set ward
     * 
     * @param ward
     *            the ward to set
     */
    public void setWard(String ward) {
        this.ward = ward;
    }

    /**
     * Get testId
     * 
     * @return testId
     */
    public String getTestId() {
        return testId;
    }

    /**
     * Set testId
     * 
     * @param testId
     *            the testId to set
     */
    public void setTestId(String testId) {
        this.testId = testId;
    }

    /**
     * Get patientGender
     * 
     * @return patientGender
     */
    public String getPatientGender() {
        return patientGender;
    }

    /**
     * Set patientGender
     * 
     * @param patientGender
     *            the patientGender to set
     */
    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    /**
     * Get patientWard
     * 
     * @return patientWard
     */
    public String getPatientWard() {
        return patientWard;
    }

    /**
     * Set patientWard
     * 
     * @param patientWard
     *            the patientWard to set
     */
    public void setPatientWard(String patientWard) {
        this.patientWard = patientWard;
    }

    /**
     * Get testSelectionId
     * 
     * @return testSelectionId
     */
    public String getTestSelectionId() {
        return testSelectionId;
    }

    /**
     * Set testSelectionId
     * 
     * @param testSelectionId
     *            the testSelectionId to set
     */
    public void setTestSelectionId(String testSelectionId) {
        this.testSelectionId = testSelectionId;
    }

    /**
     * Get tevalidatestId
     * 
     * @return tevalidatestId
     */
    public String getTevalidatestId() {
        return tevalidatestId;
    }

    /**
     * Set tevalidatestId
     * 
     * @param tevalidatestId
     *            the tevalidatestId to set
     */
    public void setTevalidatestId(String tevalidatestId) {
        this.tevalidatestId = tevalidatestId;
    }

    /**
     * Get resultId
     * 
     * @return resultId
     */
    public String getResultId() {
        return resultId;
    }

    /**
     * Set resultId
     * 
     * @param resultId
     *            the resultId to set
     */
    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    /**
     * Get samples
     * 
     * @return samples
     */
    public List<SampleDTO> getSamples() {
        return samples;
    }

    /**
     * Set samples
     * 
     * @param samples
     *            the samples to set
     */
    public void setSamples(List<SampleDTO> samples) {
        this.samples = samples;
    }

    /**
     * Get etor_id
     * 
     * @return etor_id
     */
    public String getEtor_id() {
        return Etor_id;
    }

    /**
     * Set etor_id
     * 
     * @param etor_id
     *            the etor_id to set
     */
    public void setEtor_id(String etor_id) {
        Etor_id = etor_id;
    }

}
