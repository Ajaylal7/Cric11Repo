//$Id$
package com.cric11.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import com.cric11.user.UserConstants;
import com.cric11.user.Users;

public class Cric11Util {
	
	public static Logger LOGGER = Logger.getLogger(Cric11Util.class.getName());
	
	public static String hashPassword(String password) {
		String hashedPassword = null;
		
		hashedPassword = BCrypt.hashpw(password,BCrypt.gensalt());
		
		return hashedPassword;
	}
	
	public static Boolean checkPassword(String password,String hash) {
		Boolean check = false;
		
		check = BCrypt.checkpw(password, hash);
		
		return check;
	}
	
	public static String generateUniqueId() {
		Boolean uniqueIdGenerated = false;
		String privaterKey=null;
		while(!uniqueIdGenerated)
		{
			privaterKey = UUID.randomUUID().toString();
			privaterKey = privaterKey.replace("-", "");
			Session session = CachedData.sessionFactory.openSession();
			Criteria criteria = session.createCriteria(Users.class);
			criteria.add(Restrictions.eq(UserConstants.UUID, privaterKey));
			if(criteria.list().size()==0) {
				uniqueIdGenerated = true;
			}
		}
		return privaterKey;
		
	}
	
	public static String generateUniqueResetId() {
		Boolean uniqueIdGenerated = false;
		String privaterKey=null;
		while(!uniqueIdGenerated)
		{
			privaterKey = UUID.randomUUID().toString();
			privaterKey = privaterKey.replace("-", "");
			Session session = CachedData.sessionFactory.openSession();
			Criteria criteria = session.createCriteria(Users.class);
			criteria.add(Restrictions.eq("resetId", privaterKey));
			if(criteria.list().size()==0) {
				uniqueIdGenerated = true;
			}
		}
		return privaterKey;
		
	}
	
	public static void sendResponse(HttpServletRequest request, HttpServletResponse response, String responseString) throws IOException
	{
		response.setContentType("application/json; charset=UTF-8");
		response.addHeader("Access-Control-Allow-Origin", "*");
		response.addHeader("Access-Control-Allow-Headers", "*");
		PrintWriter writer = response.getWriter();
		writer.println(responseString); //NO OUTPUTENCODING
	    writer.flush();
	}
	
	public static JSONObject updateMetaInfo(HttpServletRequest request, String moduleName, JSONArray array) {
		JSONObject mainObj = new JSONObject();
		try {
			Integer count = array.length();
			int successCount = 0;
			for(int index = 0;index<count; index++) {
				JSONObject json = array.getJSONObject(index);
				if(json.has(Cric11Constants.SUCCESS)&&json.getBoolean(Cric11Constants.SUCCESS))
				{
					successCount++;
				}
			}
			if(successCount == count) {
				mainObj.put(Cric11Constants.STATUS_CODE, Cric11Constants.ErrorMessages.API_ALL_SUCCESS.getErrorCode());
				mainObj.put(Cric11Constants.STATUS_STRING, Cric11Constants.ErrorMessages.API_ALL_SUCCESS.getErrorString());
			}
			else {
				mainObj.put(Cric11Constants.STATUS_CODE, Cric11Constants.ErrorMessages.API_ALL_FAILURE.getErrorCode());
				mainObj.put(Cric11Constants.STATUS_STRING, Cric11Constants.ErrorMessages.API_ALL_FAILURE.getErrorString());
			}
			
			Long startTime = (Long)request.getAttribute("RequestStartTime");
			if(startTime!=null)
			{
				Long endTime = System.currentTimeMillis();
				mainObj.put(Cric11Constants.TIME_TAKEN, endTime-startTime+" ms");
				//LOGGER.log(Level.INFO, "Request ends - End Time:"+endTime+", Total time taken:"+(endTime-startTime)+"ms");
			}
			mainObj.put(moduleName, array);
		}
		catch (Exception ex) {
			// TODO: handle exception
		}
		return mainObj;
	}
	
