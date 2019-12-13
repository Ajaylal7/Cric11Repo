//$Id$
package com.cric11.user;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import com.cric11.authentication.JWTClass;
import com.cric11.hibernate.HibernateUtil;
import com.cric11.utility.CachedData;
import com.cric11.utility.Cric11Constants;
import com.cric11.utility.Cric11Util;
import com.opensymphony.xwork2.ActionSupport;

public class UserLogin extends ActionSupport implements ServletRequestAware,ServletResponseAware {

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
		JSONObject userLoginObj = new JSONObject();
		try {
			String email_address = request.getHeader(UserConstants.EMAIL_ADDRESS);
			
			String password = request.getHeader(UserConstants.PASSWORD_DATA);
			
			Session session = CachedData.sessionFactory.openSession();
						
	        
	        List<Users> dataObj =  (List<Users>) HibernateUtil.getRowsByCriteria(Users.class, "emailAddress", email_address);
	        if(dataObj.size() > 0) {
	        	Users users = dataObj.get(0);
	        	System.out.println(users.getUuid());
	        	Boolean checkPassword = Cric11Util.checkPassword(password, users.getPassword());
	        	if(checkPassword) {
	        		userLoginObj = UserRegistration.getJSONResponseFromUserObj(users);
	        	}
	        	else {
	        		userLoginObj.put(UserConstants.ERROR_MESSAGE, "Password does not match");
	        		userLoginObj.put(Cric11Constants.SUCCESS, Boolean.FALSE);
	        	}
	        }
	        else {
	        	userLoginObj.put(UserConstants.ERROR_MESSAGE, "EmailId does not exists");
	        	userLoginObj.put(Cric11Constants.SUCCESS, Boolean.FALSE);
    		}
	        JSONArray userArray = new JSONArray().put(userLoginObj);
			JSONObject responseObj = Cric11Util.updateMetaInfo(request, "user", userArray);
			Cric11Util.sendResponse(request, response, responseObj.toString());
			session.close();

		}
		catch (Exception ex) {
			System.out.println(ex);
		}
		return null;
	}
	
	

}

