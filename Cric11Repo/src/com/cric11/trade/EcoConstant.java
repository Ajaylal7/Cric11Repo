//$Id$
package com.cric11.trade;

public class EcoConstant {

	public static String fallurl = "https://etmarketsapis.indiatimes.com/ET_Stats/fallfromhigh?pagesize=25&exchange=nse&pageno=1&service=intradayhigh&sortby=belowDaysHighPerChange&sortorder=asc&marketcap=largecap%2Cmidcap&index=2510%2C2369";
	
	public static String riseurl = "https://etmarketsapis.indiatimes.com/ET_Stats/recoveryfromlow?pageno=1&pagesize=25&sortby=aboveDaysLowPerChange&sortorder=desc&service=intradaylow&exchange=nse&marketcap=largecap%2Cmidcap&index=2510%2C2369";
	
	public static String loseurl = "https://etmarketsapis.indiatimes.com/ET_Stats/hourlylosers?pageno=1&pagesize=25&sortby=percentchange&sortorder=asc&service=losers&exchange=nse&marketcap=largecap%2Cmidcap&index=2510%2C2369";
	
	public static String gainurl = "https://etmarketsapis.indiatimes.com/ET_Stats/hourlygainers?pageno=1&pagesize=25&sortby=percentchange&sortorder=desc&service=gainers&exchange=nse&marketcap=largecap%2Cmidcap&index=2510%2C2369";
	
//	public static HashMap<String, String> header = new HashMap<>();
//
//	static {
//		
//	}
	
	
}

