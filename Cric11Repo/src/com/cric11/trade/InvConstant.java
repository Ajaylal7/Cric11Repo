//$Id$
package com.cric11.trade;

import java.util.HashMap;

import org.json.JSONObject;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.cric11.hibernate.HibernateUtil;

public class InvConstant implements Job {
	
	private static final String print = "{Number:"+HibernateUtil.getNum()+"}";

	public static String[] stocks = new String[] {"SUNPHARMA","VEDL","ONGC","YESBANK","GAIL","BPCL","COALINDIA","IOC","WIPRO","ZEEL","GRASIM","JSWSTEEL","TECHM","NTPC","ICICIBANK","TATAMOTORS","TATASTEEL","UPL","AXISBANK","CIPLA","HINDALCO","M%26M","INFRATEL","POWERGRID","ITC","BHARTIARTL","SBIN","ADANIPORTS","INFY","HCLTECH","HEROMOTOCO","TCS","INDUSINDBK","HDFC","HINDUNILVR"
			,"BAJFINANCE","ASIANPAINT","NESTLEIND","MARUTI","EICHERMOT","BAJAJFINSV","BRITANNIA","LT","DRREDDY","HDFCBANK","KOTAKBANK","TITAN","ULTRACEMCO","NBCC","PNB","RELCAPITAL","SAIL","TATAMTRDVR","UNIONBANK"}; 

	public static Integer[] stockId = new Integer[] {18405,18377,18311,18470,18137,18040,18075,18197,
			18467,18471,18156,18226,18429,18297,18198,18425,18428,18450,18017,
			18071,18187,18273,39852,18342,18224,18041,18376,18294,18217,18176,
			18179,18420,18215,18191,18185,18022,18011,30059,18277,18054,18108,18023,18268,18101,18177,18260,18433,18445,100271,18350,18361,18364,18399,947268,18447};
	
	public static String[] nifty50Stocks = new String[] {"Adani","Asian Paints","AXIS Bank"};
	
	public static Integer[] nifty50StocksId = new Integer[] {18294,18011,18017,18020,18022,18023,18040,18041,39852,18054,18071,18075,18101,18108,18137,18156,18176,18177,18179,18187,18185,18191,18198,18197,18215,18217,18224,18226,18260,18268,18273,18277,30059,18297,18311,18342,18367,18376,18405,18420,18425,18428,18429,18433,18445,18450,18377,18467,18470,18471};
	
	public static String url = "https://in.investing.com/common/modules/js_instrument_chart/api/data.php?pair_id={id}&pair_id_for_news={id}&chart_type=candlestick&pair_interval=300&candle_count=90&events=yes&volume_series=yes&period=";
	
	public static String ownurl = "https://in.investing.com/common/modules/js_instrument_chart/api/data.php?pair_id={id}&pair_id_for_news={id}&chart_type=candlestick&pair_interval=300&candle_count=400&events=yes&volume_series=yes&period=";
	
	public static String oneDayUrl = "https://in.investing.com/common/modules/js_instrument_chart/api/data.php?pair_id={id}&pair_id_for_news={id}&chart_type=candlestick&pair_interval=86400&candle_count=70&events=yes&volume_series=yes&period=";

	
	public static String analysisUrl = "https://in.investing.com/common/modules/js_instrument_chart/api/data.php?pair_id={id}&pair_id_for_news={id}&chart_type=candlestick&pair_interval=300&candle_count=78&events=yes&volume_series=yes&period=";
	
	public static HashMap<String, String> header = new HashMap<>();
	
	static {
		header.put("Host", "in.investing.com");
		header.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.90 Safari/537.36");
		header.put("Sec-Fetch-Mode", "cors");
		header.put("Accept", "application/json, text/javascript, */*; q=0.01");
		header.put("Sec-Fetch-Site", "same-origin");
		header.put("Referer", "https://in.investing.com/");
		header.put("X-Requested-With", "XMLHttpRequest");
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		InvClass.timer.cancel();
	}

	public static JSONObject getPrint() {
		return new JSONObject(print);
	}
	
}
