//$Id$
package com.cric11.sample;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cric11.hibernate.HibernateUtil;
import com.opensymphony.xwork2.ActionSupport;

public class SampleJSON extends ActionSupport implements ServletRequestAware,ServletResponseAware {
	
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

	@Override
	public String execute() throws Exception {
		try {
			String string;
			
			int i = 0;
			
			JSONArray arrayData = new JSONArray();
			
			File file = new File("/Users/ajay-6726/Downloads/logs.json");
			
			BufferedReader buffer = new BufferedReader(new FileReader(file));
			
			Session session = HibernateUtil.getSession();
			
			StringBuilder st = new StringBuilder(); 
			  while ((string = buffer.readLine()) != null) {
				  if(i%2!=0) {
					  JSONObject obj =new JSONObject(string);
					  Sample sample = new Sample();
					  sample.setTime(obj.get("utc_time").toString());
					  sample.setAgent(obj.get("agent").toString());
					  sample.setResponse(obj.get("response").toString());
					  sample.setExtension(obj.get("extension").toString());
					  sample.setIp(obj.get("ip").toString());
					  sample.setClientip(obj.get("clientip").toString());
					  sample.setUrl(obj.get("url").toString());
					  session.save(sample);
				  }
				  i++;
			  } 
			  HibernateUtil.getTransaction().commit();
			 
			 System.out.println(arrayData.length());
		
		}
		catch (Exception ex) {
			System.out.println(ex);
		}
		return null;
	}
	
	public String checkTime() throws Exception {
		String id = request.getHeader("id");
		String time = SampleFetch.fetchData(id);
		PrintWriter writer = response.getWriter();
		writer.write(time);
		return null;
	}
	

}
