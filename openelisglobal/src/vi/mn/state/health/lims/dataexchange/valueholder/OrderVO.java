/**
 * 
 */
package vi.mn.state.health.lims.dataexchange.valueholder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import us.mn.state.health.lims.patient.valueholder.Patient;
import us.mn.state.health.lims.provider.valueholder.Provider;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.test.valueholder.TestSection;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;

/**
 * @author dungtdo.sl
 *
 */
public class OrderVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String etorId;
	private String accessionNumber;
	private String receivedDate;
	private String collectionDate;
	private String collectionTime;
	private String doctorFirstName;
	private int typeOfSampleId;
	private String dataOfBirth;
	private String age;
	private String Diagnosis;//cd
	private String streetAddress;
	private String ward;
	private String district;
	private String city;
	private String fullname;
	private String gender;
	private String timeReceivedTimeForDisplay;
	private List<Test> liTest=new ArrayList<Test>();
	private List<SampleItem> liSampleItem=new ArrayList<SampleItem>();
	private Sample sample = new Sample();
	private Patient patient  = new Patient();;
	private Provider provider= new Provider();
	private Map<String,String> mapTest = new HashMap<>();
	private TestSection testSection=new TestSection();
	
	//add more recored to sample_item
	private List<TypeOfSample> typeOfSample =new ArrayList<TypeOfSample>();
	
	//add parameter for web service list test result
    private List<TestResultVO>  liTestResultVO = new ArrayList<TestResultVO>();
    
    public OrderVO() {
    }
    
	public Provider getProvider() {
	    return provider;
    }
	
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	public String getAccessionNumber() {
		return accessionNumber;
	}
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	public int getTypeOfSampleId() {
		return typeOfSampleId;
	}
	public void setTypeOfSampleId(int typeOfSampleId) {
		this.typeOfSampleId = typeOfSampleId;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public List<Test> getLiTest() {
		return liTest;
	}
	public void setLiTest(List<Test> liTest) {
		this.liTest = liTest;
	}
	public String getTimeReceivedTimeForDisplay() {
		return timeReceivedTimeForDisplay;
	}
	public void setTimeReceivedTimeForDisplay(String timeReceivedTimeForDisplay) {
		this.timeReceivedTimeForDisplay = timeReceivedTimeForDisplay;
	}
	public String getCollectionDate() {
		return collectionDate;
	}
	public void setCollectionDate(String collectionDate) {
		this.collectionDate = collectionDate;
	}
	public String getCollectionTime() {
		return collectionTime;
	}
	public void setCollectionTime(String collectionTime) {
		this.collectionTime = collectionTime;
	}
	public String getDoctorFirstName() {
		return doctorFirstName;
	}
	public void setDoctorFirstName(String doctorFirstName) {
		this.doctorFirstName = doctorFirstName;
	}
	public String getDataOfBirth() {
		return dataOfBirth;
	}
	public void setDataOfBirth(String dataOfBirth) {
		this.dataOfBirth = dataOfBirth;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getDiagnosis() {
		return Diagnosis;
	}
	public void setDiagnosis(String diagnosis) {
		Diagnosis = diagnosis;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public String getWard() {
		return ward;
	}
	public void setWard(String ward) {
		this.ward = ward;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}
	public Sample getSample() {
		return sample;
	}
	public void setSample(Sample sample) {
		this.sample = sample;
	}
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	public List<SampleItem> getLiSampleItem() {
		return liSampleItem;
	}
	public void setLiSampleItem(List<SampleItem> liSampleItem) {
		this.liSampleItem = liSampleItem;
	}
	public List<TypeOfSample> getTypeOfSample() {
		return typeOfSample;
	}
	public void setTypeOfSample(List<TypeOfSample> typeOfSample) {
		this.typeOfSample = typeOfSample;
	}
	public java.util.Map<String, String> getMapTest() {
		return mapTest;
	}
	public void setMapTest(java.util.Map<String, String> mapTest) {
		this.mapTest = mapTest;
	}
	public TestSection getTestSection() {
		return testSection;
	}
	public void setTestSection(TestSection testSection) {
		this.testSection = testSection;
	}
	public String getEtorId() {
		return etorId;
	}
	public void setEtorId(String etorId) {
		this.etorId = etorId;
	}
	public List<TestResultVO> getLiTestResultVO() {
		return liTestResultVO;
	}
	public void setLiTestResultVO(List<TestResultVO> liTestResultVO) {
		this.liTestResultVO = liTestResultVO;
	}
}
