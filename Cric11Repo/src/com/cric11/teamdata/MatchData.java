//$Id$
package com.cric11.teamdata;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cric11.utility.CachedData;
import com.cric11.utility.Cric11Util;
import com.opensymphony.xwork2.ActionSupport;

public class MatchData extends ActionSupport implements ServletRequestAware,ServletResponseAware {
	String matchListUrl = "http://cricapi.com/api/matches";


	@Override
	public String execute() throws Exception {
		try {

			HashMap<String, String> params = new HashMap<>(),a;
			params.put("apikey", "r3vpuMv5Yke7eAxLwPXzWgePegQ2");
			
			String team11Matches = Cric11Util.getResponsefromUrl(matchListUrl, "post", null, params, null);
			
			JSONObject matchJSON = new JSONObject(team11Matches);
			
			if(matchJSON.has("matches")) {
				JSONArray matchArray = matchJSON.getJSONArray("matches");
				for(int index = 0; index<matchArray.length(); index++) {
					JSONObject matchData = matchArray.getJSONObject(index);
					if(!Boolean.valueOf(matchData.get("matchStarted").toString())) {
						Match matchObj = new Match();
						matchObj.setMatchStatus(MatchConstants.MatchStatus.FIXTURES.getStatusId());
						String team1 = matchData.get("team-1").toString();
						String team2 = matchData.get("team-2").toString();
						Boolean check = checkMatches(team1,team2);
						if(check) {
							matchObj.setTeamName1(team1);
							matchObj.setTeamName2(team2);
							String startTimeGMT = matchData.get("dateTimeGMT").toString();
							
	//						ZonedDateTime date = ZonedDateTime.
	//						String startDate = date.format(DateTimeFormatter.ISO_DATE_TIME);
							Instant instant = Instant.parse(startTimeGMT).with(ChronoField.NANO_OF_SECOND,0);
							DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
							Date date1 = iso8601.parse(instant.toString());
							Long time1 = date1.getTime();
							
							matchObj.setStartTime(time1);
							String relatedName = team1+" vs "+team2+" WorldCup "+matchData.get("type").toString();
							matchObj.setRelatedName(relatedName);
							Session session = CachedData.sessionFactory.openSession();
							
							Transaction transaction = session.beginTransaction();
							
							session.save(matchObj);
							
							transaction.commit();
							
							session.close();
							
							
						}
					}
				}
			}
			
			
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return  null;
	}

	private Boolean checkMatches(String team1, String team2) {
		String[] team = new String[] {"New Zealand","Sri Lanka","Afghanistan","Australia","India","South Africa","Bangladesh","England","West Indies","Pakistan"};
		Boolean check = null;
		check = Arrays.asList(team).contains(team1);
		check = Arrays.asList(team).contains(team2);
		return check;
	}

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
	
	

}
