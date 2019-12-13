//$Id$
package com.cric11.user;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class UserAuthFilter implements Filter {

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		String token = req.getHeader("authorization");
		JSONObject jsonObject = new JSONObject();
		try {
		
		DecodedJWT decoded = JWT.decode(token);
		jsonObject.put("iss", decoded.getIssuer());
		jsonObject.put("iat", decoded.getIssuedAt());
		
		Algorithm algorithm = Algorithm.HMAC256("secret");
	    JWTVerifier verifier = JWT.require(algorithm)
	        .withIssuer(decoded.getIssuer()).acceptIssuedAt(decoded.getIssuedAt().getTime())
	        .build(); //Reusable verifier instance
		    try {
		    DecodedJWT jwt = verifier.verify(token);
		    chain.doFilter(request, response);
		    }
		    catch (Exception ex) {
				PrintWriter writer = response.getWriter();
				writer.write("400 Unauthorized");
		    }
		}
		catch (Exception ex) {
			PrintWriter writer = response.getWriter();
			writer.write("400 Unauthorized");
			System.out.println(ex);
		}
	    //chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
