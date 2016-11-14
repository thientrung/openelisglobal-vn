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
 * Copyright (C) ITECH, University of Washington, Seattle WA.  All Rights Reserved.
 *
 */
package us.mn.state.health.lims.dataexchange.order.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import us.mn.state.health.lims.common.log.LogEvent;

public class OrderRawServlet extends HttpServlet {
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//		LogEvent.logFatal("IndicatorAggregationReportingServlet", "size", String.valueOf(request.getContentLength()));
		String info = "\ncharacterEncoding: " + request.getCharacterEncoding() + 
				"\ncontentLength: " + request.getContentLength() +
				"\ncontentType: " + request.getContentType() + "\n\n";
				
		String sentIndicators = getDocument( request.getInputStream(), request.getContentLength());
		
		//System.out.println(info);
		//System.out.println(sentIndicators);
		
		LogEvent.logFatal("OrderRawServletServlet", "raw", info + sentIndicators);

		response.setStatus(HttpServletResponse.SC_OK);
	}

	private String getDocument(ServletInputStream inputStream, int contentLength) {
		int charCount = 0;
		byte[] byteBuffer = new byte[contentLength];

		while( true){
			try {
				int readLength = inputStream.readLine(byteBuffer, charCount, 1024);
				
				if( readLength == -1){
					return new String(byteBuffer);
				}else{
					charCount += readLength;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} 
		}
	}

	private static final long serialVersionUID = 1L;

}
