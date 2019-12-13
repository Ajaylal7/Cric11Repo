//$Id$
package com.cric11.trade;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.cric11.hibernate.HibernateUtil;
import com.cric11.utility.CachedData;
import com.cric11.utility.Cric11Util;
import com.opensymphony.xwork2.ActionSupport;

public class MainClass extends ActionSupport implements ServletRequestAware,ServletResponseAware,Job {

	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	public static HashMap<String, HashMap<String,JSONObject>> dataValue = new HashMap<>();
	
	public static Timer timer = new Timer();
	
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String execute() throws IOException, SecurityException {
		String responseObj = "";
		String timeInterval[] = new String[] {"09:15","09:20","09:25","09:30","09:35","09:40","09:45","09:50","09:55","10:00",
				"10:15","10:20","10:25","10:30","10:35","10:40","10:45","10:50","10:55","11:00",
				"11:15","11:20","11:25","11:30","11:35","11:40","11:45","11:50","11:55","12:00",
				"12:15","12:20","12:25","12:30","12:35","12:40","12:45","12:50","12:55","13:00",
				"13:15","13:20","13:25","13:30","13:35","13:40","13:45","13:50","13:55","14:00",
				"14:15","14:20","14:25","14:30","14:35","14:40","14:45","14:50","14:55","15:00",
				"15:15","15:20","15:25","15:30"};
		
		try {
		String dateInput = "2019-10-07 05:55"; 
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
	    formatter.setTimeZone(TimeZone.getTimeZone("America/New_York"));
	    Date date1 = formatter.parse(dateInput);
	    
	    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
	    
	    String vhdj = formatter.format(date1);
	    String a= vhdj;
		}
		catch (Exception ex) {
			
		}
	    
	    
	

		LinkedHashMap<String, String> paramQuery = MainConstant.queryMap;
		paramQuery.put("time", String.valueOf(System.currentTimeMillis()));
		LinkedHashMap<String , String> header = new LinkedHashMap<>();
		header.put("Content-Type", "application/x-www-form-urlencoded");
		header.put("Referer", "https://www.nseindia.com/ChartApp/install/charts/mainpage.jsp");
		System.out.println("Length = "+MainConstant.stocks.length);
		JSONObject respObj = new JSONObject(); 
		String time = null,date = null;
		for(String Stocks : MainConstant.stocks) {
			paramQuery.put("CDSymbol", Stocks);
			responseObj += Stocks + "\n";
			responseObj += Cric11Util.getResponsefromUrl(MainConstant.url, "post", header , paramQuery, null)+"\n\n";
			
			String[] lastThreeCandles = responseObj.split("~");
			
			Integer hjb = lastThreeCandles.length-1;
			String current = null, previous = null;
			Boolean checkContinuity = true;
			
			for(int index = hjb; index > hjb-3	; index--) {
				String dataValue = lastThreeCandles[index];
				if(index == hjb) {
					date = dataValue.substring(0,dataValue.indexOf(" "));
					time = dataValue.substring(dataValue.indexOf(" ")+1, dataValue.indexOf("|"));
				}
				dataValue = dataValue.substring(dataValue.indexOf("|")+1, dataValue.length());
					if(!dataValue.contains("date")) {
						String[] datas = dataValue.split("\\|");
						Double[] correctData = new Double[2];
						correctData[0] = Double.parseDouble(datas[0]);
						correctData[1] = Double.parseDouble(datas[3]);
						if(correctData[0] > correctData[1]) {
							current = "red";
						}
						else if(correctData[0] < correctData[1]) {
							current = "green";
						}
						
						if(current!= null && previous!=null && current != previous ) {
							checkContinuity = false;
							break;
						}
						else {
							previous = current;
						}
					}
					else {
						checkContinuity = false;
						break;
					}
			}
			if(checkContinuity) {
				if(respObj.has("Green") && current.equals("green")) {
					respObj.getJSONArray("Green").put(Stocks);
				}
				else if(respObj.has("Red") && current.equals("red")) {
					respObj.getJSONArray("Red").put(Stocks);
				}
				else if(current.equals("green")) {
					respObj.put("Green", new JSONArray());
					respObj.getJSONArray("Green").put(Stocks);
				}
				else if(current.equals("red")) {
					respObj.put("Red", new JSONArray());
					respObj.getJSONArray("Red").put(Stocks);
				}
			}
		}
		StockData stock = new StockData();
		stock.setDate(date);
		stock.setTime(time);
		stock.setStockData(respObj.toString());
		Session session = CachedData.sessionFactory.openSession();
		session.save(stock);
		session.beginTransaction();
		Transaction transaction = session.getTransaction();
		transaction.commit();
		response.getWriter().write(respObj.toString());

		return null;
	}
	
	
	public String checkInv() {
		String url = "https://in.investing.com/common/modules/js_instrument_chart/api/data.php?pair_id=18470&pair_id_for_news=18470&chart_type=candlestick&pair_interval=300&candle_count=20&events=yes&volume_series=yes&period=";
		HashMap<String, String> header = new HashMap<>();
		header.put("Host", "in.investing.com");
		header.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
		header.put("Sec-Fetch-Mode", "cors");
		header.put("Accept", "application/json, text/javascript, */*; q=0.01");
		header.put("Sec-Fetch-Site", "same-origin");
		header.put("Referer", "https://in.investing.com/");
		header.put("X-Requested-With", "XMLHttpRequest");
		for(int index = 0; index<100; index++) {
		String resp = Cric11Util.getResponsefromUrl(url, "get", header, null, null);
		JSONObject respObj = new JSONObject(resp);
		if(respObj.has("candles")) {
			System.out.println(respObj.getJSONArray("candles").length());
		}
		}
		
		return null;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {	
			TimerTask task = new TimerTask() {
				
				@Override
				public void run() {
					try {
						execute();
						//System.out.println("Hai");
					}
					catch (Exception ex) {
						System.out.print(ex);
					}
				}
			};
			
			timer.schedule(task, 0, 60000*5);
			
		}
		catch (Exception ex) {
			System.out.println(ex);
		}
	}

}
