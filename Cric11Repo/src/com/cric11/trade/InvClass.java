//$Id$
package com.cric11.trade;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
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

public class InvClass extends ActionSupport implements ServletRequestAware,ServletResponseAware,Job {
	
	private HttpServletRequest request,a;
	
	private HttpServletResponse response;
	
	public static Timer timer = new Timer();
	
	public static HashSet<String> highBrekers = new HashSet<>();
	
	public static HashSet<String> lowBrekers = new HashSet<>();
	
	static JSONArray highArray = new JSONArray();
	
	static JSONArray lowArray = new JSONArray();

	
	public String execute() {
		try {
			
//			for(int index = 0; index<InvConstant.stockId.length;index++) {
//				System.out.println(InvConstant.stockId[index] + "=" + InvConstant.stocks[index]);
//			}
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");  
			LocalDateTime now = LocalDateTime.now();
			String dateFormat = dtf.format(now);
			
			JSONObject respObj = new JSONObject();
			String invUrl = InvConstant.url;
			for(int j = 0; j< InvConstant.nifty50StocksId.length; j++) {
				String current = null, previous = null;
				Boolean checkContinuity = true;
				String url = invUrl.replace("{id}", InvConstant.nifty50StocksId[j].toString());
				String invRespObj = Cric11Util.getResponsefromUrl(url, "get", InvConstant.header, null, null);
				JSONObject jsonResp = new JSONObject(invRespObj);
				if(jsonResp.has("candles")) {
					JSONArray candleData = jsonResp.getJSONArray("candles");
					for(int index = 18 ; index > 15 ; index--) {
						JSONArray stockValues = candleData.getJSONArray(index);
						Double open = Double.parseDouble(stockValues.get(1).toString());
						Double close = Double.parseDouble(stockValues.get(4).toString());
						current = open > close ? "red" : "green";
						if(current!= null && previous!=null && current != previous ) {
							checkContinuity = false;
							break;
						}
						else {
							previous = current;
						}	
					}
				}
				else {
					checkContinuity = false;
				}
				
				if(checkContinuity) {
					String Stocks = jsonResp.getJSONObject("attr").get("symbol").toString();
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
			StockDataInv stock = new StockDataInv();
			stock.setDate(dateFormat.split(" ")[0]);
			stock.setTime(dateFormat.split(" ")[1]);
			stock.setStockData(respObj.toString());
			Session session = CachedData.sessionFactory.openSession();
			session.save(stock);
			session.beginTransaction();
			Transaction transaction = session.getTransaction();
			transaction.commit();
			response.getWriter().write(respObj.toString());
		
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
						prevoiusDayBreaker();
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
	
	public static String dayFirstHighBreak() {
		String firstCandleData = null;
		String currentCandleData = null;
		try {
			LocalDateTime now = LocalDateTime.now();
			String firstCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth())+" 09:15:00";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = sdf.parse(firstCandleTime);
			String firstCandlemillis = String.valueOf(date.getTime());
			String invUrl = InvConstant.url;
//			for(int i=0;i<75;i++) {
//				Date candleDate = DateUtils.addMinutes(date, 5);
//				String currentCandleMillis = String.valueOf(candleDate.getTime());
				
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
			//String dateFormat = candleDate.getDate()+"-"+candleDate.getMonth()+"-"+(candleDate.getYear()+1900)+" "+candleDate.getHours()+":"+candleDate.getMinutes();
			String dateFormat = dtf.format(now); 
			String currentCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth())+" "+now.getHour()+":"+now.getMinute()+":00";
			date = sdf.parse(currentCandleTime);
			String currentCandleMillis = String.valueOf(date.getTime());
				for(int j = 0; j< InvConstant.nifty50StocksId.length; j++) {
					String id = InvConstant.nifty50StocksId[j].toString();
					String url = invUrl.replace("{id}", id);
					String invRespObj = Cric11Util.getResponsefromUrl(url, "get", InvConstant.header, null, null);
					JSONObject jsonResp = new JSONObject(invRespObj);
					if(jsonResp.has("candles")) {
						JSONArray candleData = jsonResp.getJSONArray("candles");
						for(int index = 0 ; index<candleData.length();index++) {
							if(candleData.getJSONArray(index).get(0).toString().equals(firstCandlemillis)) {
								firstCandleData = candleData.getJSONArray(index).toString();
							}
							else if(candleData.getJSONArray(index).get(0).toString().equals(currentCandleMillis)) {
								currentCandleData = candleData.getJSONArray(index).toString();
							}
						}
						checkCandleData(firstCandleData,currentCandleData,jsonResp,dateFormat.split(" ")[0]);
					}
				}
				HighBreaker high = new HighBreaker();
				high.setDate(dateFormat.split(" ")[0]);
				high.setTime(dateFormat.split(" ")[1]);
				high.setStockData(highArray.toString());
				
				LowComer low = new LowComer();
				low.setDate(dateFormat.split(" ")[0]);
				low.setTime(dateFormat.split(" ")[1]);
				low.setStockData(lowArray.toString());
				
				Session session = CachedData.sessionFactory.openSession();
				session.save(high);
				session.save(low);
				session.beginTransaction();
				Transaction transaction = session.getTransaction();
				transaction.commit();
				System.out.println("Inserted");
				
				highArray = new JSONArray();
				lowArray = new JSONArray();
				
//				date = candleDate;
				
//			}
		}
		catch (Exception e) {
			System.out.print(e);
		}
		return null;
	}
	
	public static String dayFirstHighBreakOwn() {
		String firstCandleData = null;
		String currentCandleData = null;
		try {
			LocalDateTime now = LocalDateTime.now();
			String firstCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth())+" 09:15:00";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = sdf.parse(firstCandleTime);
			String firstCandlemillis = String.valueOf(date.getTime());
			String invUrl = InvConstant.ownurl;
			for(int i=0;i<75;i++) {
				Date candleDate = DateUtils.addMinutes(date, 5);
				String currentCandleMillis = String.valueOf(candleDate.getTime());
				
			
			//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
			String dateFormat = candleDate.getDate()+"-"+(candleDate.getMonth()+1)+"-"+(candleDate.getYear()+1900)+" "+candleDate.getHours()+":"+candleDate.getMinutes();
			//String dateFormat = dtf.format(now); 
			//String currentCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth())+" "+now.getHour()+":"+now.getMinute()+":00";
			//date = sdf.parse(dateFormat);
			//String currentCandleMillis = String.valueOf(date.getTime());
				for(int j = 0; j< InvConstant.nifty50StocksId.length; j++) {
					String id = InvConstant.nifty50StocksId[j].toString();
					String url = invUrl.replace("{id}", id);
					String invRespObj = Cric11Util.getResponsefromUrl(url, "get", InvConstant.header, null, null);
					JSONObject jsonResp = new JSONObject(invRespObj);
					if(jsonResp.has("candles")) {
						JSONArray candleData = jsonResp.getJSONArray("candles");
						for(int index = 0 ; index<candleData.length();index++) {
							if(candleData.getJSONArray(index).get(0).toString().equals(firstCandlemillis)) {
								firstCandleData = candleData.getJSONArray(index).toString();
							}
							else if(candleData.getJSONArray(index).get(0).toString().equals(currentCandleMillis)) {
								currentCandleData = candleData.getJSONArray(index).toString();
							}
						}
						checkCandleData(firstCandleData,currentCandleData,jsonResp,dateFormat.split(" ")[0]);
					}
				}
				HighBreaker high = new HighBreaker();
				high.setDate(dateFormat.split(" ")[0]);
				high.setTime(dateFormat.split(" ")[1]);
				high.setStockData(highArray.toString());
				
				LowComer low = new LowComer();
				low.setDate(dateFormat.split(" ")[0]);
				low.setTime(dateFormat.split(" ")[1]);
				low.setStockData(lowArray.toString());
				
				Session session = CachedData.sessionFactory.openSession();
				session.save(high);
				session.save(low);
				session.beginTransaction();
				Transaction transaction = session.getTransaction();
				transaction.commit();
				System.out.println("Inserted");
				
				highArray = new JSONArray();
				lowArray = new JSONArray();
				
				date = candleDate;
				
			}
		}
		catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return null;
	}
	
	public static String dayFirstHighBreakOwnNifty() {
		String firstCandleData = null;
		String currentCandleData = null;
		try {
			LocalDateTime now = LocalDateTime.now();
			String previousCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth()-2)+" 05:30:00";
			String firstCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth()-2)+" 09:15:00";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = sdf.parse(firstCandleTime);
//			String firstCandlemillis = String.valueOf(date.getTime());
			String invUrl = InvConstant.ownurl;
			for(int i=0;i<75;i++) {
				Date candleDate = DateUtils.addMinutes(date, 5);
				String currentCandleMillis = String.valueOf(candleDate.getTime());
				
			
			//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
			String dateFormat = candleDate.getDate()+"-"+(candleDate.getMonth()+1)+"-"+(candleDate.getYear()+1900)+" "+candleDate.getHours()+":"+candleDate.getMinutes();
			//String dateFormat = dtf.format(now); 
			//String currentCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth())+" "+now.getHour()+":"+now.getMinute()+":00";
			//date = sdf.parse(dateFormat);
			//String currentCandleMillis = String.valueOf(date.getTime());
				for(int j = 0; j< InvConstant.nifty50StocksId.length; j++) {
					String id = InvConstant.nifty50StocksId[j].toString();
					String url = invUrl.replace("{id}", id);
					String invRespObj = Cric11Util.getResponsefromUrl(url, "get", InvConstant.header, null, null);
					JSONObject jsonResp = new JSONObject(invRespObj);
					if(jsonResp.has("candles")) {
						JSONArray candleData = jsonResp.getJSONArray("candles");
						for(int index = 0 ; index<candleData.length();index++) {
//							if(candleData.getJSONArray(index).get(0).toString().equals(firstCandlemillis)) {
//								firstCandleData = candleData.getJSONArray(index).toString();
//							}
							 if(candleData.getJSONArray(index).get(0).toString().equals(currentCandleMillis)) {
								currentCandleData = candleData.getJSONArray(index).toString();
							}
						}
						//checkCandleData(firstCandleData,currentCandleData,jsonResp,dateFormat.split(" ")[0]);
						checkHighAndLow(currentCandleData,jsonResp);
					}
				}
				highArray.put(0,"Green " + highArray.length()+"\n");
				
				highArray.put(highArray.length()-1,"Red "+lowArray.length()+"\n");
				
				for(int index = 0; index<lowArray.length();index++) {
					highArray.put(lowArray.get(index));
				}
				
				NiftyAnalysis high = new NiftyAnalysis();
				high.setDate(dateFormat.split(" ")[0]);
				high.setTime(dateFormat.split(" ")[1]);
				high.setStockData(highArray.toString());
				
				
				Session session = CachedData.sessionFactory.openSession();
				session.save(high);
				session.beginTransaction();
				Transaction transaction = session.getTransaction();
				transaction.commit();
				System.out.println("Inserted");
				
				highArray = new JSONArray();
				lowArray = new JSONArray();
				
				date = candleDate;
				
			}
		}
		catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return null;
	}
	
	public static String prevoiusDayBreaker() {
		String firstCandleData = null,a;
		String currentCandleData = null;
		try {
			int minusDate = 1;
			LocalDateTime now = LocalDateTime.now();
			String firstCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth())+" 09:15:00";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			Date date = sdf.parse(firstCandleTime);
//			String firstCandlemillis = String.valueOf(date.getTime());
			String invUrl = InvConstant.ownurl;
			//for(int i=0;i<75;i++) 
			{
//				Date candleDate = date;
//				String currentCandleMillis = String.valueOf(candleDate.getTime());
				String currentCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth())+" "+now.getHour()+":"+now.getMinute()+":00";
				date = sdf.parse(currentCandleTime);
				String currentCandleMillis = String.valueOf(date.getTime());
				Date candleDate = date;
				if(candleDate.getDay() == 2) {
					minusDate = 3;
				}
 
			
			//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
			String dateFormat = candleDate.getDate()+"-"+(candleDate.getMonth()+1)+"-"+(candleDate.getYear()+1900)+" "+candleDate.getHours()+":"+candleDate.getMinutes();
			//String dateFormat = dtf.format(now); 
			//String currentCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth())+" "+now.getHour()+":"+now.getMinute()+":00";
			//date = sdf.parse(dateFormat);
			//String currentCandleMillis = String.valueOf(date.getTime());
				for(int j = 0; j< InvConstant.nifty50StocksId.length; j++) {
					String id = InvConstant.nifty50StocksId[j].toString();
					 {
						String previousCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth()-minusDate)+" 05:30:00";
						Date previosdate = sdf.parse(previousCandleTime);
						String firstCandlemillis = String.valueOf(previosdate.getTime());
						String dayUrl = InvConstant.oneDayUrl;
						String previousDayUrl = dayUrl.replace("{id}", id);
						String invRespObj = Cric11Util.getResponsefromUrl(previousDayUrl, "get", InvConstant.header, null, null);
						JSONObject jsonResp = new JSONObject(invRespObj);
						if(jsonResp.has("candles")) {
							JSONArray candleData = jsonResp.getJSONArray("candles");
							for(int index = 0 ; index<candleData.length();index++) {
								if(candleData.getJSONArray(index).get(0).toString().equals(firstCandlemillis)) {
									firstCandleData = candleData.getJSONArray(index).toString();
								}
							}
						}
					}
					
					
					
					String url = invUrl.replace("{id}", id);
					String invRespObj = Cric11Util.getResponsefromUrl(url, "get", InvConstant.header, null, null);
					JSONObject jsonResp = new JSONObject(invRespObj);
					if(jsonResp.has("candles")) {
						JSONArray candleData = jsonResp.getJSONArray("candles");
						for(int index = 0 ; index<candleData.length();index++) {
//							if(candleData.getJSONArray(index).get(0).toString().equals(firstCandlemillis)) {
//								firstCandleData = candleData.getJSONArray(index).toString();
//							}
							 if(candleData.getJSONArray(index).get(0).toString().equals(currentCandleMillis)) {
								currentCandleData = candleData.getJSONArray(index).toString();
							}
						}
						//checkCandleData(firstCandleData,currentCandleData,jsonResp,dateFormat.split(" ")[0]);
						checkHighAndLow(firstCandleData,currentCandleData,jsonResp,dateFormat.split(" ")[0]);
					}
				}
				PreviousDayHighBreaker high = new PreviousDayHighBreaker();
				high.setDate(dateFormat.split(" ")[0]);
				high.setTime(dateFormat.split(" ")[1]);
				high.setStockData(highArray.toString());
				
				PreviousDayLowBreaker low = new PreviousDayLowBreaker();
				low.setDate(dateFormat.split(" ")[0]);
				low.setTime(dateFormat.split(" ")[1]);
				low.setStockData(lowArray.toString());
				
				Session session = CachedData.sessionFactory.openSession();
				session.save(high);
				session.save(low);
				session.beginTransaction();
				Transaction transaction = session.getTransaction();
				transaction.commit();
				System.out.println("Inserted");
				
				highArray = new JSONArray();
				lowArray = new JSONArray();
				firstCandleData = null;
