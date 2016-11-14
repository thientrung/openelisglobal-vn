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
package us.mn.state.health.lims.common.provider.query;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.validator.GenericValidator;

import us.mn.state.health.lims.common.services.DisplayListService;
import us.mn.state.health.lims.common.services.TestService;
import us.mn.state.health.lims.common.util.IdValuePair;
import us.mn.state.health.lims.common.util.XMLUtil;
import us.mn.state.health.lims.panel.dao.PanelDAO;
import us.mn.state.health.lims.panel.daoimpl.PanelDAOImpl;
import us.mn.state.health.lims.panel.valueholder.Panel;
import us.mn.state.health.lims.panelitem.dao.PanelItemDAO;
import us.mn.state.health.lims.panelitem.daoimpl.PanelItemDAOImpl;
import us.mn.state.health.lims.panelitem.valueholder.PanelItem;
import us.mn.state.health.lims.systemusersection.valueholder.SystemUserSection;
import us.mn.state.health.lims.test.dao.TestDAO;
import us.mn.state.health.lims.test.daoimpl.TestDAOImpl;
import us.mn.state.health.lims.test.daoimpl.TestSectionDAOImpl;
import us.mn.state.health.lims.test.valueholder.Test;
import us.mn.state.health.lims.testdictionary.daoimpl.TestDictionaryDAOImpl;
import us.mn.state.health.lims.testdictionary.valueholder.TestDictionary;
import us.mn.state.health.lims.typeofsample.dao.TypeOfSamplePanelDAO;
import us.mn.state.health.lims.typeofsample.daoimpl.TypeOfSamplePanelDAOImpl;
import us.mn.state.health.lims.typeofsample.util.TypeOfSampleUtil;
import us.mn.state.health.lims.typeofsample.valueholder.TypeOfSamplePanel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

public class SampleEntryTestsForTypeProvider extends BaseQueryProvider{
	private TestDAO testDAO = new TestDAOImpl();
	private PanelDAO panelDAO = new PanelDAOImpl();
	private static final String USER_TEST_SECTION_ID;
    private static final String VARIABLE_SAMPLE_TYPE_ID;
    private boolean isVariableTypeOfSample;

	static{
		USER_TEST_SECTION_ID = new TestSectionDAOImpl().getTestSectionByName("user").getId();
        VARIABLE_SAMPLE_TYPE_ID = TypeOfSampleUtil.getTypeOfSampleIdForLocalAbbreviation( "Variable" );
	}

	@SuppressWarnings("unchecked")
    @Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
	    Set<SystemUserSection> userSection =(Set<SystemUserSection>) request.getSession().getAttribute("userSection");
		String sampleType = request.getParameter("sampleType");
        isVariableTypeOfSample =  VARIABLE_SAMPLE_TYPE_ID.equals( sampleType );
        StringBuilder xml = new StringBuilder();

		String result = createSearchResultXML(sampleType, xml,userSection);

