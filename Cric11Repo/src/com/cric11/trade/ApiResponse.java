//$Id$
package com.cric11.trade;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cric11.hibernate.HibernateUtil;
import com.cric11.sample.Sample;
import com.opensymphony.xwork2.ActionSupport;

public class ApiResponse extends ActionSupport implements ServletRequestAware,ServletResponseAware {
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String execute() {
		try {
			String date = request.getParameter("date");
			String type = request.getParameter("type");
			Class<?> className = null;
			switch (type) {
			case "1":
				className = StockDataEco.class;
				break;
			case "2":
				className = StockDataEcoRise.class;
				break;
			case "3":
				className = StockDataEcoLosers.class;
				break;
			case "4":
				className = StockDataEcoGainers.class;
				break;
			case "5":
				className = HighBreaker.class;
				break;
			case "6":
				className = LowComer.class;
				break;
			case "7":
				className = NiftyAnalysis.class;
				break;
			case "8":
				className = PreviousDayHighBreaker.class;
				break;
			case "9":
				className = PreviousDayLowBreaker.class;
				break;
			}
			JSONArray respArray = new JSONArray();
			List<?> data = HibernateUtil.getRowsByCriteria(className, "date", date) ;
			Iterator<?> itr = data.iterator();
			while (itr.hasNext()) {
				JSONObject respObj = new JSONObject();
				Object stock  = itr.next();
				switch (type) {
				case "1":
					StockDataEco stockObj = (StockDataEco) stock;
					respObj.put(stockObj.getTime(), new JSONArray(stockObj.getStockData()));
					break;
				case "2":
					StockDataEcoRise stockObj1 = (StockDataEcoRise) stock;
					respObj.put(stockObj1.getTime(), new JSONArray(stockObj1.getStockData()));
					break;
				case "3":
					StockDataEcoLosers stockObj2 = (StockDataEcoLosers) stock;
					respObj.put(stockObj2.getTime(), new JSONArray(stockObj2.getStockData()));
					break;
				case "4":
					StockDataEcoGainers stockObj3 = (StockDataEcoGainers) stock;
					respObj.put(stockObj3.getTime(), new JSONArray(stockObj3.getStockData()));
					break;
				case "5":
					HighBreaker stockObj4 = (HighBreaker) stock;
					respObj.put(stockObj4.getTime(), new JSONArray(stockObj4.getStockData()));
					break;
				case "6":
					LowComer stockObj5 = (LowComer) stock;
					respObj.put(stockObj5.getTime(), new JSONArray(stockObj5.getStockData()));
					break;
				case "7":
					NiftyAnalysis stockObj6 = (NiftyAnalysis) stock;
					respObj.put(stockObj6.getTime(), new JSONArray(stockObj6.getStockData()));
					break;
				case "8":
					PreviousDayHighBreaker stockObj7 = (PreviousDayHighBreaker) stock;
					respObj.put(stockObj7.getTime(), new JSONArray(stockObj7.getStockData()));
					break;
				case "9":
					PreviousDayLowBreaker stockObj8 = (PreviousDayLowBreaker) stock;
					respObj.put(stockObj8.getTime(), new JSONArray(stockObj8.getStockData()));
					break;
				}
				response.setHeader("Access-Control-Allow-Origin", "*");
				respArray.put(respObj);
			}
			response.getWriter().write(respArray.toString());
		}
		catch (Exception ex) {
			
		}
		return null;
	}

}
