//$Id$
package com.cric11.user;

import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Configuration;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cric11.authentication.JWTClass;
import com.cric11.hibernate.HibernateUtil;
import com.cric11.utility.Cric11Constants;
import com.cric11.utility.Cric11Util;
import com.opensymphony.xwork2.ActionSupport;

public class UserRegistration extends ActionSupport implements ServletRequestAware,ServletResponseAware {
	
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
	
	private static final Validator validator;

    static {
        Configuration<?> config = Validation.byDefaultProvider().configure();
        ValidatorFactory factory = config.buildValidatorFactory();
        validator = factory.getValidator();
        factory.close();
    }

	@Override
	public String execute() throws Exception {
		JSONObject userRegistrationResponse = null;
		try {
			String email_address = request.getHeader(UserConstants.EMAIL_ADDRESS);
						
			String password = request.getHeader(UserConstants.PASSWORD_DATA);
			
			if(email_address!=null) {
			
				Users userObj = new Users();
				userObj.setEmailAddress(email_address);
				userObj.setPassword(password);
				userObj.setIsEmailVerified(Boolean.FALSE);
				userObj.setIsMobileVerified(Boolean.FALSE);
				//userObj.setUserName("mike");

				
				Set<ConstraintViolation<Users>> validationErrors = validator.validate(userObj);
				
				System.out.println(validationErrors);
				
				
				Session session = HibernateUtil.getSession();
				userObj.setUuid(Cric11Util.generateUniqueId());
				userObj.setResetId(Cric11Util.generateUniqueResetId());
				session.save(userObj);
				HibernateUtil.getTransaction().commit();
				
				Thread thread = new Thread() {
					@Override
					public void run() {
						VerifyUser.verifyUserEmail(userObj);
					}
					
				};
				thread.start();
								
				userRegistrationResponse = getJSONResponseFromUserObj(userObj);
				
			}
		}
		catch (Exception ex) {
			Throwable throw1 = ex.getCause();
			if(throw1 instanceof ConstraintViolationException) {
				userRegistrationResponse = new JSONObject();
				userRegistrationResponse.put(UserConstants.ERROR_MESSAGE, "EmailId already exists");
				userRegistrationResponse.put(Cric11Constants.SUCCESS, Boolean.FALSE);
			}
		}
		JSONArray userArray = new JSONArray().put(userRegistrationResponse);
		JSONObject responseObj = Cric11Util.updateMetaInfo(request, "user", userArray);
		Cric11Util.sendResponse(request, response, responseObj.toString());
		return null;
	}

	public static JSONObject getJSONResponseFromUserObj(Users userObj) {
		JSONObject userData = new JSONObject();
		try {
			userData.put(UserConstants.EMAIL_ADDRESS, userObj.getEmailAddress());
			userData.put(UserConstants.UUID, userObj.getUuid());
			userData.put(UserConstants.IS_EMAIL_VERIFIED, userObj.getIsEmailVerified());
			userData.put(UserConstants.IS_MOBILE_VERIFIED, userObj.getIsMobileVerified());
			HashMap<String, Object> tokenMap = JWTClass.generateJWTCode(userObj.getUuid());
			userData.put(UserConstants.AUTH_CODE, tokenMap.get("token"));
			userData.put(UserConstants.EXPIRY_TIME, tokenMap.get("login_time"));
			userData.put(Cric11Constants.SUCCESS, Boolean.TRUE);
		}
		catch(Exception ex) {
			
		}
		return userData;
	}

	
}