	public static String getResponsefromUrl(String url,String method,HashMap<String, String> headers, HashMap<String, String> params,JSONObject body) {
		String response = null;
		HttpURLConnection httpcon = null;
		String requestParams = "";
		try {
			URL httpurl = new URL(url);
			InputStream ins;
			httpcon = (HttpURLConnection) httpurl.openConnection();
			if(headers!=null) {
				for(Map.Entry<String, String> header:headers.entrySet()) {
					httpcon.setRequestProperty(header.getKey(), header.getValue());
				}
			}
			if(params!=null) {
				int len = 1;
				for(Map.Entry<String, String> entry:params.entrySet()) {
					requestParams+=entry.getKey()+"="+entry.getValue();
					if(len<params.size()) {
						requestParams+="&";
					}
					len++;
				}
			}
			if(method.equals("post")) {
				httpcon.setRequestMethod("POST");		
				httpcon.setDoOutput(true);
				httpcon.setDoInput(true);
				//httpcon.setRequestProperty("Content-Type", "application/json");		// NO I18N
				httpcon.setRequestProperty("Connection", "Keep-Alive");	//No I18N
			}
			else if(method.equals("get")) {
				httpcon.setRequestMethod("GET");		
				//httpcon.setDoInput(true);
				httpcon.setRequestProperty("Connection", "Keep-Alive");	//No I18N
				if(requestParams!="") {
					url = url+"?"+requestParams;
					httpurl = new URL(url);
				}
			}
			else if(method.equals("put")) {
				httpcon.setRequestMethod("PUT");		
				httpcon.setDoOutput(true);
				httpcon.setDoInput(true);
				httpcon.setRequestProperty("Connection", "Keep-Alive");	//No I18N
			}
			else if(method.equals("delete")) {
				httpcon.setRequestMethod("DELETE");		
				httpcon.setDoOutput(true);
				httpcon.setDoInput(true);
				httpcon.setRequestProperty("Connection", "Keep-Alive");	//No I18N
			}
			if(method.equals("post")||method.equals("put")||method.equals("delete")) {
				OutputStream os = httpcon.getOutputStream();
				if(requestParams!="") {
					os.write(requestParams.getBytes("UTF-8"));          //No I18N
				}
				else if(body!=null){
					os.write(body.toString().getBytes("UTF-8"));          //No I18N
				}
			}
			 httpcon.setConnectTimeout(120000);
			 int responseCode = httpcon.getResponseCode();
		        if(responseCode == 200||responseCode == 201){
		        	httpcon.connect();
		            ins = httpcon.getInputStream();
		        } else{
		            ins = httpcon.getErrorStream();
		        }
		      response = readContents(ins);
		}
		catch(Exception ex) {
			LOGGER.log(Level.SEVERE, "Exception in fetching data:"+ex);
		}
		finally {
			if(httpcon !=null) {
				httpcon.disconnect();
			}
		}
		return response;
	}
	
	public static String readContents(InputStream in) throws IOException {
	      char[] buf = new char[500000]; //even number so should not cut off unicode bytes
	      //int navailable;
	      StringBuilder sb = new StringBuilder();
	      InputStreamReader isr = null;
	      try{ 
	          isr = new InputStreamReader(in, "UTF-8");//No internationalization
	          synchronized (in) {
	              int readBytes = 0;
	              while ((readBytes = isr.read(buf, 0, buf.length)) >= 0) {
	                  sb.append(buf, 0, readBytes);
	              }
	          }
	      }catch(Exception ex){
	          LOGGER.log(Level.SEVERE, "Exception in StreamUtil readContents method",ex);
	      }finally{
	          if(isr!=null){
	              isr.close();
	          }
	      }
	      return sb.toString();
	}
	
	public static Root<?> createCriteria(Class<?> className) {
		Root<?> root = null;
		try {
			Session session = CachedData.sessionFactory.openSession();
			CriteriaBuilder builder = session.getCriteriaBuilder();
	        CriteriaQuery<?> query = builder.createQuery(className);
	        root = query.from(className);
		}
		catch (Exception ex) {
			LOGGER.log(Level.SEVERE, "Exception in fetching criteria");
		}
        
        return root;
	}
	
}
