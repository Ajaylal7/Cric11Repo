//$Id$
package com.cric11.trade;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.cric11.utility.CachedData;
import com.cric11.utility.Cric11Util;
import com.opensymphony.xwork2.ActionSupport;

public class EcoClass extends ActionSupport implements ServletRequestAware,ServletResponseAware,Job {
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	public static Timer timer = new Timer();

	
	public String execute() {
		try {
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");  
			LocalDateTime now = LocalDateTime.now();  
			String dateFormat = dtf.format(now);
			String date = dateFormat.split(" ")[0];
			String time = dateFormat.split(" ")[1];
			Integer hour = now.getHour();
			Integer minute = now.getMinute();
			JSONArray respObj = new JSONArray();
			String ecoUrl = EcoConstant.fallurl;
			String ecoResponse = Cric11Util.getResponsefromUrl(ecoUrl, "get", null, null, null);
			JSONArray ecoArray = new JSONObject(ecoResponse).getJSONArray("searchresult");
			for(int index = 0; index<ecoArray.length(); index++) {
				respObj.put(ecoArray.getJSONObject(index).get("companyShortName").toString());
			}
			
			JSONArray respObj1 = new JSONArray();
			String ecoUrl1 = EcoConstant.riseurl;
			String ecoResponse1 = Cric11Util.getResponsefromUrl(ecoUrl1, "get", null, null, null);
			JSONArray ecoArray1 = new JSONObject(ecoResponse1).getJSONArray("searchresult");
			for(int index = 0; index<ecoArray1.length(); index++) {
				respObj1.put(ecoArray1.getJSONObject(index).get("companyShortName").toString());
			}
			

			Integer start = hour;
			
			if(minute == 0) {
				--start;
			}
			
			Integer end = (start+1);
			
			JSONArray respObj2 = new JSONArray();
			String ecoUrl2 = EcoConstant.loseurl;
			//ecoUrl2 = ecoUrl2.replace("{start}", start+"-00").replace("{end}", end+"-00");
			String ecoResponse2 = Cric11Util.getResponsefromUrl(ecoUrl2, "get", null, null, null);
			JSONArray ecoArray2 = new JSONObject(ecoResponse2).getJSONArray("searchresult");
			for(int index = 0; index<ecoArray2.length(); index++) {
				respObj2.put(ecoArray2.getJSONObject(index).get("companyShortName").toString());
			}
			
			
			JSONArray respObj3 = new JSONArray();
			String ecoUrl3 = EcoConstant.gainurl;
			//ecoUrl3 = ecoUrl3.replace("{start}", start+"-00").replace("{end}", end+"-00");
			String ecoResponse3 = Cric11Util.getResponsefromUrl(ecoUrl3, "get", null, null, null);
			JSONArray ecoArray3 = new JSONObject(ecoResponse3).getJSONArray("searchresult");
			for(int index = 0; index<ecoArray3.length(); index++) {
				respObj3.put(ecoArray3.getJSONObject(index).get("companyShortName").toString());
			}
			
			
			StockDataEco stock = new StockDataEco();
			stock.setDate(dateFormat.split(" ")[0]);
			stock.setTime(dateFormat.split(" ")[1]);
			stock.setStockData(respObj.toString());
			
			StockDataEcoRise stockRise = new StockDataEcoRise();
			stockRise.setDate(dateFormat.split(" ")[0]);
			stockRise.setTime(dateFormat.split(" ")[1]);
			stockRise.setStockData(respObj1.toString());
			
			StockDataEcoGainers stockGain = new StockDataEcoGainers();
			stockGain.setDate(dateFormat.split(" ")[0]);
			stockGain.setTime(dateFormat.split(" ")[1]);
			stockGain.setStockData(respObj3.toString());
			
			StockDataEcoLosers stockLose = new StockDataEcoLosers();
			stockLose.setDate(dateFormat.split(" ")[0]);
			stockLose.setTime(dateFormat.split(" ")[1]);
			stockLose.setStockData(respObj2.toString());
			
			Session session = CachedData.sessionFactory.openSession();
			session.save(stock);
			session.save(stockRise);
			session.save(stockGain);
			session.save(stockLose);
			session.beginTransaction();
			Transaction transaction = session.getTransaction();
			transaction.commit();
			//response.getWriter().write(respObj.toString());
		
		}
		catch (Exception ex) {
			System.out.print(ex);
		}
		return null;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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
