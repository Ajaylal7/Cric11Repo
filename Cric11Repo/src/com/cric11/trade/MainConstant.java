//$Id$
package com.cric11.trade;

import java.util.LinkedHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class MainConstant implements Job {

	public static String url = "https://www.nseindia.com/ChartApp/install/charts/data/GetHistoricalNew.jsp";
	
	public static String params = "Instrument=FUTSTK&CDSymbol={Symbol}&Segment=CM&Series=EQ&CDExpiryMonth=1&FOExpiryMonth=1&IRFExpiryMonth=30-09-2020&CDIntraExpiryMonth=29-10-2019&FOIntraExpiryMonth=31-10-2019&IRFIntraExpiryMonth=&CDDate1=05-10-2016&CDDate2=05-10-2019&PeriodType=2&Periodicity=2&ct0=g1|1|1&ct1=g2|2|1&ctcount=2";
	
	public static String[] stocks = new String[] {"SUNPHARMA","VEDL","ONGC","YESBANK","GAIL","BPCL","COALINDIA","IOC","WIPRO","ZEEL","GRASIM","JSWSTEEL","TECHM","NTPC","ICICIBANK","TATAMOTORS","TATASTEEL","UPL","AXISBANK","CIPLA","HINDALCO","M%26M","INFRATEL","POWERGRID","ITC","BHARTIARTL","SBIN","ADANIPORTS","INFY","HCLTECH","HEROMOTOCO","TCS","INDUSINDBK","HDFC","HINDUNILVR","BAJFINANCE","ASIANPAINT","NESTLEIND","MARUTI","EICHERMOT","BAJAJFINSV","BRITANNIA","LT","DRREDDY","HDFCBANK","KOTAKBANK","TITAN","ULTRACEMCO"}; 
	
	public static LinkedHashMap<String,String> queryMap = null;
	
	static {
		queryMap = new LinkedHashMap<>();
		String [] paramArray = params.split("&");
		try {
		for(String param : paramArray) {
			String array[] = param.split("=");
			queryMap.put(array[0], array.length<2 ? "" : array[1]);
		}
		}
		catch (Exception ex) {
			System.out.print(ex);
		}
	}
	
	public static String getMainREsponse() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			HttpPost httpPost = new HttpPost("https://www.nseindia.com/ChartApp/install/charts/data/GetHistoricalNew.jsp");
			
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			
			
			StringEntity entity = new StringEntity("Instrument=FUTSTK&CDSymbol=BPCL&Segment=CM&Series=EQ&CDExpiryMonth=1&FOExpiryMonth=1&IRFExpiryMonth=30-09-2020&CDIntraExpiryMonth=29-10-2019&FOIntraExpiryMonth=31-10-2019&IRFIntraExpiryMonth=&CDDate1=05-10-2016&CDDate2=05-10-2019&PeriodType=2&Periodicity=2&ct0=g1|1|1&ct1=g2|2|1&ctcount=2&time=1570290490976");
			
			
			httpPost.setEntity(entity);
			
			HttpResponse response = httpClient.execute(httpPost);

			
	        int statusCode = response.getStatusLine().getStatusCode();
	        
	        HttpEntity httpEntity = response.getEntity();
	        String apiOutput = EntityUtils.toString(httpEntity);
	        
	        httpClient.getConnectionManager().shutdown();

		}
		catch (Exception ex) {
			System.out.print(ex);
		}
		return null;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		MainClass.timer.cancel();
	}
	
}
