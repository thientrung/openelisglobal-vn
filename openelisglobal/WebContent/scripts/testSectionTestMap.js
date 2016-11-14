var testSectionTestMap = {};
var message = "";

// Maps the testSection and tests 
function /*void*/ mapTestSectionTest() {
	var testSectionId = 0; 	//e.g. testSectionId = 7 (set testSectionId to get all tests for the specified test section)
    new Ajax.Request (
           'ajaxXML',  // url
           {  // options
              method: 'get', // http method
              parameters: 'provider=TestSectionTestMapProvider&testSectionId=' + testSectionId,  // request parameters
              //indicator: 'throbbing'
              onSuccess:  processTestSectionTestMapSuccess,
              onFailure:  processFailure
           }
    );
}

//ajax success call
function /*void*/ processTestSectionTestMapSuccess(xhr) {
	message = xhr.responseXML.getElementsByTagName("message").item(0).firstChild.nodeValue;
	testSectionTestMap = JSON.parse(message);
	//alert("message: " + message);
}

//ajax failed call
function /*void*/ processFailure(xhr) {
	alert("Failed to load tests of the test section.");
}

// Rebuild the testsection-test option fields
function /*void*/ updateTestSectionTestOptions(testSectionId, testId) {
	var optList = "";
	if ($jq("#" + testSectionId).val() != 0 && $jq.isArray(testSectionTestMap[$jq("#" + testSectionId + " option:selected").val()])) {
		// Build list from array in the map
		var testsToShow = [];
		$jq(testSectionTestMap[$jq("#" + testSectionId + " option:selected").val()]).each(function(id, value){
			testsToShow.push(value);
		});
		if (originalTestList != null)
			$jq(originalTestList).each(function(){
				if ($jq(this).val() == 0 || $jq.inArray($jq(this).text(), testsToShow) > -1) {
					optList += '<option value="' + $jq(this).val() + '">' + $jq(this).text() + '</option>';
				}
			});
	} else if (originalTestList != null) {
		// Restore original list of all tests
		$jq(originalTestList).each(function(){
			optList += '<option value="' + $jq(this).val() + '">' + $jq(this).text() + '</option>';
		});
	}
	if (optList.length > 0)
		$jq("#" + testId).empty().append(optList);
}
