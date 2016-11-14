package vi.mn.state.health.lims.report.valueholder;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class ReportSampleResult {
	private String stt;
	private BigDecimal personId;
	private BigDecimal patientId;
	private String accessNumber;
	private String docter;
	private Timestamp collectionDate;
	private String nationalId;
	private Double age;
	private Double monthBegin;
	private Double monthEnd;
	private Double birthYear;
	private Double birthMonth;
	private Double birthDay;
	private String name;
	private String gender;
	private BigDecimal sampleItem;
	private BigDecimal typeSampleId;
	private String typeSample;
	private BigDecimal testId;
	private String testName;
	private String result;
	private String resultValue;
	private String resultType;
	private String loilc;
	private String reportDescriptoon;
	private BigDecimal sortOrder;
	private Timestamp recieveDate;
	private BigDecimal timeTaMax;
	private BigDecimal timeTaAverage;
	private String description;
	private BigDecimal testSectionId;
	private String testSection;
	private Timestamp testSectionOrder;
	private String isBold;
	private String paymentType;
	private String department;
	private String isuranceNumber;
	private String address;
	private String patientDiagnosis;
	private String patientType;
	private String hour;
	private String city;
	private String organizationName;
	private String resultDo;
	private Timestamp resultDate;
	private String chartNumber;
	
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}
	public BigDecimal getPersonId() {
		return personId;
	}
	public void setPersonId(BigDecimal personId) {
		this.personId = personId;
	}
	public BigDecimal getPatientId() {
		return patientId;
	}
	public String getPatientType() {
		return patientType;
	}
	public void setPatientType(String patientType) {
		this.patientType = patientType;
	}
	public void setPatientId(BigDecimal patientId) {
		this.patientId = patientId;
	}
	public String getAccessNumber() {
		return accessNumber;
	}
	public void setAccessNumber(String accessNumber) {
		this.accessNumber = accessNumber;
	}
	public String getDocter() {
		return docter;
	}
	public void setDocter(String docter) {
		this.docter = docter;
	}
	public Timestamp getCollectionDate() {
		return collectionDate;
	}
	public void setCollectionDate(Timestamp collectionDate) {
		this.collectionDate = collectionDate;
	}
	public String getNationalId() {
		return nationalId;
	}
	public void setNationalId(String nationalId) {
		this.nationalId = nationalId;
	}
	public Double getAge() {
		return age;
	}
	public void setAge(Double age) {
		this.age = age;
	}
	public Double getMonthBegin() {
		return monthBegin;
	}
	public void setMonthBegin(Double monthBegin) {
		this.monthBegin = monthBegin;
	}
	public Double getMonthEnd() {
		return monthEnd;
	}
	public void setMonthEnd(Double monthEnd) {
		this.monthEnd = monthEnd;
	}
	public Double getBirthYear() {
		return birthYear;
	}
	public void setBirthYear(Double birthYear) {
		this.birthYear = birthYear;
	}
	public Double getBirthMonth() {
		return birthMonth;
	}
	public void setBirthMonth(Double birthMonth) {
		this.birthMonth = birthMonth;
	}
	public Double getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(Double birthDay) {
		this.birthDay = birthDay;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public BigDecimal getSampleItem() {
		return sampleItem;
	}
	public void setSampleItem(BigDecimal sampleItem) {
		this.sampleItem = sampleItem;
	}
	public BigDecimal getTypeSampleId() {
		return typeSampleId;
	}
	public void setTypeSampleId(BigDecimal typeSampleId) {
		this.typeSampleId = typeSampleId;
	}
	public String getTypeSample() {
		return typeSample;
	}
	public void setTypeSample(String typeSample) {
		this.typeSample = typeSample;
	}
	public BigDecimal getTestId() {
		return testId;
	}
	public void setTestId(BigDecimal testId) {
		this.testId = testId;
	}
	public String getTestName() {
		return testName;
	}
	public void setTestName(String testName) {
		this.testName = testName;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getResultValue() {
		return resultValue;
	}
	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public String getLoilc() {
		return loilc;
	}
	public void setLoilc(String loilc) {
		this.loilc = loilc;
	}
	public String getReportDescriptoon() {
		return reportDescriptoon;
	}
	public void setReportDescriptoon(String reportDescriptoon) {
		this.reportDescriptoon = reportDescriptoon;
	}
	public BigDecimal getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(BigDecimal sortOrder) {
		this.sortOrder = sortOrder;
	}
	public Timestamp getRecieveDate() {
		return recieveDate;
	}
	public void setRecieveDate(Timestamp recieveDate) {
		this.recieveDate = recieveDate;
	}
	public BigDecimal getTimeTaMax() {
		return timeTaMax;
	}
	public void setTimeTaMax(BigDecimal timeTaMax) {
		this.timeTaMax = timeTaMax;
	}
	public BigDecimal getTimeTaAverage() {
		return timeTaAverage;
	}
	public void setTimeTaAverage(BigDecimal timeTaAverage) {
		this.timeTaAverage = timeTaAverage;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getTestSectionId() {
		return testSectionId;
	}
	public void setTestSectionId(BigDecimal testSectionId) {
		this.testSectionId = testSectionId;
	}
	public String getTestSection() {
		return testSection;
	}
	public void setTestSection(String testSection) {
		this.testSection = testSection;
	}
	public Timestamp getTestSectionOrder() {
		return testSectionOrder;
	}
	public void setTestSectionOrder(Timestamp testSectionOrder) {
		this.testSectionOrder = testSectionOrder;
	}
	public String getIsBold() {
		return isBold;
	}
	public void setIsBold(String isBold) {
		this.isBold = isBold;
	}
	public String getStt() {
		return stt;
	}
	public void setStt(String stt) {
		this.stt = stt;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getIsuranceNumber() {
		return isuranceNumber;
	}
	public void setIsuranceNumber(String isuranceNumber) {
		this.isuranceNumber = isuranceNumber;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPatientDiagnosis() {
		return patientDiagnosis;
	}
	public void setPatientDiagnosis(String patientDiagnosis) {
		this.patientDiagnosis = patientDiagnosis;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getOrganizationName() {
		return organizationName;
	}
	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}
	public String getResultDo() {
		return resultDo;
	}
	public void setResultDo(String resultDo) {
		this.resultDo = resultDo;
	}
	public Timestamp getResultDate() {
		return resultDate;
	}
	public void setResultDate(Timestamp resultDate) {
		this.resultDate = resultDate;
	}
	public String getChartNumber() {
		return chartNumber;
	}
	public void setChartNumber(String chartNumber) {
		this.chartNumber = chartNumber;
	}
}