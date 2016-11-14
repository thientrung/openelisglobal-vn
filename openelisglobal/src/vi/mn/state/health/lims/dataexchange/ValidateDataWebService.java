/**
 * 
 */
package vi.mn.state.health.lims.dataexchange;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.NumberUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessages;

import us.mn.state.health.lims.common.action.IActionConstants;
import us.mn.state.health.lims.common.provider.validation.IAccessionNumberValidator.ValidationResults;
import us.mn.state.health.lims.common.util.validator.ActionError;
import us.mn.state.health.lims.sample.util.AccessionNumberUtil;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testresult.dao.TestResultDAO;
import us.mn.state.health.lims.testresult.daoimpl.TestResultDAOImpl;
import us.mn.state.health.lims.testresult.valueholder.TestResult;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSampleDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSampleDAOImpl;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSample;
import vi.mn.state.health.lims.dataexchange.valueholder.OrderVO;
import vi.mn.state.health.lims.dataexchange.valueholder.TestResultVO;

/**
 * @author dungtdo.sl
 *
 */
public class ValidateDataWebService implements IActionConstants{
	public static Map<String, String> validateOrderData(OrderVO orderVO) {
		Map<String, String> error=new HashMap<>();
		//validate accession number
ValidationResults result ;
		
			result = AccessionNumberUtil.correctFormat(orderVO.getSample().getAccessionNumber(), false);
			
			if( result == ValidationResults.SUCCESS ){
				result = AccessionNumberUtil.isUsed(orderVO.getSample().getAccessionNumber()) ? ValidationResults.SAMPLE_FOUND : ValidationResults.SAMPLE_NOT_FOUND;
			}		
		switch( result ) {
			case SUCCESS:
			    break;
			case SAMPLE_FOUND:
				error.put("accessionFound", FOUND_ACCESSION_NUMBER);
			case SAMPLE_NOT_FOUND:
			    break;
			default:
				error.put("accessionNotFound", NOT_FOUND_ACCESSION_NUMBER);
		}
		if(orderVO.getProvider().getPersonProvider()==null){
			error.put("notFoundProvider", NOT_FOUND_PROVIDER);
		}
		//validate test is correct
		for (Entry<String, String> mapTest : orderVO.getMapTest().entrySet()) {
			TestDAO testDAO=new TestDAOImpl();
			Test test=new Test();
			test=testDAO.getTestById(mapTest.getKey());
			if(test.getId()==null){
				error.put("notFoundTest"+mapTest.getKey(), NOT_FOUND_TEST +mapTest.getKey());
			}
		}
		//check type of sample correct in database
		for (TypeOfSample item : orderVO.getTypeOfSample()) {
			TypeOfSampleDAO typeOfSampleDAO=new TypeOfSampleDAOImpl();
			TypeOfSample typeOfSample=new TypeOfSample();
			typeOfSample=typeOfSampleDAO.getTypeOfSampleById(item.getId());
			if(typeOfSample.getId()==null){
				error.put("notFoundTypeOfSample"+item.getId(), NOT_FOUND_TYPE_OF_SAMPLE +item.getId());
			}
		}
		//sample item validate
		// validate etor id
		//validate test_result_id bat buoc phai co
//		for (TestResultVO item : orderVO.getLiTestResultVO()) {
//			TestResultDAO testResultDAO= new TestResultDAOImpl();
//			TestResult testResult=new TestResult();
//			if(NumberUtils.stringToInt(item.getResult()) > 0){
//				testResult.setId(item.getResult());
//				testResult=testResultDAO.getTestResultById(testResult);
//			}
//			if(testResult.getTest().getId()!=null){
//				error.put("notFoundTestResult"+item.getResult(), NOT_FOUND_TYPE_OF_SAMPLE +item.getResult());
//			}
//			
//		}
		return error;
	}
}
