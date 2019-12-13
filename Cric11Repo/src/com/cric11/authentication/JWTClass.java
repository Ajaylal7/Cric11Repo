//$Id$
package com.cric11.authentication;

import java.sql.Date;
import java.util.HashMap;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.opensymphony.xwork2.ActionSupport;

public class JWTClass extends ActionSupport {
	
	@Override
	public String execute() throws Exception {

		return null;
	}

	public static HashMap<String, Object> generateJWTCode(String uuid) {
		String token = null;
		HashMap<String, Object> tokenMap = new HashMap<>();
		try {
			Date date = new Date(System.currentTimeMillis());
			Algorithm algorithm = Algorithm.HMAC256("secret");
			token = JWT.create().withIssuer(uuid).withIssuedAt(date).sign(algorithm);
			tokenMap.put("token", token);
			tokenMap.put("login_time",date.getTime());
		}
		catch (Exception ex) {
			
		}
		return tokenMap;
	}

}
