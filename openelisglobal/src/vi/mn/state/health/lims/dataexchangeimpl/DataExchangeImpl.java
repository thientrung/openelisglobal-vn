package vi.mn.state.health.lims.dataexchangeimpl;

import java.util.List;

import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import us.mn.state.health.lims.patient.action.bean.PatientManagementInfo;
import us.mn.state.health.lims.sample.action.util.SamplePatientUpdateData;
import us.mn.state.health.lims.sample.bean.SampleOrderItem;
import vi.mn.state.health.lims.dataexchange.DataExchange;
import vi.mn.state.health.lims.dataexchange.valueholder.OrderVO;

public class DataExchangeImpl implements DataExchange {

	@Override
	public List<Object[]> getStructureFields() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see vi.mn.state.health.lims.dataexchange.DataExchange#addNewOrder(java.util.List)
	 */
	@Override
	public boolean addNewOrder(List<OrderVO> order) throws LIMSRuntimeException {
		//examble basic data
		OrderVO orderVO =new OrderVO();
		orderVO.setAccessionNumber("1670010102");
		orderVO.setDoctorFirstName("Tiên Khánh");
		orderVO.setReceivedDate("03/05/2016");
		orderVO.setCollectionDate("03/05/2016");
		orderVO.setDiagnosis("Bệnh Trĩ");
		orderVO.setTypeOfSampleId(7);//mau
		orderVO.setFullname("Đào Duy Tần");
		orderVO.setDataOfBirth("11/11/2000");
		orderVO.setAge("16");
		orderVO.setGender("M");
		orderVO.setStreetAddress("Duy tiên");
		orderVO.setWard("Tam đảo");
		orderVO.setDistrict("1418");
		orderVO.setCity("1343");
		
		
		if(order.size() <0)
			return false;
		
		// TODO Auto-generated method stub
		SamplePatientUpdateData updateData = new SamplePatientUpdateData("1") ;
		//data put to patient 
		PatientManagementInfo patientInfo= new PatientManagementInfo();
		patientInfo.setFirstName(orderVO.getFullname());
		patientInfo.setStreetAddress(orderVO.getStreetAddress());
		patientInfo.setAddressWard(orderVO.getWard());
		patientInfo.setAddressDistrict(orderVO.getWard());
		patientInfo.setCity(orderVO.getCity());
		patientInfo.setGender(orderVO.getGender());
		patientInfo.setBirthDateForDisplay(orderVO.getDataOfBirth());
		patientInfo.setPatientProcessingStatus("add");
		
		SampleOrderItem sampleOrder = new SampleOrderItem();
		//put data to sample Order
		sampleOrder.setReceivedTime(orderVO.getReceivedDate());
		sampleOrder.setLabNo(orderVO.getAccessionNumber());
		sampleOrder.setProviderFirstName(orderVO.getDoctorFirstName());
		sampleOrder.setProviderFirstName(orderVO.getDoctorFirstName());
		sampleOrder.setPatientDiagnosis(orderVO.getDiagnosis());
		sampleOrder.setPatientAgeValue(orderVO.getAge());
		
		String receivedDateForDisplay =  sampleOrder.getReceivedDateForDisplay();
		if (!GenericValidator.isBlankOrNull(sampleOrder.getReceivedTime())) {
			receivedDateForDisplay += " " + sampleOrder.getReceivedTime();
		}else{
			receivedDateForDisplay += " 00:00";
		}
		//required
		updateData.setCollectionDateFromRecieveDateIfNeeded( receivedDateForDisplay );
		updateData.initializeRequester( sampleOrder );
        updateData.setProjectIds(sampleOrder);

		return false;
	}

}
