//$Id$
package com.cric11.user;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.cric11.hibernate.HibernateUtil;
import com.cric11.utility.Cric11Util;
import com.opensymphony.xwork2.ActionSupport;

public class UserEmailVerify extends ActionSupport implements ServletRequestAware,ServletResponseAware {
	
	private HttpServletRequest request;
	
	private HttpServletResponse response;
	
	private String resultUrl;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getResultUrl() {
		return resultUrl;
	}

	public void setResultUrl(String resultUrl) {
		this.resultUrl = resultUrl;
	}

	@Override
	public String execute() throws Exception {
		String scheme = request.getScheme();
		String host = request.getServerName();
		Integer port = request.getServerPort();
		String param = request.getParameter("token");
		String url = scheme + "://" + host + ":" + port+"/";
		if(param!= null) {
			String resetId = param;
			List<Users> userObj = (List<Users>) HibernateUtil.getRowsByCriteria(Users.class, "resetId", resetId);
			if(userObj.size()>0) {
				Users user = userObj.get(0);
				String resetData = user.getResetId();
				if(resetId.equals(resetData)) {
					user.setIsEmailVerified(Boolean.TRUE);
					user.setResetId(Cric11Util.generateUniqueResetId());
					HibernateUtil.getSession().saveOrUpdate(user);
					HibernateUtil.getTransaction().commit();
					url +="emailverify.jsp";
				}
			}
			else {
				url +="error.jsp";
			}
		}
		else {
			url +="error.jsp";
		}
		setResultUrl(url);
		return "success";
	}
	
	

}