//				candleDate =DateUtils.addMinutes(date, 5);
//				date = candleDate;
				
			}
			
		}
		catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return null;
	}
	
	public static String prevoiusDayBreakerOwn() {
		String firstCandleData = null;
		String currentCandleData = null;
		try {
			int minusDate = 1;
			LocalDateTime now = LocalDateTime.now();
			String firstCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth())+" 09:15:00";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			
			Date date = sdf.parse(firstCandleTime);
//			String firstCandlemillis = String.valueOf(date.getTime());
			String invUrl = InvConstant.ownurl;
			Integer day = date.getDay();
			if(date.getDay() == 1) {
				minusDate = 3;
			}
			for(int i=0;i<75;i++) 
			{
				Date candleDate = date;
				String currentCandleMillis = String.valueOf(candleDate.getTime());
				//String currentCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth())+" "+now.getHour()+":"+now.getMinute()+":00";
				//date = sdf.parse(currentCandleTime);
//				String currentCandleMillis = String.valueOf(date.getTime());
//				Date candleDate = date;
				
// 
			
			//DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
			String dateFormat = candleDate.getDate()+"-"+(candleDate.getMonth()+1)+"-"+(candleDate.getYear()+1900)+" "+candleDate.getHours()+":"+candleDate.getMinutes();
			//String dateFormat = dtf.format(now); 
			//String currentCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth())+" "+now.getHour()+":"+now.getMinute()+":00";
			//date = sdf.parse(dateFormat);
			//String currentCandleMillis = String.valueOf(date.getTime());
				for(int j = 0; j< InvConstant.nifty50StocksId.length; j++) {
					String id = InvConstant.nifty50StocksId[j].toString();
					 {
						String previousCandleTime = now.getYear()+"/"+now.getMonthValue()+"/"+(now.getDayOfMonth()-minusDate)+" 05:30:00";
						Date previosdate = sdf.parse(previousCandleTime);
						String firstCandlemillis = String.valueOf(previosdate.getTime());
						String dayUrl = InvConstant.oneDayUrl;
						String previousDayUrl = dayUrl.replace("{id}", id);
						String invRespObj = Cric11Util.getResponsefromUrl(previousDayUrl, "get", InvConstant.header, null, null);
						JSONObject jsonResp = new JSONObject(invRespObj);
						if(jsonResp.has("candles")) {
							JSONArray candleData = jsonResp.getJSONArray("candles");
							for(int index = 0 ; index<candleData.length();index++) {
								if(candleData.getJSONArray(index).get(0).toString().equals(firstCandlemillis)) {
									firstCandleData = candleData.getJSONArray(index).toString();
								}
							}
						}
					}
					
					
					
					String url = invUrl.replace("{id}", id);
					String invRespObj = Cric11Util.getResponsefromUrl(url, "get", InvConstant.header, null, null);
					JSONObject jsonResp = new JSONObject(invRespObj);
					if(jsonResp.has("candles")) {
						JSONArray candleData = jsonResp.getJSONArray("candles");
						for(int index = 0 ; index<candleData.length();index++) {
//							if(candleData.getJSONArray(index).get(0).toString().equals(firstCandlemillis)) {
//								firstCandleData = candleData.getJSONArray(index).toString();
//							}
							 if(candleData.getJSONArray(index).get(0).toString().equals(currentCandleMillis)) {
								currentCandleData = candleData.getJSONArray(index).toString();
							}
						}
						//checkCandleData(firstCandleData,currentCandleData,jsonResp,dateFormat.split(" ")[0]);
						checkHighAndLow(firstCandleData,currentCandleData,jsonResp,dateFormat.split(" ")[0]);
					}
				}
				PreviousDayHighBreaker high = new PreviousDayHighBreaker();
				high.setDate(dateFormat.split(" ")[0]);
				high.setTime(dateFormat.split(" ")[1]);
				high.setStockData(highArray.toString());
				
				PreviousDayLowBreaker low = new PreviousDayLowBreaker();
				low.setDate(dateFormat.split(" ")[0]);
				low.setTime(dateFormat.split(" ")[1]);
				low.setStockData(lowArray.toString());
				
				Session session = CachedData.sessionFactory.openSession();
				session.save(high);
				session.save(low);
				session.beginTransaction();
				Transaction transaction = session.getTransaction();
				transaction.commit();
				System.out.println("Inserted");
				
				highArray = new JSONArray();
				lowArray = new JSONArray();
				firstCandleData = null;
				candleDate =DateUtils.addMinutes(date, 5);
				date = candleDate;
				
			}
			
		}
		catch (Exception e) {
			System.out.print(e.getMessage());
		}
		return null;
	}

	private static void checkHighAndLow(String firstCandleData, String currentCandleData, JSONObject stockData,String date) {
		try {
			Double previousDayHigh = Double.valueOf(firstCandleData.split(",")[2]);
			Double previousDayLow = Double.valueOf(firstCandleData.split(",")[3]);
			Double currentCandleOpen = Double.valueOf(currentCandleData.split(",")[1]);
			String stockValue = stockData.getJSONObject("attr").get("symbol").toString();
			if(stockValue.equals("Dr. Reddy’s Laboratories Ltd")) {
				stockValue = "Dr Reddy";
			}
			
			if(previousDayHigh<currentCandleOpen) {
				Integer size = HibernateUtil.getRowsByCriteriaAndContains(PreviousDayHighBreaker.class, "date",date,"stockData" ,"%"+stockValue+"%").size();
				if(size == 0) {
					highArray.put(stockValue);
				}
				
			}
			else if(previousDayLow > currentCandleOpen) {
				Integer size = HibernateUtil.getRowsByCriteriaAndContains(PreviousDayLowBreaker.class, "date",date,"stockData" ,"%"+stockValue+"%").size();
				if(size == 0) {
					lowArray.put(stockValue);
				}
			}
		}
		catch (Exception ex) {
			System.out.print(ex);
		}

	}

	private static void checkHighAndLow(String currentCandleData, JSONObject stockData) {
		try {
			Double currentCandleOpen = Double.valueOf(currentCandleData.split(",")[1]);
			Double currentCandleClose = Double.valueOf(currentCandleData.split(",")[4]);
			if(currentCandleOpen<currentCandleClose) {
				highArray.put(stockData.getJSONObject("attr").get("symbol").toString());
			}
			else {
				lowArray.put(stockData.getJSONObject("attr").get("symbol").toString());
			}
		}
		catch (Exception ex) {
			System.out.print(ex);
		}

	}

	private static void checkCandleData(String firstCandleData, String currentCandleData,JSONObject stockData,String date) {
		String a;
		try {
			Double firstCandleHigh = Double.valueOf(firstCandleData.split(",")[2]);
			Double firstCandleLow = Double.valueOf(firstCandleData.split(",")[3]);
			
			Double currentCandleOpen = Double.valueOf(currentCandleData.split(",")[1]);
			
			String stockValue = stockData.getJSONObject("attr").get("symbol").toString();
			
			if(stockValue.equals("Dr. Reddy’s Laboratories Ltd")) {
				stockValue = "Dr Reddy";
			}
			
			if(currentCandleOpen>=firstCandleHigh) {
				
				Integer size = HibernateUtil.getRowsByCriteriaAndContains(HighBreaker.class, "date",date,"stockData" ,"%"+stockValue+"%").size();
				if(size == 0) {
					highArray.put(stockValue);
				}
			}
			
			else if(currentCandleOpen<=firstCandleLow) {
				Integer size = HibernateUtil.getRowsByCriteriaAndContains(LowComer.class, "date",date,"stockData" ,"%"+stockValue+"%").size();
				if(size == 0 ) {
					lowArray.put(stockValue);
				}
			}
			
		}
		catch (Exception ex) {
			System.out.print(ex);
		}
	}

}