		ajaxServlet.sendData(xml.toString(), result, request, response);

	}

	private String createSearchResultXML(String sampleType, StringBuilder xml,Set<SystemUserSection> userSection){

		String success = VALID;
		//Added by Dung 2016-07-25
		//get all test lists of system user section
		List<Integer> testIds=new ArrayList<Integer>();
		for (SystemUserSection session : userSection) {
            if(session.getHasView().equals("Y")){
                List<Test> test=new ArrayList<Test>();
                test=testDAO.getTestBySectionId(Integer.parseInt(session.getTestSection().getId()));
                for (Test itest : test) {
                    testIds.add(Integer.parseInt(itest.getId()));
                }
            }
        }
		List<Test> tests=new ArrayList<Test>();
		
		tests = TypeOfSampleUtil.getTestListBySampleTypeId(sampleType, true,testIds);

		Collections.sort(tests, new Comparator<Test>(){
			@Override
			public int compare(Test t1, Test t2){
				if(GenericValidator.isBlankOrNull(t1.getSortOrder()) || GenericValidator.isBlankOrNull(t2.getSortOrder())){
					return TestService.getUserLocalizedTestName( t1 ).compareTo(TestService.getUserLocalizedTestName( t2 ));
				}

				try{
					int t1Sort = Integer.parseInt(t1.getSortOrder());
					int t2Sort = Integer.parseInt(t2.getSortOrder());

					if(t1Sort > t2Sort){
						return 1;
					}else if(t1Sort < t2Sort){
						return -1;
					}else{
						return 0;
					}

				}catch(NumberFormatException e){
                    return TestService.getUserLocalizedTestName( t1 ).compareTo(TestService.getUserLocalizedTestName( t2 ));
				}

			}
		});

        if( isVariableTypeOfSample){
            xml.append( "<variableSampleType/>" );
        }
		addTests(tests, xml);

		List<TypeOfSamplePanel> panelList = getPanelList(sampleType);
		List<PanelTestMap> panelMap = linkTestsToPanels(panelList, tests);

		addPanels(panelMap, xml);

		return success;
	}

	private void addTests(List<Test> tests, StringBuilder xml){
		xml.append("<tests>");
		for(Test test : tests){
			addTest(test, xml);
		}

		xml.append("</tests>");
	}

	private void addTest(Test test, StringBuilder xml){
		xml.append("<test>");
		XMLUtil.appendKeyValue("name", StringEscapeUtils.escapeXml( TestService.getUserLocalizedTestName( test )), xml);
		XMLUtil.appendKeyValue("id", test.getId(), xml);
		XMLUtil.appendKeyValue("userBenchChoice", String.valueOf(USER_TEST_SECTION_ID.equals(test.getTestSection().getId())), xml);
        if( isVariableTypeOfSample){
            addVariableSampleTypes( test, xml);
        }
		xml.append("</test>");
	}

    private void addVariableSampleTypes( Test test, StringBuilder xml ){
        TestDictionary testDictionary = new TestDictionaryDAOImpl().getTestDictionaryForTestId( test.getId() );
        List<IdValuePair> pairs = DisplayListService.getDictionaryListByCategory( testDictionary.getDictionaryCategory().getCategoryName() );
        xml.append( "<variableSampleTypes " );
        if( !GenericValidator.isBlankOrNull( testDictionary.getQualifiableDictionaryId() )){
             XMLUtil.appendKeyValueAttribute( "qualifiableId", testDictionary.getQualifiableDictionaryId(), xml );
        }
        xml.append( " >" );
        for(IdValuePair pair : pairs){
            xml.append( "<type " );
            XMLUtil.appendKeyValueAttribute( "id", pair.getId(), xml );
            XMLUtil.appendKeyValueAttribute( "name", pair.getValue(), xml );
            xml.append( " />" );
        }
        xml.append( "</variableSampleTypes>" );
    }



    private void addPanels(List<PanelTestMap> panelMap, StringBuilder xml){
		panelMap = sortPanels(panelMap);

		xml.append("<panels>");
		for(PanelTestMap testMap : panelMap){
			addPanel(testMap, xml);
		}
		xml.append("</panels>");
	}

	private List<PanelTestMap> sortPanels(List<PanelTestMap> panelMap){

		Collections.sort(panelMap, new Comparator<PanelTestMap>(){
			@Override
			public int compare(PanelTestMap o1, PanelTestMap o2){
				return o1.getPanelOrder() - o2.getPanelOrder();
			}
		});

		return panelMap;
	}

	private void addPanel(PanelTestMap testMap, StringBuilder xml){
		xml.append("<panel>");
		XMLUtil.appendKeyValue("name", testMap.getName(), xml);
		XMLUtil.appendKeyValue("id", testMap.getPanelId(), xml);
		XMLUtil.appendKeyValue("testMap", testMap.getTestMaps(), xml);
		xml.append("</panel>");
	}

	private List<TypeOfSamplePanel> getPanelList(String sampleType){
		TypeOfSamplePanelDAO samplePanelDAO = new TypeOfSamplePanelDAOImpl();
		return samplePanelDAO.getTypeOfSamplePanelsForSampleType(sampleType);
	}

	private List<PanelTestMap> linkTestsToPanels(List<TypeOfSamplePanel> panelList, List<Test> tests){
		List<PanelTestMap> selected = new ArrayList<PanelTestMap>();

		Map<String, Integer> testNameOrderMap = new HashMap<String, Integer>();

		for(int i = 0; i < tests.size(); i++){
			testNameOrderMap.put(TestService.getUserLocalizedTestName( tests.get( i ) ), new Integer(i));
		}

		PanelItemDAO panelItemDAO = new PanelItemDAOImpl();

		for(TypeOfSamplePanel samplePanel : panelList){
			Panel panel = panelDAO.getPanelById(samplePanel.getPanelId());
			if("Y".equals(panel.getIsActive())){
				String matchTests = getTestIndexesForPanels(samplePanel.getPanelId(), testNameOrderMap, panelItemDAO);
				if(!GenericValidator.isBlankOrNull(matchTests)){
					/*int panelOrder = panelDAO.getPanelById(samplePanel.getPanelId()).getSortOrderInt();
					selected.add(new PanelTestMap(samplePanel.getPanelId(), panelOrder, panel.getPanelName(), matchTests));*/
					String panelOrder = panelDAO.getPanelById(samplePanel.getPanelId()).getSortOrder();
					selected.add(new PanelTestMap(samplePanel.getPanelId(), Integer.valueOf(panelOrder), panel.getPanelName(), matchTests));
				}
			}
		}

		return selected;
	}

	@SuppressWarnings("unchecked")
	private String getTestIndexesForPanels(String panelId, Map<String, Integer> testIdOrderMap, PanelItemDAO panelItemDAO){
		StringBuilder indexes = new StringBuilder();
		List<PanelItem> items = panelItemDAO.getPanelItemsForPanel(panelId);

		for(PanelItem item : items){
			String derivedNameFromPanel = getDerivedNameFromPanel(item);
			if(derivedNameFromPanel != null){
				Integer index = testIdOrderMap.get(derivedNameFromPanel);

				if(index != null){
					indexes.append(index.toString());
					indexes.append(",");
				}
			}
		}

		String withExtraComma = indexes.toString();
		return withExtraComma.length() > 0 ? withExtraComma.substring(0, withExtraComma.length() - 1) : "";
	}

	private String getDerivedNameFromPanel(PanelItem item){
		//This cover the transition in the DB between the panel_item being linked by name
		// to being linked by id
		if(item.getTest() != null && item.getTest().getId() != null){
			Test test = testDAO.getTestById(item.getTest().getId());
			if(test != null){
				return TestService.getUserLocalizedTestName( test );
			}
		}else{
			return item.getTestName();
		}

		return null;
	}

	public class PanelTestMap{
		private String name;
		private String testMaps;
		private String panelId;
		private int panelOrder;

		public PanelTestMap(String panelId, int panelOrder, String panelName, String map){
			name = panelName;
			testMaps = map;
			this.panelId = panelId;
			this.panelOrder = panelOrder;
		}

		public String getName(){
			return name;
		}

		public String getTestMaps(){
			return testMaps;
		}

		public String getPanelId(){
			return panelId;
		}

		public int getPanelOrder(){
			return panelOrder;
		}
	}

}
