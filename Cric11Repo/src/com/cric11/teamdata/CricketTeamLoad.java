//$Id$
package com.cric11.teamdata;

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

public class CricketTeamLoad extends ActionSupport implements ServletRequestAware,ServletResponseAware {

	@Override
	public String execute() throws Exception {
		String teamListUrl = "https://www.my11circle.com/api/lobbyApi/v1/getMatches";
		try {
			HashMap<String, String> headers = new HashMap<>();
			headers.put("Cookie", "SSID=SSID602800ff-5e44-4255-a2f0-d8e695f89247");
			JSONObject jsonBody = new JSONObject();
			jsonBody.put("sportsType", "1");
			String teamData = Cric11Util.getResponsefromUrl(teamListUrl, "post", headers, null, jsonBody);
			JSONArray jsonData = new JSONObject(teamData).getJSONObject("matches").getJSONArray("1");
			for(int index = 0; index<jsonData.length(); index++) {
				JSONObject team = jsonData.getJSONObject(index);
				JSONObject team1 = team.getJSONObject("team1");
				JSONObject team2 = team.getJSONObject("team2");
				
				Session session = CachedData.sessionFactory.openSession();
				Transaction transaction = session.beginTransaction();
				
				try {
				
				Team teamObj = new Team();
				teamObj.setTeamName(team1.get("name").toString());
				teamObj.setTeamDisplayName(team1.get("dName").toString());
				teamObj.setTeamImageUrl(team1.get("teamFlagURL").toString());
				session.save(teamObj);
				transaction.commit();
				}
				catch (Exception e) {
					// TODO: handle exception
				}

				try {
				transaction = session.beginTransaction();
				Team teamObj = new Team();
				teamObj.setTeamName(team2.get("name").toString());
				teamObj.setTeamDisplayName(team2.get("dName").toString());
				teamObj.setTeamImageUrl(team2.get("teamFlagURL").toString());
				session.save(teamObj);

				transaction.commit();
				
				}
				catch (Exception e) {
					// TODO: handle exception
				}
				session.close();
				
			}
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		
	}
	
	

}
